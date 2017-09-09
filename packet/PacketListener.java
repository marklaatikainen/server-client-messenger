package packet;

import java.net.Socket;

public interface PacketListener {
	
	public void packetSent(Packet packet, Socket client);
	public void packetReceived(Packet packet, Socket client);
	public void packetUpdate(Packet packet, Socket client);
	
}
