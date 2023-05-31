package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet34Team;

public class Team extends Object{
	
	private TeamBox tb;
	private String text;
	private boolean hovering;
	
	public Team(Handler handler, TeamBox tb, String text, int i) {
		super(handler, 0, 0);
		width = tb.getWidth();
		height = (int) (40 * Window.SCALE);
		x = tb.getX();
		y = tb.getY() + tb.getHeight() + height * i;

		this.text = text;
		this.tb = tb;
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(hovering) {
			hovering = false;
			if((handler.getPlayer().getTeam() == null && text.equalsIgnoreCase("Случайно"))
			|| (handler.getPlayer().getTeam() != null && text.equalsIgnoreCase(handler.getPlayer().getTeam()))) {
				return;
			}
			Packet34Team packet = new Packet34Team(handler.getPlayer().getUsername(), text);
			packet.writeData(handler.getSocketClient());				
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {

	}

	@Override
	public void tick() {
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX >= x && mouseX <= x + width
		&& mouseY >= y && mouseY <= y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(hovering) {
			g2d.setColor(Color.decode("#7A7A7A"));
			g2d.fillRoundRect((int) (x + 4 * Window.SCALE), (int) y,
					(int) (width - 8 * Window.SCALE), height,
					(int) (6 * Window.SCALE), (int) (6 * Window.SCALE));
		}
		Text.drawCenteredText(text, x + width / 2, y + height / 2 - 3 * Window.SCALE, g, (int) (30 * Window.SCALE));
	}

}
