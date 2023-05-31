package com.olodiman11.noir;

import com.olodiman11.noir.gfx.Assets;

public class Launcher {
	
	public static void main(String[] args) {
		
		// Preload Screen
		Preload preload = new Preload();
		Assets assets = new Assets();
		
		// Running the Game
		Game game = new Game("Noir", assets);
		game.start();
		
		// Hiding Preload
		preload.setVisible(false);
		
		// Displaying Game Window
		game.getWindow().getFrame().setOpacity(1f);
		game.getWindow().getFrame().requestFocus();
		
	}
	
}
