package com.olodiman11.noir.net.packets;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet01Disconnect extends Packet{

	InetAddress ipAddress;
	int port;
	
	public Packet01Disconnect(String username, InetAddress ipAddress, int port) {
		super(01);
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public Packet01Disconnect(byte[] data) {
		super(01);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		try {
			ipAddress = InetAddress.getByName(dataArray[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		port = Integer.parseInt(dataArray[2]);
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
		return ("01" + username + ";" + ipAddress.getHostAddress() + ";" + port + "~/~").getBytes();
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

}
