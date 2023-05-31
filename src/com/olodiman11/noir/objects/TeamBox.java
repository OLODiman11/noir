package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;

public class TeamBox extends Object{

	private boolean displayed, hovering;
	private ArrayList<Team> teams;
	
	public TeamBox(Handler handler) {
		super(handler, 0, 0);
		x = x0 + 708 * Window.SCALE;
		y = y0 + 114 * Window.SCALE;
		width = (int) (315 * Window.SCALE);
		height = (int) (44 * Window.SCALE);
		displayed = false;
		teams = new ArrayList<Team>();
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		for(Team t: teams) {
			t.onMouseReleased(m);
		}
		if(hovering) {
			if(!teams.isEmpty()) {
				displayed = !displayed;
			}
		} else {
			displayed = false;
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
		
		if(displayed) {
			for(Team t: teams) {
				t.tick();
			}
		}
	}

	@Override
	public void render(Graphics g) {	
		if(displayed) {			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.decode("#232528"));
			g2d.fillRoundRect((int) x, (int) (y + height / 2), width, (int) (height / 2 + (40 * teams.size() + 6) * Window.SCALE),
					(int) (10 * Window.SCALE), (int) (10 * Window.SCALE));
			for(Team t: teams) {
				t.render(g);
			}
		}
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public ArrayList<Team> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<Team> teams) {
		this.teams = teams;
	}
	
}
