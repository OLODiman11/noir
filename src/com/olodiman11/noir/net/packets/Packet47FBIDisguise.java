package com.olodiman11.noir.net.packets;

public class Packet47FBIDisguise extends Packet{

	public Packet47FBIDisguise(String username) {
		super(47);
		this.username = username;
	}
	
	public Packet47FBIDisguise(byte[] data) {
		super(47);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("47" + username + "~/~").getBytes();
	}

}
