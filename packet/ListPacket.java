package packet;

public class ListPacket extends Packet{
	
	// incoming constructor
	public String i_username;
	public ListPacket(String[] rawData) {
		super(rawData);
		
		i_username = getData(1);
	}
	
	// outgoing constructor
	private String o_username;
	public ListPacket(String username) {
		super(PacketType.LIST);
		this.o_username = username;
	}

	@Override
	protected void indexOutgoingData() {
		addData(o_username);
	}
}
