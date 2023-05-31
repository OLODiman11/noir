package com.olodiman11.noir.net.packets;

import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Server;

public abstract class Packet {

	protected String username;
	
	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), BOARD(02), SHIFT(03), MODE(04), INDEX(05), CAPTURE(06), COLLAPSE(07),
		ORDER(10), ENDTURN(11), CANVAS(12), DECK(13), READY(14), HAND(15), MURDER(16), ACCUSE(17), ROLE(18), DRAW(19),
		IDENTITY(20), EXONERATE(21), EVADE(22), KILL(23), DEFEND(24), CHARGE(25), INVESTIGATE(26), STEAL(27), CHANGE(28),
		DEPUTIZE(29), CHECK(30), POINTS(31), USERNAME(32), COLOR(33), TEAM(34), DISGUISE(35), MDISGUISE(36), MKILL(37),
		THREAT(38), FBIACCUSE(39), DISARM(40), SWAP(41), BOMB(42), DETONATE(43), SNIPE(44), SETUP(45), AUTOPSY(46), FBIDISGUISE(47),
		FBICANVAS(48), PROTECT(49), PROFILE(50), MARKER(51), FASTSHIFT(52), ROB(53), DISABLE(54), VANISH(55), INSIDEJOB(56),
		HACK(57), SILENCE(58), DUPLICATE(59), ADSWAP(60), STEALTHYSHIFT(61), SAFEBREAKING(62), OFSWAP(63), CATCH(64), SURVEILLANCE(65),
		OFFICERS(66), SHUFFLE(67), ROLEDECK(68), NUMROLL(69);
		
		private int packetId;
		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}
	
	public void writeData(Client client) {
		client.sendData(getData());			
	}

	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}
	
	public String readData(byte[] data) {
		String message= new String(data).trim();
		return message.substring(2);
	}
	
	public abstract byte[] getData();
	
	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch(NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}
	
	public static PacketTypes lookupPacket(int id) {
		for(PacketTypes p: PacketTypes.values()) {
			if(p.getId() == id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}
	
	public String getUsername() {
		return username;
	}
	
}
