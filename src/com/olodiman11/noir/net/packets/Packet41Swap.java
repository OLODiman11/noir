package com.olodiman11.noir.net.packets;

public class Packet41Swap extends Packet{

	private int[][] coords;
	
	public Packet41Swap(String username, int[][] coords) {
		super(41);
		this.username = username;
		this.coords = coords;
	}
	
	public Packet41Swap(byte[] data) {
		super(41);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		coords = new int[dataArray.length - 1][2];
		for(int i = 0; i < dataArray.length - 1; i++) {
			String[] coords = dataArray[i + 1].split(",");
			this.coords[i][0] = Integer.parseInt(coords[0]);
			this.coords[i][1] = Integer.parseInt(coords[1]);
		}
	}

	@Override
	public byte[] getData() {
		String data = "";
		for(int i = 0; i < coords.length; i++) {
			data += String.valueOf(coords[i][0]) + ",";
			if(i == coords.length - 1) {
				data += String.valueOf(coords[i][1]);
			} else {				
				data += String.valueOf(coords[i][1]) + ";";
			}
		}
		return ("41" + username + ";" + data + "~/~").getBytes();
	}

	public int[][] getCoords() {
		return coords;
	}
	
}
