package com.olodiman11.noir.net.packets;

public class Packet66Officers extends Packet {

	private int[] ints;
	
	public Packet66Officers(String username, int[] ints) {
		super(66);
		this.username = username;
		this.ints = ints;
	}
	
	public Packet66Officers(byte[] data) {
		super(66);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		String[] strInts = dataArray[1].split(",");
		ints = new int[strInts.length];
		for(int i = 0; i < ints.length; i++) {
			ints[i] = Integer.parseInt(strInts[i]);
		}
	}

	@Override
	public byte[] getData() {
		String strInts = "";
		for(int i = 0; i < ints.length; i++) {
			if(i == ints.length - 1) {
				strInts += String.valueOf(ints[i]);
			} else {				
				strInts += String.valueOf(ints[i]) + ",";
			}
		}
		return ("66" + username + ";" + strInts + "~/~").getBytes();
	}

	public int[] getInts() {
		return ints;
	}

}
