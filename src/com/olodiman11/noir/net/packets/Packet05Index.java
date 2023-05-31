package com.olodiman11.noir.net.packets;

public class Packet05Index extends Packet{

	private String[] usernames;
	private int[] indexes;
	
	public Packet05Index(String username, String[] usernames, int[] indexes) {
		super(05);
		this.username = username;
		this.usernames = usernames;
		this.indexes = indexes;
	}
	
	public Packet05Index(byte[] data) {
		super(05);
		String[] dataArray = readData(data).split(";");
		for(String s: dataArray) {
			System.out.println(s + " " + dataArray.length);	
		}
		username = dataArray[0];
		usernames = dataArray[1].split(",");
		indexes = new int[usernames.length];
		String[] indexes = dataArray[2].split(",");
		for(int i = 0; i < indexes.length; i++) {
			this.indexes[i] = Integer.parseInt(indexes[i]);
		}
	}

	public Packet05Index(String username, String usernames, int indexes) {
		super(05);
		this.username = username;
		this.usernames = new String[] {usernames};
		this.indexes = new int[] {indexes};
	}

	@Override
	public byte[] getData() {
		String usernames = "";
		String indexes = "";
		for(int i = 0; i < this.usernames.length; i++) {
			if(i == this.usernames.length - 1) {
				usernames += this.usernames[i];
				indexes += String.valueOf(this.indexes[i]);
				continue;
			}
			usernames += this.usernames[i] + ",";
			indexes += String.valueOf(this.indexes[i]) + ",";
		}
		return ("05" + username + ";" + usernames + ";" + indexes + "~/~").getBytes();
	}

	public String[] getUsernames() {
		return usernames;
	}
	
	public int[] getIndexes() {
		return indexes;
	}

}
