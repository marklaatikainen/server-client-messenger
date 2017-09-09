package packet;

public class ConnectPacket extends Packet {
	
	// incoming constructor
	public String i_username;
	public ConnectPacket(String[] rawData) {
		super(rawData);
		
		i_username = getData(1);
	}
	
	// outgoing constructor
	private String o_username;
	public ConnectPacket(String username) {
		super(PacketType.CONNECT);
		this.o_username = username;
	}

	@Override
	protected void indexOutgoingData() {
		addData(o_username);
	}
	
}
