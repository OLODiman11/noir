package com.olodiman11.noir.net.packets;

public class Packet24Defend extends Packet{

	private int index;
	
	public Packet24Defend(String username, int index) {
		super(24);
		this.username = username;
		this.index = index;
	}
	
	public Packet24Defend(byte[] data) {
		super(24);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("24" + username + ";" + String.valueOf(index) +  "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}
	
}
