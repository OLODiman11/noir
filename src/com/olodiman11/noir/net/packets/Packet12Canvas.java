package com.olodiman11.noir.net.packets;

public class Packet12Canvas extends Packet{

	private int col, row;
	
	public Packet12Canvas(String username, int row, int col) {
		super(12);
		this.username = username;
		this.col = col;
		this.row = row;
	}
	
	public Packet12Canvas(byte[] data) {
		super(12);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("12" + username + ";" + row + ";" + col + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
	
	
	
}
