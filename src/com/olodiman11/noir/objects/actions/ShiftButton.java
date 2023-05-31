package com.olodiman11.noir.objects.actions;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.objects.Arrow;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.states.GameState.Actions;

public class ShiftButton extends ActionButton{

	private ArrayList<Arrow> arrows;
	private boolean fast, stealth;
	private BufferedImage[] up, down, left, right;
	private int numCols, numRows, arrowW, arrowH;
	private int[] justShifted; 
	public static final int RIGHT = 0,
							UP = 1,
							LEFT = 2,
							DOWN = 3;
	
	public ShiftButton(Handler handler) {
		super(handler);
		justShifted = new int[] {-1, -1};
		arrows = new ArrayList<Arrow>();
		setArrowsTextures();
		arrowW = up[0].getWidth();
		arrowH = up[0].getHeight();
	}
	
	public void setArrowsTextures() {
		
		up = handler.getAssets().getUp();
		down = handler.getAssets().getDown();
		left = handler.getAssets().getLeft();
		right = handler.getAssets().getRight();
	
	}
	
	public void setupTexture() {
		if(handler.getPlayer().getRole().equals(Roles.Runner)) {
			if(handler.getGameState().getCurrMode().getButtons().isEmpty()) {
				texture = handler.getAssets().getRoles().get(Roles.Runner.getText())[3][0];
				selectedImg = handler.getAssets().getRoles().get(Roles.Runner.getText())[3][1];
				return;
			}
		} else if(handler.getPlayer().getRole().equals(Roles.Sneak)) {
			if(handler.getGameState().getCurrMode().getButtons().isEmpty()) {				
				texture = handler.getAssets().getRoles().get(Roles.Sneak.getText())[3][0];
				selectedImg = handler.getAssets().getRoles().get(Roles.Sneak.getText())[3][1];
				return;
			}
		}
		texture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[1][0];
		selectedImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[1][1];
	}
	
	public void setupXnY() {
		switch(handler.getPlayer().getRole()) {
		case Runner:
			if(!handler.getGameState().getCurrMode().getButtons().isEmpty()) {
				x = x0 + handler.getGameState().getPreviewX() + 26 * ratio;
				y = y0 + handler.getGameState().getPreviewY() + 189 * ratio;				
			} else {
				x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
				y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
				fast = true;
			}
			break;
		case Sneak:
			if(!handler.getGameState().getCurrMode().getButtons().isEmpty()) {
				x = x0 + handler.getGameState().getPreviewX() + 26 * ratio;
				y = y0 + handler.getGameState().getPreviewY() + 189 * ratio;				
			} else {
				x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
				y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
				stealth = true;
			}
			break;
		case Hitman:
		case Sniper:
		case Thug:
		case Bomber:
			x = x0 + handler.getGameState().getPreviewX() + 25 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 189 * ratio;
			break;
		case Profiler:
		case Suit:
			x = x0 + handler.getGameState().getPreviewX() + 27 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 281 * ratio;
			break;
		case Psycho:
		case Safecracker:
			x = x0 + handler.getGameState().getPreviewX() + 30 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
			break;
		case Undercover:
			x = x0 + handler.getGameState().getPreviewX() + 24 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 189 * ratio;
			break;
		default:
			x = x0 + handler.getGameState().getPreviewX() + 26 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 189 * ratio;
			break;
		}
	}
	
	@Override
	public void onMousePressed(MouseEvent m) {
		
		super.onMousePressed(m);
		
		for(Arrow arr: arrows) {
			arr.onMousePressed(m);
		}
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		
		super.onMouseReleased(m);
		
		try {
			for(Arrow arr: arrows) {
				arr.onMouseReleased(m);
			}			
		} catch(ConcurrentModificationException e) {}
		
	}
	
	public void clearArrows() {
		arrows.clear();
	}
	
	public void tick() {
		
		super.tick();
		
		try {
			if(!arrows.isEmpty()) {
				for(Arrow arr: arrows) {
					arr.tick();
				}
			}
		} catch(ConcurrentModificationException e) {}
		
	}
	
	public void render(Graphics g) {
		
		super.render(g);
		
		try {
			if(!arrows.isEmpty()) {
				for(Arrow arr: arrows) {
					arr.render(g);
				}
			}
		} catch(ConcurrentModificationException e) {}
		
	}
	
