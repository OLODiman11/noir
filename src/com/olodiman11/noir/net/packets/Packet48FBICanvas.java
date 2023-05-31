package com.olodiman11.noir.net.packets;

public class Packet48FBICanvas extends Packet{

	private int index;
	
	public Packet48FBICanvas(String username, int index) {
		super(48);
		this.username = username;
		this.index = index;
	}
	
	public Packet48FBICanvas(byte[] data) {
		super(48);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("48" + username + ";" + index + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}	
	
}
