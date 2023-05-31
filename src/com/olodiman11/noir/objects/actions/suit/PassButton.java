package com.olodiman11.noir.objects.actions.suit;

import java.awt.Graphics;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet49Protect;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.buttons.Button;

public class PassButton extends Button {

	public PassButton(Handler handler) {
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
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {			
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setDisplayed(true);
				break;
			}
		}
		Packet49Protect packet = new Packet49Protect(handler.getPlayer().getUsername(), -1, -1);
		packet.writeData(handler.getSocketClient());
		active = false;
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
