package com.olodiman11.noir.net.packets;

public class Packet35Disguise extends Packet{

	public Packet35Disguise(String username) {
		super(35);
		this.username = username;
	}
	
	public Packet35Disguise(byte[] data) {
		super(35);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("35" + username + "~/~").getBytes();
	}

}
