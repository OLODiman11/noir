package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet20Identity extends Packet {

	private String name;
	private int index;
	
	public Packet20Identity(String username, String name, int index) {
		super(20);
		this.username = username;
		this.name = name;
		this.index = index;
	}
	
	public Packet20Identity(byte[] data) {
		super(20);
		System.out.println(new String(data));
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		name = dataArray[1];
		index = Integer.parseInt(dataArray[2]);
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
		return ("20" + username + ";" + name + ";" + String.valueOf(index) + "~/~").getBytes();
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
	
}
