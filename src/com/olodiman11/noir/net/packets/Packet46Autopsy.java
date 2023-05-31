package com.olodiman11.noir.net.packets;

public class Packet46Autopsy extends Packet{

	private int row, col;
	
	public Packet46Autopsy(String username, int row, int col) {
		super(46);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet46Autopsy(byte[] data) {
		super(46);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("46" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
