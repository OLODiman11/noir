package com.olodiman11.noir.net.packets;

public class Packet69NumRoll extends Packet {

	private int dir;
	private String key;
	
	public Packet69NumRoll(String username, String key, int dir) {
		super(69);
		this.username = username;
		this.key = key;
		this.dir = dir;
		
	}
	
	public Packet69NumRoll(byte[] data) {
		super(69);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		key = dataArray[1];
		dir = Integer.parseInt(dataArray[2]);
	}
	
	@Override
	public byte[] getData() {
		return ("69" + username + ";" + key + ";" + String.valueOf(dir) + "~/~").getBytes();
	}

	public int getDir() {
		return dir;
	}

	public String getKey() {
		return key;
	}
	
}
