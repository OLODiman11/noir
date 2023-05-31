package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet07Collapse extends Packet{
	
	private boolean refRow;
	private int[] indexes;

	public Packet07Collapse(String username, boolean refRows, int[] indexes) {
		super(07);
		this.username = username;
		this.refRow = refRows;
		this.indexes = indexes;
	}
	
	public Packet07Collapse(byte[] data) {
		super(07);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		refRow = Boolean.parseBoolean(dataArray[1]);
		indexes = new int[dataArray.length - 2];
		for(int i = 0; i < indexes.length; i++) {
			indexes[i] = Integer.parseInt(dataArray[i+2]);
		}
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
		String message = "07" + username + ";" + refRow;
		for(Integer i: indexes) {
			message += ";" + String.valueOf(i);
		}
		message += "~/~";
		return (message.trim()).getBytes();
	}

	public boolean isRefRows() {
		return refRow;
	}

	public int[] getIndexes() {
		return indexes;
	}

}
