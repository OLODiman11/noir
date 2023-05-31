package com.olodiman11.noir;

import com.dosse.upnp.UPnP;

public class ShutDownHook extends Thread {

	private int port;
	
	public ShutDownHook(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		UPnP.closePortTCP(port);
		System.out.println("removed: " + port);
	}
	
}
