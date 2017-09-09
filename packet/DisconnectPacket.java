package packet;

public class DisconnectPacket extends Packet {
	// incoming constructor
	public String i_username;
	public DisconnectPacket(String[] rawData) {
		super(rawData);
		i_username = getData(1);

	}
	
	// outgoing constructor
	private String o_username;
	public DisconnectPacket(String username) {
		super(PacketType.DISCONNECT);
		this.o_username = username;
	}

	@Override
	protected void indexOutgoingData() {
		addData(o_username);
	}

}