	public ArrayList<Arrow> getArrows(){
		return arrows;
	}

	@Override
	public void processButtonPress() {
		numCols = handler.getGameState().getNumCols();
		numRows = handler.getGameState().getNumRows();
		handler.getGameState().setCurrAction(Actions.Shift);
		Card[][] map = handler.getGameState().getMap();
		if(numCols > 1) {			
			for(int i = 0; i < numRows; i++) {
				if(i == justShifted[0] && RIGHT == justShifted[1]) {
					continue;
				}
				if(handler.getPlayer().getRole().equals(Roles.SecurityChief)) {
					Card c;
					boolean cont = false;
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(ev.isHidden()) {								
							if(c.getRow() == i) {
								cont = true;
								break;
							}
						}
					}
					if(cont) {
						continue;
					}
				}
				double x = x0 + 323 * Window.SCALE;
				double y = map[i][0].getY() + map[i][0].getHeight() / 2 - arrowH * Window.SCALE / 2;
				arrows.add(new Arrow(handler, left, x, y, LEFT, 0, i));
			}
			for(int i = 0; i < numRows; i++) {
				if(i == justShifted[0] && LEFT == justShifted[1]) {
					continue;
				}
				if(handler.getPlayer().getRole().equals(Roles.SecurityChief)) {
					Card c;
					boolean cont = false;
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(ev.isHidden()) {								
							if(c.getRow() == i) {
								cont = true;
								break;
							}
						}
					}
					if(cont) {
						continue;
					}
				}
				double x = x0 + 989 * Window.SCALE;
				double y = map[i][numCols - 1].getY() + map[i][numCols - 1].getHeight() / 2 - arrowH * Window.SCALE / 2;
				arrows.add(new Arrow(handler, right, x, y, RIGHT, numCols - 1, i));
			}
		}
		if(numRows > 1) {			
			for(int i = 0; i < numCols; i++) {
				if(i == justShifted[0] && DOWN == justShifted[1]) {
					continue;
				}
				if(handler.getPlayer().getRole().equals(Roles.SecurityChief)) {
					Card c;
					boolean cont = false;
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(ev.isHidden()) {								
							if(c.getCol() == i) {
								cont = true;
								break;
							}
						}
					}
					if(cont) {
						continue;
					}
				}
				double x = map[0][i].getX() + map[0][i].getWidth() / 2 - arrowW * Window.SCALE / 2;
				double y = y0 + 67 * Window.SCALE;
				arrows.add(new Arrow(handler, up, x, y, UP, i, 0));
			}
			for(int i = 0; i < numCols; i++) {
				if(i == justShifted[0] && UP == justShifted[1]) {
					continue;
				}
				if(handler.getPlayer().getRole().equals(Roles.SecurityChief)) {
					Card c;
					boolean cont = false;
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						if(ev.isHidden()) {							
							c = handler.getGameState().getCm().getCard(ev.getIndex());
							if(c.getCol() == i) {
								cont = true;
								break;
							}
						}
					}
					if(cont) {
						continue;
					}
				}
				double x = map[numRows - 1][i].getX() + map[numRows - 1][i].getWidth() / 2 - arrowW * Window.SCALE / 2;
				double y = y0 + 691 * Window.SCALE;
				arrows.add(new Arrow(handler, down, x, y, DOWN, i, numRows - 1));
			}
		}
		System.out.print("Hello");
		if(handler.getPlayer().getRole().equals(Roles.Thug)
		|| handler.getPlayer().getRole().equals(Roles.Sniper)
		|| handler.getPlayer().getRole().equals(Roles.Suit)) {
			for(Arrow a: arrows) {
				a.setFast(true);
			}
		} else if(fast) {
			for(Arrow a: arrows) {
				a.setFastOnly(true);
			}
			System.out.println("fast");
		} else if(stealth) {
			for(Arrow a: arrows) {
				a.setStealth(true);
			}
		}
	}

	public void setJustShifted(int rc, int dir) {
		justShifted[0] = rc;
		justShifted[1] = dir;
	}
	
	public void notShifted() {
		justShifted[0] = -1;
		justShifted[1] = -1;
	}
	
}
