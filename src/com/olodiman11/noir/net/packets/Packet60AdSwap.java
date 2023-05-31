package com.olodiman11.noir.net.packets;

public class Packet60AdSwap extends Packet{

	private int row, col;
	
	public Packet60AdSwap(String username, int row, int col) {
		super(60);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet60AdSwap(byte[] data) {
		super(60);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("60" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
