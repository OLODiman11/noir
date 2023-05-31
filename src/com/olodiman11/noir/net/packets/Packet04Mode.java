package com.olodiman11.noir.net.packets;

public class Packet04Mode extends Packet{

	private int index;
	
	public Packet04Mode(String username, int index) {
		super(04);
		this.username = username;
		this.index = index;
	}
	
	public Packet04Mode(byte[] data) {
		super(04);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("04" + username + ";" + index + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
