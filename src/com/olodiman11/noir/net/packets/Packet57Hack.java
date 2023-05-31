package com.olodiman11.noir.net.packets;

public class Packet57Hack extends Packet{

	private int index;
	
	public Packet57Hack(String username, int index) {
		super(57);
		this.username = username;
		this.index = index;
	}
	
	public Packet57Hack(byte[] data) {
		super(57);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("57" + username + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
