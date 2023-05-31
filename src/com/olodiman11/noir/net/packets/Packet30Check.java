package com.olodiman11.noir.net.packets;

public class Packet30Check extends Packet{

	private boolean check;
	private String key;
	
	public Packet30Check(String username, String key,  boolean check) {
		super(30);
		this.username = username;
		this.check = check;
		this.key = key;
	}
	
	public Packet30Check(byte[] data) {
		super(30);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		key = dataArray[1];
		check = Boolean.parseBoolean(dataArray[2]);
		}

	@Override
	public byte[] getData() {
		return ("30" + username + ";" + key + ";" + String.valueOf(check) + "~/~").getBytes();
	}

	public boolean isCheck() {
		return check;
	}

	public String getKey() {
		return key;
	}

}
