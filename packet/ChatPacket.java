package packet;

public class ChatPacket extends Packet {
	// incoming constructor
		public String i_username;
		public String i_message;
		public ChatPacket(String[] rawData) {
			super(rawData);
			i_username = getData(1);
			i_message = getData(2);

		}
		
		// outgoing constructor
		private String o_username;
		private String o_message;
		public ChatPacket(String message) {
			super(PacketType.CHAT);
			this.o_username = message;
			this.o_message = message;
		}

		@Override
		protected void indexOutgoingData() {
			addData(o_message);
		}
}
