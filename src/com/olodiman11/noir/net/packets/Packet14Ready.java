package com.olodiman11.noir.net.packets;

public class Packet14Ready extends Packet{

	public Packet14Ready(String username) {
		super(14);
		this.username = username;
	}

	public Packet14Ready(byte[] data) {
		super(14);
		username = readData(data);
	}

	@Override
	public byte[] getData() {
		return ("14" + username + "~/~").getBytes();
	}

}
