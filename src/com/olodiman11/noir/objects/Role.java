package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet34Team;

public class Role extends Object{

	private RoleBox rb;
	private Roles role;
	private boolean hovering;
	
	public Role(Handler handler, RoleBox rb, Roles role, int i) {
		super(handler, 0, 0);
		width = rb.getWidth();
		height = (int) (40 * Window.SCALE);
		x = rb.getX();
		y = rb.getY() + rb.getHeight() + height * i;
		
		this.rb = rb;
		this.role = role;
		
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
			String role;
			if(this.role == null) {
				role = "Случайно";
			} else {
				role = this.role.getText();
			}
			if((handler.getPlayer().getRole() == null && role.equalsIgnoreCase("Случайно"))
			|| (handler.getPlayer().getRole() != null && role.equalsIgnoreCase(handler.getPlayer().getRole().getText()))) {
				return;
			}
			Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(), handler.getPlayer().getUsername(), role);
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
		String role;
		if(this.role == null) {
			role = "Случайно";
		} else {
			role = this.role.getName();
		}
		float size = (float) (30 * Window.SCALE);
		if(this.role != null) {
			if(this.role.equals(Roles.ChiefOfPolice)) {
				size = (float) (25 * Window.SCALE);
			} else if(this.role.equals(Roles.Undercover)) {
				size = (float) (23 * Window.SCALE);
			}
		}
		Text.drawCenteredText(role, x + width / 2, y + height / 2 - 3 * Window.SCALE, g, size);
	}

}
