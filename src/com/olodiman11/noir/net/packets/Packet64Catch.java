package com.olodiman11.noir.net.packets;

public class Packet64Catch extends Packet{

	private int col, row;
	
	public Packet64Catch(String username, int row, int col) {
		super(64);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet64Catch(byte[] data) {
		super(64);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("64" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

}
