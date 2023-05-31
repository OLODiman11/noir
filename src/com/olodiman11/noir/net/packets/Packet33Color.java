package com.olodiman11.noir.net.packets;

public class Packet33Color extends Packet{

	private String color;
	
	public Packet33Color(String username, String color) {
		super(33);
		this.username = username;
		this.color = color;
	}
	
	public Packet33Color(byte[] data) {
		super(33);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		color = dataArray[1];
	}

	@Override
	public byte[] getData() {
		return ("33" + username + ";" + color + "~/~").getBytes();
	}

	public String getColor() {
		return color;
	}

}
