package com.olodiman11.noir.net.packets;

public class Packet18Role extends Packet{

	private String role, name;
	
	public Packet18Role(String username, String name, String role) {
		super(18);
		this.username = username;
		this.name = name;
		this.role = role;
	}
	
	public Packet18Role(byte[] data) {
		super(18);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		name = dataArray[1];
		role = dataArray[2];
	}

	@Override
	public byte[] getData() {
		return ("18" + username + ";" + name + ";" + role + "~/~").getBytes();
	}

	public String getRole() {
		return role;
	}

	public String getName() {
		return name;
	}

}
