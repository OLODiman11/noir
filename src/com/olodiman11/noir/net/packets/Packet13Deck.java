package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet13Deck extends Packet{

	private int[] deck;
	
	public Packet13Deck(String username, int[] deck) {
		super(13);
		this.username = username;
		this.deck = deck;
	}
	
	public Packet13Deck(byte[] data) {
		super(13);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		String[] strDeck = dataArray[1].split(",");
		deck = new int[strDeck.length];
		for(int i = 0; i < strDeck.length; i++) {
			deck[i] = Integer.parseInt(strDeck[i]);
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
		String deck = "";
		for(int i = 0; i < this.deck.length; i++) {
			if(i == this.deck.length - 1) {
				deck += String.valueOf(this.deck[i]);
				continue;
			}
			deck += String.valueOf(this.deck[i]) + ",";
		}
		return ("13" + username + ";" + deck + "~/~").getBytes();
	}

	public int[] getDeck() {
		return deck;
	}
	
}
