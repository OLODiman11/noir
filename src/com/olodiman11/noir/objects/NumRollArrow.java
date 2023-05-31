package com.olodiman11.noir.objects;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet69NumRoll;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.buttons.Button;

public class NumRollArrow extends Button{

	private int dir;
	private NumRoll nr;
	
	public NumRollArrow(Handler handler, NumRoll nr, int dir) {
		super(handler, 0, 0);
		this.dir = dir;
		this.nr = nr;
		y = nr.getY();
		if(dir == ShiftButton.LEFT) {
			this.x = nr.getX() - 20 * Window.SCALE;
			texture = handler.getAssets().getNumRollLeftAr()[0];
			selectedImg = handler.getAssets().getNumRollLeftAr()[1];
			pressedImg = handler.getAssets().getNumRollLeftAr()[2];
		} else if(dir == ShiftButton.RIGHT) {
			this.x = nr.getX() + 30 * Window.SCALE;
			texture = handler.getAssets().getNumRollRightAr()[0];
			selectedImg = handler.getAssets().getNumRollRightAr()[1];
			pressedImg = handler.getAssets().getNumRollRightAr()[2];

		}
		
		width = (int) (20 * Window.SCALE);
		height = (int) (30 * Window.SCALE);
		active = true;
	}
	
	@Override
	public void processButtonPress() {
		if(dir == ShiftButton.LEFT && nr.getBotLim() != nr.getNum()) {
			new Packet69NumRoll(handler.getPlayer().getUsername(), nr.getKey(), dir).writeData(handler.getSocketServer());
		} else if(dir == ShiftButton.RIGHT && nr.getTopLim() != nr.getNum()) {
			new Packet69NumRoll(handler.getPlayer().getUsername(), nr.getKey(), dir).writeData(handler.getSocketServer());
		}
	}

}
