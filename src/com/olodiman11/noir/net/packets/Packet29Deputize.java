package com.olodiman11.noir.net.packets;

public class Packet29Deputize extends Packet{

	private int index;
	
	public Packet29Deputize(String username, int index) {
		super(29);
		this.username = username;
		this.index = index;
	}
	
	public Packet29Deputize(byte[] data) {
		super(29);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("29" + username + ";" + String.valueOf(index) +  "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}
	
}
