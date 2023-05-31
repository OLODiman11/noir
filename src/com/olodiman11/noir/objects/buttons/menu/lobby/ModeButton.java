package com.olodiman11.noir.objects.buttons.menu.lobby;

import java.awt.Graphics;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet04Mode;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState;

public class ModeButton extends Button{
	
	private gameModes mode;

	public ModeButton(Handler handler, double x, double y, gameModes mode) {
		super(handler, x, y);
		this.mode = mode;
		texture = handler.getAssets().getModeChoice()[mode.getIndex()];
		selectedImg = handler.getAssets().getSquareHighlight();
		width = texture.getWidth();
		height = texture.getHeight();
	}
	
	@Override
	public void render(Graphics g) {
		
		if(hovering) {
			g.drawImage(selectedImg, (int) (x - Math.floor(10 * Window.SCALE)), (int) (y - Math.floor(10 * Window.SCALE)),
					(int) (width + 20 * Window.SCALE), (int) (height + 20 * Window.SCALE), null); 
		}
		
		g.drawImage(texture, (int) x, (int) y, width, height, null);
		
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setMode(mode);
		handler.getMenuState().setMode(mode);
		Packet04Mode packet = new Packet04Mode(handler.getPlayer().getUsername(), mode.getIndex());
		packet.writeData(handler.getSocketClient());
		handler.getMenuState().setMenuState(MainMenuState.menuStates.Lobby);
//		done = true;
	}
	
	

}
