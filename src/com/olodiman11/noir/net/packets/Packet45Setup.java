package com.olodiman11.noir.net.packets;

public class Packet45Setup extends Packet{

	private int[][] coords;
	private int type;
	
	public Packet45Setup(String username, int type, int[][] coords) {
		super(45);
		this.username = username;
		this.type = type;
		this.coords = coords;
	}
	
	public Packet45Setup(byte[] data) {
		super(45);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		type = Integer.parseInt(dataArray[1]);
		coords = new int[dataArray.length - 2][2];
		for(int i = 0; i < dataArray.length - 2; i++) {
			String[] coords = dataArray[i + 2].split(",");
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
		return ("45" + username + ";" + String.valueOf(type) + ";" + data + "~/~").getBytes();
	}

	public int[][] getCoords() {
		return coords;
	}

	public int getType() {
		return type;
	}
	
}
