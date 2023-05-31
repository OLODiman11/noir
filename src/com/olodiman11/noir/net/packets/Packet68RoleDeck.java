package com.olodiman11.noir.net.packets;

public class Packet68RoleDeck extends Packet {

	private int[] indexes;
	
	public Packet68RoleDeck(String username, int[] indexes) {
		super(68);
		this.username = username;
		this.indexes = indexes;
	}
	
	public Packet68RoleDeck(byte[] data) {
		super(68);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		String[] strInts = dataArray[1].split(",");
		indexes = new int[strInts.length];
		for(int i = 0; i < indexes.length; i++) {
			indexes[i] = Integer.parseInt(strInts[i]);
		}
	}

	@Override
	public byte[] getData() {
		String strInts = "";
		for(int i = 0; i < indexes.length; i++) {
			if(i == indexes.length - 1) {
				strInts += String.valueOf(indexes[i]);
			} else {				
				strInts += String.valueOf(indexes[i]) + ",";
			}
		}
		return ("68" + username + ";" + strInts + "~/~").getBytes();
	}

	public int[] getInts() {
		return indexes;
	}

}
