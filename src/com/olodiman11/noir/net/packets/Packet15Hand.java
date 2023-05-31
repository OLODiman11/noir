package com.olodiman11.noir.net.packets;

public class Packet15Hand extends Packet {
	
	private int[] indexes;

	public Packet15Hand(String username, int[] indexes) {
		super(15);
		this.username = username;
		this.indexes = indexes;
	}
	
	public Packet15Hand(byte[] data) {
		super(15);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		String[] strIndexes = dataArray[1].split(",");
		indexes = new int[strIndexes.length];
		for(int i = 0; i < strIndexes.length; i++) {
			indexes[i] = Integer.parseInt(strIndexes[i]);
		}
	}

	@Override
	public byte[] getData() {
		String strIndexes = "";
		for(int i = 0; i < indexes.length; i++) {
			if(i == indexes.length - 1) {
				strIndexes += String.valueOf(indexes[i]);
				continue;
			}
			strIndexes += String.valueOf(indexes[i]) + ",";
		}
		return ("15" + username + ";" + strIndexes + "~/~").getBytes();
	}

	public int[] getIndexes() {
		return indexes;
	}
	
}
