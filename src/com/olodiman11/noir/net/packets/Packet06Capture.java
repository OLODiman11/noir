package com.olodiman11.noir.net.packets;

public class Packet06Capture extends Packet{
	
	private int row, col;

	public Packet06Capture(String username, int row, int col) {
		super(06);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet06Capture(byte[] data) {
		super(06);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("06" + username + ";" + row + ";" + col + "~/~").getBytes();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
