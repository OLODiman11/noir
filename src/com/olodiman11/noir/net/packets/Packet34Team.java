package com.olodiman11.noir.net.packets;

public class Packet34Team extends Packet{

	private String team;
	
	public Packet34Team(String username, String team) {
		super(34);
		this.username = username;
		this.team = team;
	}
	
	public Packet34Team(byte[] data) {
		super(34);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		team = dataArray[1];
	}

	@Override
	public byte[] getData() {
		return ("34" + username + ";" + team + "~/~").getBytes();
	}

	public String getTeam() {
		return team;
	}

}
