package com.olodiman11.noir.net.packets;

public class Packet17Accuse extends Packet{

	private int col, row;
	
	public Packet17Accuse(String username, int row, int col) {
		super(17);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet17Accuse(byte[] data) {
		super(17);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public byte[] getData() {
		return ("17" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

}
