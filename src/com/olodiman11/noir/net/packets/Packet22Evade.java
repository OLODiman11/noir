package com.olodiman11.noir.net.packets;

public class Packet22Evade extends Packet{

	public Packet22Evade(String username) {
		super(22);
		this.username = username;
	}
	
	public Packet22Evade(byte[] data) {
		super(22);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("22" + username + "~/~").getBytes();
	}

}
