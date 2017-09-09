package packet;

import java.util.ArrayList;
import java.util.List;

public abstract class Packet {
	
	//username;time;chat_message;
	private List<String> dataList = new ArrayList<String>();
	private PacketType packetType;
	
	
	// read the incoming data from the same packet
	public Packet(String[] rawData) {
		for(String segment : rawData) {
			dataList.add(segment);
		}
	}
	
	// outgoing packet constructor
	public Packet(PacketType type) {
		this.packetType = type;
	}

	protected String getData(int index) {
		return dataList.get(index);
	}
	
	// outgoing data methods
	protected void addData(String data) {
		addData(data, 0);
	}
	
	protected void addData(String data, int index) {
		dataList.add(index, data);
	}
	protected abstract void indexOutgoingData();
	
	public String getOutgoingData() {
		// request the child packets to create the raw data list to be sent to the client
		dataList.clear();
		indexOutgoingData();
		return compileOutgoingData();		
	}
	
	protected String compileOutgoingData() {
		StringBuffer buffer = new StringBuffer(packetType.name()).append(";");
		for(int i = dataList.size() - 1; i >= 0; i--) {
			String data = dataList.get(i);
			buffer.append(data).append(";");
		}
		return buffer.toString();
	}
	
	
}
