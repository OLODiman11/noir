package com.olodiman11.noir.net.packets;

public class Packet23Kill extends Packet{

	private int row, col;
	
	public Packet23Kill(String username, int row, int col) {
		super(23);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet23Kill(byte[] data) {
		super(23);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("23" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
