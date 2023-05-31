package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet61StealthyShift extends Packet{

	private int dir, rc;
	
	public Packet61StealthyShift(String username, int dir, int rc) {
		super(61);
		this.username = username;
		this.dir = dir;
		this.rc = rc;
	}
	
	public Packet61StealthyShift(byte[] data) {
		super(61);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		dir = Integer.parseInt(dataArray[1]);
		rc = Integer.parseInt(dataArray[2]);
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
		return ("61" + username + ";" + dir + ";" + rc + "~/~").getBytes();
	}

	public int getDir() {
		return dir;
	}

	public int getRC() {
		return rc;
	}

}
