package com.olodiman11.noir.net.packets;

public class Packet42Bomb extends Packet{

	private int row, col;
	
	public Packet42Bomb(String username, int row, int col) {
		super(42);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet42Bomb(byte[] data) {
		super(42);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("42" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
