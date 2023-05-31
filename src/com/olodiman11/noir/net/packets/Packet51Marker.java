package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet51Marker extends Packet{

	private int col, row;
	
	public Packet51Marker(String username, int row, int col) {
		super(51);
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public Packet51Marker(byte[] data) {
		super(51);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		row = Integer.parseInt(dataArray[1]);
		col = Integer.parseInt(dataArray[2]);
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("51" + username + ";" + String.valueOf(row) + ";" + String.valueOf(col) + "~/~").getBytes();
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

}
