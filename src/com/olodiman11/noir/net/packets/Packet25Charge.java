package com.olodiman11.noir.net.packets;

public class Packet25Charge extends Packet{

	private int row, col;
	
	public Packet25Charge(String username, int row, int col) {
		super(25);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet25Charge(byte[] data) {
		super(25);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("25" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
