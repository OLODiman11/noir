package com.olodiman11.noir.net.packets;

public class Packet55Vanish extends Packet{

	public Packet55Vanish(String username) {
		super(55);
		this.username = username;
	}
	
	public Packet55Vanish(byte[] data) {
		super(55);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("55" + username + "~/~").getBytes();
	}

}
