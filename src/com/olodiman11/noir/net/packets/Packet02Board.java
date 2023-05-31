package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;
import com.olodiman11.noir.objects.Card;

public class Packet02Board extends Packet{

	private int numCols, numRows;
	private int[][] mapIndex;
	
	public Packet02Board(String username, Card[][] map, int numCols, int numRows) {
		super(02);
		mapIndex = new int[numRows][numCols];
		for(int col = 0; col < numCols; col++) {
			for(int row = 0; row < numRows; row++) {
				mapIndex[row][col] = map[row][col].getIndex();
			}
		}
		this.username = username;
		this.numCols = numCols;
		this.numRows = numRows;
	}
	
	public Packet02Board(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(";");
		String[] mapArray = dataArray[1].split(",");
		username = dataArray[0];
		numCols = Integer.parseInt(dataArray[2]);
		numRows = Integer.parseInt(dataArray[3]);
		mapIndex = new int[numRows][numCols];
		for(int col = 0; col < numCols; col++) {
			for(int row = 0; row < numRows; row++) {
				mapIndex[row][col] = Integer.parseInt(mapArray[row + col * numRows]);
			}
		}
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		String indexArray = "";
		for(int col = 0; col < numCols; col++) {
			for(int row = 0; row < numRows; row++) {
				if(col == numCols - 1 && row == numRows - 1) {
					indexArray += String.valueOf(mapIndex[row][col]);
					continue;
				}
				indexArray += String.valueOf(mapIndex[row][col]) + ",";
			}
		}
		return ("02" + username + ";" + indexArray + ";" + numCols + ";" + numRows + "~/~").getBytes();
	}

	public int getxSize() {
		return numCols;
	}

	public int getySize() {
		return numRows;
	}

	public int[][] getMapIndex() {
		return mapIndex;
	}

}
