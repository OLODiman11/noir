package com.olodiman11.noir.net.packets;

public class Packet37MKill extends Packet{

	private int row, col;
	
	public Packet37MKill(String username, int row, int col) {
		super(37);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet37MKill(byte[] data) {
		super(37);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("37" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
