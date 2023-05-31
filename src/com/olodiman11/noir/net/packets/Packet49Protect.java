package com.olodiman11.noir.net.packets;

public class Packet49Protect extends Packet{

	private int col, row;
	
	public Packet49Protect(String username, int row, int col) {
		super(49);
		this.username = username;
		this.col = col;
		this.row = row;
	}
	
	public Packet49Protect(byte[] data) {
		super(49);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("49" + username + ";" + row + ";" + col + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
	
	
	
}
