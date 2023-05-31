package com.olodiman11.noir.net.packets;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public class Packet00Login extends Packet{
	
	private InetAddress ipAddress;
	private int port;
	
	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(";");
		username = dataArray[0];
		try {
			this.ipAddress = InetAddress.getByName(dataArray[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		port = Integer.parseInt(dataArray[2]);
	}
	
	public Packet00Login(String username, InetAddress ipAddress, int port) {
		super(00);
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
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
		return ("00" + username + ";" + ipAddress.getHostAddress() + ";" + port + "~/~").getBytes();
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

}
