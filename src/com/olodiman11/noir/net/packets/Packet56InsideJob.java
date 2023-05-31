package com.olodiman11.noir.net.packets;

public class Packet56InsideJob extends Packet{

	private int row, col;
	
	public Packet56InsideJob(String username, int row, int col) {
		super(56);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet56InsideJob(byte[] data) {
		super(56);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("56" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
