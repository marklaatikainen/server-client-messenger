package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import messengeri.Messengeri;
import packet.ChatPacket;
import packet.ConnectPacket;
import packet.Packet;
import packet.PacketDictionary;
import packet.PacketListener;
import packet.PacketType;
import packet.DisconnectPacket;
import packet.ListPacket;

public class NetworkServer implements PacketListener{
	private ServerSocket socket;
	private Socket newClient;
	private boolean running = false;
	private int port;
	private String usr;
	private List<PacketListener> packetListeners = new ArrayList<>();
	private Map<String, Socket> connectedClientMap = new HashMap<>();
	private Messengeri messengeri;
	
	public NetworkServer(Messengeri messengeri, int port) {
		this.port = port;
		this.messengeri = messengeri;
		
		addPacketListener(this);
	}
	
	private void addPacketListener(PacketListener listener) {
		packetListeners.add(listener);
	}

	public void startServer() {
		try {
			socket = new ServerSocket(port);
			Messengeri.getInstance().showMessage("Server socked initialized on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	new Thread(new Runnable() {
	@Override
		public void run() {
			Messengeri.getInstance().showMessage("Waiting for clients...");

			while(running) {
				try {
					newClient = socket.accept();
					Messengeri.getInstance().showMessage("Client has connected! " + newClient.getRemoteSocketAddress());
					
					// listen to client
					new Thread(new Runnable() {
						@Override
						public void run() {
							boolean error = false;
							while(!error && newClient.isConnected()) {
								try {
									DataInputStream input = new DataInputStream(newClient.getInputStream());
									String rawData = input.readUTF();
									String[] data = rawData.trim().split(";");
									PacketType type = PacketType.valueOf(data[0]);
									Packet packet = PacketDictionary.translatePacketType(type, data);
									broadcastPacketReceived(packet, newClient);
									usr = data[1];										
									
								} catch (EOFException e) {
									error = true;
									Messengeri.getInstance().showMessage(usr + " has disconnected! ");
								}
								
								 catch (IOException e) {
									error = true;
									e.printStackTrace();
								}
							}
							try {
								removeClient(newClient);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							Messengeri.getInstance().showMessage(usr + " has disconnected! ");
						}

					}).start();
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}).start();
		running = true;
	}
	
	
	

	private void broadcastPacketReceived(Packet packet, Socket client) {
		for(PacketListener packetListener : packetListeners) {
			packetListener.packetReceived(packet, client);
		}
		
	}
	
		
	public void stopServer() {
		running = false;
	}

	@Override
	public void packetSent(Packet packet, Socket client) {
	}
	

	@Override
	public void packetReceived(Packet packet, Socket client) {
		if(packet instanceof ConnectPacket) {
			try {
				connectClient((ConnectPacket) packet, client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(packet instanceof ChatPacket) {
			try {
				handleMessage((ChatPacket) packet, client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	// handle the message	
	private void handleMessage(ChatPacket packet, Socket client) throws IOException {
	// client wrote something
		Messengeri.getInstance().showMessage(packet.i_username + " - " + packet.i_message);
		
	// send client's message to all clients
		for (Entry<String, Socket> entry : connectedClientMap.entrySet()) {
			Socket value = entry.getValue();
		    sendMessage(packet.i_username + ";" + packet.i_message, value);
		}
	}

	
	public void sendMessage(String message, Socket toWhom) throws IOException {
			sendPacket(new ChatPacket(message), toWhom);
	}

	public void sendConnection(String message, Socket toWhom) throws IOException {
		    sendPacket(new ConnectPacket(message), toWhom);
	}

	public void sendList(String message, Socket toWhom) throws IOException {
	    sendPacket(new ListPacket(message), toWhom);
}

	
	public void sendPacket(Packet packet, Socket toWhom) throws IOException {
			Socket connectionSocket = toWhom;
			DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());
			output.writeUTF(packet.getOutgoingData());
	}
	

	private void connectClient(ConnectPacket packet, Socket client) throws IOException {
		if(connectedClientMap.get(packet.i_username) != null) {
			return;
		}		
		String allUsers = "";
		connectedClientMap.put(packet.i_username, client);
		for (Entry<String, Socket> entry : connectedClientMap.entrySet()) {
		    String key = entry.getKey();
		    allUsers += (key) + "@";
		}
		
		for (Entry<String, Socket> entry : connectedClientMap.entrySet()) {
		    Socket value = entry.getValue();
		    sendList(allUsers, value);
		    sendConnection(packet.i_username, value);
		    
		}
		
		Messengeri.getInstance().showMessage(packet.i_username + " has joined us!");
		messengeri.updateView();
	}

	
	private void removeClient(Socket client) throws IOException {
		for (String nickname : connectedClientMap.keySet()) {
			Socket socket = connectedClientMap.get(nickname);
			if(socket.equals(client)) {
				connectedClientMap.remove(nickname);
				
				for (Entry<String, Socket> entry : connectedClientMap.entrySet()) {
				    Socket value = entry.getValue();
				    String allUsers1 = "";
				    for (Entry<String, Socket> entry1 : connectedClientMap.entrySet()) {
					    String key = entry1.getKey();
					    allUsers1 += (key) + "@";
					}
				    sendList(allUsers1, value);				    
				    sendPacket(new DisconnectPacket(nickname), value);
				}
			}
		}
		messengeri.updateView();
	}

	
	public Map<String, Socket> getConnectedClientMap() {
		
		return connectedClientMap;
	}

	@Override
	public void packetUpdate(Packet packet, Socket client) {
	}	
}