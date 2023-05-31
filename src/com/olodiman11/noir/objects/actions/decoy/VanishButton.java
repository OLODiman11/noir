package com.olodiman11.noir.objects.actions.decoy;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.net.packets.Packet55Vanish;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class VanishButton extends ActionButton{

	public VanishButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.Decoy.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.Decoy.getText())[3][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 15 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Vanish);
		Evidence ev = handler.getPlayer().getCard();
		handler.getPlayer().removeIdentity(false);
		handler.getGameState().getCm().shuffleInDeck(ev);
		handler.getGameState().getCm().sendToShuffle();
		Game.sleep(500);
		Packet55Vanish packet = new Packet55Vanish(handler.getPlayer().getUsername());
		packet.writeData(handler.getSocketClient());
	}

}
