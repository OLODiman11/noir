package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet10Order extends Packet{
	
	private String playerName;
	private int lineNum;

	public Packet10Order(String username, String playerName, int lineNum) {
		super(10);
		this.username = username;
		this.playerName = playerName;
		this.lineNum = lineNum;
	}
	
	public Packet10Order(byte[] data) {
		super(10);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		playerName = dataArray[1];
		lineNum = Integer.parseInt(dataArray[2]);
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
		return ("10" + username + ";" + playerName + ";" + lineNum + "~/~").getBytes();
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getLineNum() {
		return lineNum;
	}

}
