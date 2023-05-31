package com.olodiman11.noir.net.packets;

public class Packet67Shuffle extends Packet{

	private int[][] cards;
	
	public Packet67Shuffle(String username, int[][] cards) {
		super(67);
		this.username = username;
		this.cards = cards;
	}
	
	public Packet67Shuffle(byte[] data) {
		super(67);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		cards = new int[dataArray.length - 1][2];
		for(int i = 0; i < cards.length; i++) {
			cards[i][0] = Integer.parseInt(dataArray[i + 1].split(",")[0]);
			cards[i][1] = Integer.parseInt(dataArray[i + 1].split(",")[1]);
		}
	}

	@Override
	public byte[] getData() {
		String data = "";
		for(int i = 0; i < cards.length; i++) {
			data += String.valueOf(cards[i][0]) + ",";
			if(i == cards.length - 1) {				
				data += String.valueOf(cards[i][1]);
			} else {				
				data += String.valueOf(cards[i][1]) + ";";
			}
		}
		return ("67" + username + ";" + data + "~/~").getBytes();
	}

	public int[][] getCards() {
		return cards;
	}

}
