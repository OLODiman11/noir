package com.olodiman11.noir.objects.actions;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.packets.Packet11EndTurn;
import com.olodiman11.noir.objects.buttons.Button;

public class PassTurnButton extends Button{

	public PassTurnButton(Handler handler, double x, double y) {
		super(handler, x, y);
		texture = ImageManager.getImage("/buttons/others/check.png");
		selectedImg = ImageManager.getImage("/buttons/others/selected/check.png");
		pressedImg = ImageManager.getImage("/buttons/others/pressed/check.png");
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().getCurrMode().deactivateButtons();
		Packet11EndTurn packet = new Packet11EndTurn(handler.getPlayer().getUsername(), handler.getPlayer().getLineNum() + 1);
		packet.writeData(handler.getSocketClient());
	}
	
}
