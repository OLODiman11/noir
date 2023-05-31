package com.olodiman11.noir.net.packets;

public class Packet21Exonerate extends Packet {

	private int index;
	
	public Packet21Exonerate(String username, int index) {
		super(21);
		this.username = username;
		this.index = index;
	}
	
	public Packet21Exonerate(byte[] data) {
		super(21);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("21" + username + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
