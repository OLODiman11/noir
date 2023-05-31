package com.olodiman11.noir.net.packets;

public class Packet62Safebreaking extends Packet{
	
	public Packet62Safebreaking(String username) {
		super(62);
		this.username = username;
	}
	
	public Packet62Safebreaking(byte[] data) {
		super(62);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
	}

	@Override
	public byte[] getData() {
		return ("62" + username + "~/~").getBytes();
	}

}
