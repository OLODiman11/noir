package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet28Change extends Packet{

	private int index;
	
	public Packet28Change(String username, int index) {
		super(24);
		this.username = username;
		this.index = index;
	}
	
	public Packet28Change(byte[] data) {
		super(28);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		index = Integer.parseInt(dataArray[1]);
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
		return ("28" + username + ";" + String.valueOf(index) +  "~/~").getBytes();
	}

	public int getIndex() {
		return index;
	}

}
