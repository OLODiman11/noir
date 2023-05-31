package com.olodiman11.noir.net.packets;

public class Packet53Rob extends Packet{

	private int index;
	
	public Packet53Rob(String username, int index) {
		super(53);
		this.username = username;
		this.index = index;
	}
	
	public Packet53Rob(byte[] data) {
		super(53);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("53" + username + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
