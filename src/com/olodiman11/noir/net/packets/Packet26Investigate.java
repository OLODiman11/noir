package com.olodiman11.noir.net.packets;

public class Packet26Investigate extends Packet{

	private int row, col;
	
	public Packet26Investigate(String username, int row, int col) {
		super(26);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet26Investigate(byte[] data) {
		super(26);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("26" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
