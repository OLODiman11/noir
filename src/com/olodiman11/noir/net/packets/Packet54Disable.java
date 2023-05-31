package com.olodiman11.noir.net.packets;

public class Packet54Disable extends Packet{

	private int row, col;
	
	public Packet54Disable(String username, int row, int col) {
		super(54);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet54Disable(byte[] data) {
		super(54);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("54" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
