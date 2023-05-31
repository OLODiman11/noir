package com.olodiman11.noir.net.packets;

public class Packet65Surveillance extends Packet{

	private int col, row;
	
	public Packet65Surveillance(String username, int row, int col) {
		super(65);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet65Surveillance(byte[] data) {
		super(65);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("65" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

}
