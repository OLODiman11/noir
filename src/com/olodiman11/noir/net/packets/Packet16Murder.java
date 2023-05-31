package com.olodiman11.noir.net.packets;

public class Packet16Murder extends Packet{

	private int col, row;
	
	public Packet16Murder(String username, int row, int col) {
		super(16);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet16Murder(byte[] data) {
		super(16);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("16" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

}
