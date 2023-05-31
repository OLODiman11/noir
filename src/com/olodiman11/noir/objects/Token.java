package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet40Disarm;
import com.olodiman11.noir.net.packets.Packet45Setup;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;

public class Token extends Object{

	private Tokens type;
	private Card c;
	private double destX, destY, cardDestX, cardDestY;
	private boolean hovering, taken, moving, fadingIn, fadingOut;
	private float opacity, fadingSpeed;
	private BufferedImage highlight;
	private int hlOffset;
	
	public Token(Handler handler, Tokens type, Card c) {
		super(handler, 0, 0);
		this.type = type;
		this.c = c;
		texture = handler.getGameState().getCm().getTokens()[type.getIndex()];
		highlight = handler.getGameState().getCm().getTknHighlight();
		hlOffset = (int) Math.floor(highlight.getWidth() - texture.getWidth()) / 2;
		x = c.getX() + c.getWidth() / 2 - texture.getWidth() / 2;
		y = c.getY() + c.getHeight() / 2 - texture.getHeight() / 2;
		moving = false;
		opacity = 0f;
		fadingSpeed = 1.0f / 30;
		fadingIn = true;
	}
	
	@Override
	public void onMousePressed(MouseEvent m) {
		if(hovering) {
			if(handler.getGameState().getCurrAction().equals(Actions.Setup)) {
				taken = true;
				x = handler.getMm().getX() - texture.getWidth() / 2;
				y = handler.getMm().getY() - texture.getHeight() / 2;
			}
		}
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}
	
	public void onMouseDragged(MouseEvent m) {
		if(taken) {
			x = handler.getMm().getX() - texture.getWidth() / 2;
			y = handler.getMm().getY() - texture.getHeight() / 2;
		}
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(hovering) {
			if(handler.getGameState().getCurrAction().equals(Actions.Setup)) {
				for(int row = c.getRow() - 1; row <= c.getRow() + 1; row++) {
					for(int col = c.getCol() - 1; col <= c.getCol() + 1; col++) {
						if(handler.getGameState().getMap()[row][col].isHovering()
						&& !handler.getGameState().getMap()[row][col].equals(c)) {
							Packet45Setup packet = new Packet45Setup(handler.getPlayer().getUsername(),
									type.getIndex(), new int[][] {{c.getRow(), c.getCol()}, {row, col}});
							packet.writeData(handler.getSocketClient());
							return;
						}
					}
				}
				x = x0 + c.getX() + c.getWidth() / 2 - texture.getWidth() / 2;
				y = y0 + c.getY() + c.getHeight() / 2 - texture.getHeight() / 2;
				taken = false;
			} else if(handler.getGameState().getCurrAction().equals(Actions.Disarm)) {
				Packet40Disarm disarm = new Packet40Disarm(handler.getPlayer().getUsername(), c.getRow(), c.getCol(), type.getIndex());
				disarm.writeData(handler.getSocketClient());
			}
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}

	@Override
	public void tick() {
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX >= x && mouseX <= x + texture.getWidth()
		&& mouseY >= y && mouseY <= y + texture.getHeight()) {
			hovering = true;
		} else {
			hovering = false;
		}
		
		if(taken) {
			return;
		}

		switch(c.getTokens().size()) {
		case 1:
			destX = c.getX() + c.getWidth() / 2 - texture.getWidth() / 2;
			destY = c.getY() + c.getHeight() / 2 - texture.getHeight() / 2;
			break;
		case 2:
			destX = c.getX() + c.getWidth() / 2 - texture.getWidth() / 2;
			destY = c.getY() + (c.getHeight() - texture.getHeight() * 2) / 3 + c.getTokens().indexOf(this)
					* ((c.getHeight() - texture.getHeight() * 2) / 3 + texture.getHeight());
			break;
		case 3:
			double angle = 360 / 3;
			double rad = Math.sqrt(Math.pow(texture.getWidth() / 2, 2) + Math.pow(texture.getHeight() / 2, 2));
			destX = c.getX() + c.getWidth() / 2 + rad
					* Math.cos(-angle * c.getTokens().indexOf(this) + 150) - texture.getWidth() / 2;
			destY = c.getY() + c.getHeight() / 2 + rad
					* -Math.sin(-angle * c.getTokens().indexOf(this) + 150) - texture.getHeight() / 2;
			break;
		}
		
//		if(moving) {			
			double speed = 12.6;
			if(x != destX || y != destY) {
				x += cardDestX;
				y += cardDestY;
				x += (destX - x) / speed;
				y += (destY - y) / speed;
//				boolean xDest = false;
//				boolean yDest = false;
				if(Math.abs(destX - x) <= 1) {
					x = destX;
//					xDest = true;
				}
				if(Math.abs(destY - y) <= 1) {
					y = destY;
//					yDest = true;
				}
//				if(xDest && yDest) {
//					moving = false;
//				}
			} else {
//				moving = false;
				x = destX;
				y = destY;
			}
//		} else {
//			x = destX;
//			y = destY;
//		}
		
		if(fadingIn) {
			opacity += fadingSpeed;
			if(opacity >= 1) {
				opacity = 1f;
				fadingIn = false;
			}
		} else if(fadingOut) {
			opacity -= fadingSpeed;
			if(opacity <= 0f) {
				c.getTokens().remove(this);
			}
		} else {			
			opacity = c.getOpacity();
		}
		
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		if(hovering && handler.getGameState().getCurrAction().equals(Actions.Disarm)) {
			g2d.drawImage(highlight, (int) (x - hlOffset * Window.SCALE), (int) (y - hlOffset * Window.SCALE), null);
		}
		g2d.drawImage(texture, (int) x, (int) y, null); 
	}
	
	public void move(double x, double y) {
		destX = x;
		destY = y;
	}
	
	public void setXnY(double x, double y) {
		this.x = x;
		this.y = y;
		this.destX = x;
		this.destY = y;
	}

	public Tokens getType() {
		return type;
	}
	
	public void setC(Card c) {
		this.c = c;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setCardDestX(double cardDestX) {
		this.cardDestX = cardDestX;
	}

	public void setCardDestY(double cardDestY) {
		this.cardDestY = cardDestY;
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
		
	}

	public void setFadingOut(boolean fadingOut) {
		this.fadingOut = fadingOut;
	}
	
}
