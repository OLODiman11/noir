package com.olodiman11.noir.objects.actions.psycho;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.net.packets.Packet38Threat;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.buttons.Button;

public class DoneButton extends Button{
	
	private BufferedImage disabledImg;

	public DoneButton(Handler handler) {
		super(handler, 0, 0);
		x = x0 + 1067 * Window.SCALE;
		y = y0 + 113 * Window.SCALE;
		texture = handler.getAssets().getCancel()[0];
		selectedImg = handler.getAssets().getCancel()[1];
		pressedImg = handler.getAssets().getCancel()[2];
		disabledImg = handler.getAssets().getCancel()[3];
		
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		active = false;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().getCm().deselectAll();
		handler.getGameState().getCm().deactivateAll();
		int[][] ints = new int[((MvsFBI) handler.getGameState().getCurrMode()).getData().size()][2];
		for(int i = 0; i < ((MvsFBI) handler.getGameState().getCurrMode()).getData().size(); i++) {
			ints[i][0] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[0];
			ints[i][1] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[1];
		}
		Packet38Threat packet = new Packet38Threat(handler.getPlayer().getUsername(), ints);
		packet.writeData(handler.getSocketClient());
		active = false;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setDisplayed(true);
				break;
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if(active) {
			super.render(g);
		} else {
			g.drawImage(disabledImg, (int) x, (int) y, width, height, null);
		}
	}

}
