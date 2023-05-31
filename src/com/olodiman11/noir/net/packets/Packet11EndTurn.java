package com.olodiman11.noir.net.packets;

public class Packet11EndTurn extends Packet{

	private int num;
	
	public Packet11EndTurn(String username, int num) {
		super(11);
		this.username = username;
		this.num = num;
	}
	
	public Packet11EndTurn(byte[] data) {
		super(11);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		num = Integer.parseInt(dataArray[1]);
	}

	@Override
	public byte[] getData() {
		return ("11" + username + ";" + num + "~/~").getBytes();
	}

	public int getNum() {
		return num;
	}

}
