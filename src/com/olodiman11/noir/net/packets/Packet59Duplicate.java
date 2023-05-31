package com.olodiman11.noir.net.packets;

public class Packet59Duplicate extends Packet{

	private int index;
	
	public Packet59Duplicate(String username, int index) {
		super(59);
		this.username = username;
		this.index = index;
	}
	
	public Packet59Duplicate(byte[] data) {
		super(59);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("59" + username + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
