package com.olodiman11.noir.net.packets;

public class Packet27Steal extends Packet{

	private int row, col;
	
	public Packet27Steal(String username, int row, int col) {
		super(27);
		this.row = row;
		this.col = col;
		this.username = username;
	}
	
	public Packet27Steal(byte[] data) {
		super(27);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("27" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
}
