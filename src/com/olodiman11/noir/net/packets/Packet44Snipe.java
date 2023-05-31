package com.olodiman11.noir.net.packets;

public class Packet44Snipe extends Packet{

	private int row, col;
	
	public Packet44Snipe(String username, int row, int col) {
		super(44);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet44Snipe(byte[] data) {
		super(44);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("44" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
