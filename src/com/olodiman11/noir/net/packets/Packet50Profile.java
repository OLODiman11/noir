package com.olodiman11.noir.net.packets;

public class Packet50Profile extends Packet{

	private int index;
	
	public Packet50Profile(String username, int index) {
		super(50);
		this.username = username;
		this.index = index;
	}
	
	public Packet50Profile(byte[] data) {
		super(50);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("50" + username + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}
	
	
	
}
