package com.olodiman11.noir.net.packets;

public class Packet40Disarm extends Packet{

	private int row, col, type;
	
	public Packet40Disarm(String username, int row, int col, int type) {
		super(40);
		this.row = row;
		this.col = col;
		this.type = type;
		this.username = username;
	}
	
	public Packet40Disarm(byte[] data) {
		super(40);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
		type = Integer.parseInt(dataArray[3]);
	}

	@Override
	public byte[] getData() {
		return ("40" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + ";" + String.valueOf(type) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getType() {
		return type;
	}
	
}
