package com.olodiman11.noir.net.packets;

public class Packet36MDisguise extends Packet{

	public Packet36MDisguise(String username) {
		super(36);
		this.username = username;
	}
	
	public Packet36MDisguise(byte[] data) {
		super(36);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("36" + username + "~/~").getBytes();
	}

}
