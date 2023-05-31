package com.olodiman11.noir.net.packets;

public class Packet19Draw extends Packet{
	
	private String name;
	private boolean hand, choice, identity;
	private int amm;
	
	public Packet19Draw(byte[] data) {
		super(19);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		name = dataArray[1];
		hand = Boolean.parseBoolean(dataArray[2]);
		choice = Boolean.parseBoolean(dataArray[3]);
		identity = Boolean.parseBoolean(dataArray[4]);
		amm = Integer.parseInt(dataArray[5]);
	}
	
	public Packet19Draw(String username, String name, boolean hand, boolean choice, boolean identity, int amm) {
		super(19);
		this.username = username;
		this.name = name;
		this.hand = hand;
		this.choice = choice;
		this.identity = identity;
		this.amm = amm;
	}
	
	public Packet19Draw(String username, boolean hand, boolean choice, boolean identity, int amm) {
		this(username, username, hand, choice, identity, amm);
	}

	@Override
	public byte[] getData() {
		return ("19" + username + ";" + name + ";" + String.valueOf(hand) + ";" 
	+ String.valueOf(choice) + ";" + String.valueOf(identity) + ";"
				+ String.valueOf(amm) + "~/~").getBytes();
	}

	public boolean isHand() {
		return hand;
	}

	public boolean isChoice() {
		return choice;
	}

	public boolean isIdentity() {
		return identity;
	}

	public String getName() {
		return name;
	}

	public int getAmm() {
		return amm;
	}

}
