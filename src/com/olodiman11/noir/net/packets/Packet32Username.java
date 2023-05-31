package com.olodiman11.noir.net.packets;

public class Packet32Username extends Packet{

	private String newName;
	
	public Packet32Username(String username, String newName) {
		super(32);
		this.username = username;
		this.newName = newName;
	}
	
	public Packet32Username(byte[] data) {
		super(32);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		newName = dataArray[1];
	}

	@Override
	public byte[] getData() {
		return ("32" + username + ";" + newName + "~/~").getBytes();
	}

	public String getNewName() {
		return newName;
	}

}
