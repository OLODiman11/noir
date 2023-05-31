package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import javax.media.jai.PerspectiveTransform;
import javax.media.jai.RenderedOp;
import javax.media.jai.Warp;
import javax.media.jai.WarpPerspective;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.HvsS;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet06Capture;
import com.olodiman11.noir.net.packets.Packet12Canvas;
import com.olodiman11.noir.net.packets.Packet16Murder;
import com.olodiman11.noir.net.packets.Packet17Accuse;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet23Kill;
import com.olodiman11.noir.net.packets.Packet25Charge;
import com.olodiman11.noir.net.packets.Packet26Investigate;
import com.olodiman11.noir.net.packets.Packet27Steal;
import com.olodiman11.noir.net.packets.Packet37MKill;
import com.olodiman11.noir.net.packets.Packet38Threat;
import com.olodiman11.noir.net.packets.Packet39FBIAccuse;
import com.olodiman11.noir.net.packets.Packet41Swap;
import com.olodiman11.noir.net.packets.Packet42Bomb;
import com.olodiman11.noir.net.packets.Packet43Detonate;
import com.olodiman11.noir.net.packets.Packet44Snipe;
import com.olodiman11.noir.net.packets.Packet46Autopsy;
import com.olodiman11.noir.net.packets.Packet51Marker;
import com.olodiman11.noir.net.packets.Packet54Disable;
import com.olodiman11.noir.net.packets.Packet56InsideJob;
import com.olodiman11.noir.net.packets.Packet58Silence;
import com.olodiman11.noir.net.packets.Packet60AdSwap;
import com.olodiman11.noir.net.packets.Packet64Catch;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.psycho.DoneButton;
import com.olodiman11.noir.objects.actions.silencer.SilenceButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.StateManager;

public class Card extends Object implements Cloneable{
	
	// Booleans
	private boolean pressed, hovering, dead, fadingIn, fadingOut,
	selected, resizing, square, bigSquare, exonerated, active, highlighted, target,
	flipping, scalingUp, scalingDown, flipUp, flipDown;
	
	// Spacing
	private int space;
	
	// Textures
	private BufferedImage shadow, shade, highlight;
	private BufferedImage srcTexture, srcDeadTexture;
	private BufferedImage smallTexture, smallDeadTexture; 
	private BufferedImage largeTexture, largeDeadTexture;
	private BufferedImage newTexture, newDeadTexture;
	
	// Icons
	private BufferedImage check, actIconPress, actIconSel, actIconPend;
	
	// Coordinates
	private int finX, finY;
	private double tempX, tempY;
	
	// Others
	private int row, col, index, animInd;
	private float opacity, fadingSpeed, flipSpeed;
	private double ratio, speed;
	private Evidence ev;
	private ArrayList<Token> tokens;
	private float flipIt = 0;
	private double scaleIt = 1;
	
	public Card(Handler handler, double x, double y, int index) {
		super(handler, x, y);
		this.index = index;
		opacity = 1.0f;
		space = handler.getGameState().getCm().getSpace();
		finX = handler.getGameState().getCm().getFinX();
		finX = handler.getGameState().getCm().getFinY();
		fadingSpeed = 1.0f / 30;
		speed = 12.6;
		animInd = 0;
		tempX = -1;
		tempY = -1;
		ratio = 1;
		
		hovering = dead = fadingIn = fadingOut = selected = resizing
				= active = pressed = square = bigSquare = highlighted
				= flipping = scalingUp = scalingDown = false;
		
		srcTexture = handler.getAssets().getCards()[index];
		srcDeadTexture = handler.getAssets().getDead()[index];
		
		shadow = handler.getAssets().getShadow();
		shade = handler.getAssets().getShade();
		highlight = handler.getAssets().getHighlight();
		check = handler.getAssets().getCollapseCheck()[0];
		
		int width = handler.getGameState().getPreviewW();
		int height = handler.getGameState().getPreviewH();
		largeTexture = ImageManager.scaleImage(srcTexture, width, height);
		largeDeadTexture = ImageManager.scaleImage(srcDeadTexture, width, height); 
		
		tokens = new ArrayList<Token>();

	}
	
	public void resize() {
	
		int newWidth = handler.getGameState().getCm().getCardW();
		int newHeight = handler.getGameState().getCm().getCardH();
		
		if(newWidth == width && newHeight == height) {
			return;
		}
		
		newTexture = ImageManager.scaleImage(srcTexture, newWidth, newHeight);
		newDeadTexture = ImageManager.scaleImage(srcDeadTexture, newWidth, newHeight);
		
		resizing = true;
		fadeOut();
		if(ev != null) {
			ev.fadeOut();
		}
	
	}
	
	public void finishResizing() {
		
		if(row == 0 && col == 0) {
			double ratio = (double) newTexture.getWidth() / 290D;
			for(int i = 0; i < handler.getGameState().getCm().getTokens().length; i++) {
				handler.getGameState().getCm().getTokens()[i] = ImageManager.scaleImage(
						handler.getGameState().getCm().getTokens()[i],
						(int) (handler.getAssets().getTokens()[0].getWidth() * ratio),
						(int) (handler.getAssets().getTokens()[0].getHeight() * ratio));
			}
		}
		
		width = newTexture.getWidth();
		height = newTexture.getHeight();
		
		if(ev != null) {
			ev.scale(width, height);
		}
		
		finX = handler.getGameState().getCm().getFinX();
		finY = handler.getGameState().getCm().getFinY();
		
		updateTexture();
		
		x = finX + width * col + space * col;
		y = finY + height * row + space * row;
		
		fadingSpeed = 1.0f / 60;
		fadeIn();
		if(ev != null) {
			ev.fadeIn();
		}
	}
	
	public void checkCoordinates() {
	
		int minSpeed = 1;
		double newX = finX + width * col + space * col;
		double newY = finY + height * row + space * row;
		if(newX - minSpeed < x && newX + minSpeed > x && newY - minSpeed < y && newY + minSpeed > y) {
			x = newX;
			y = newY;
			if(!tokens.isEmpty()) {
				setCardDestCoords(0, 0);
			}
			if(!fadingIn && !fadingOut && !handler.getGameState().getCurrAction().equals(Actions.Collapse)) {				
				calculateOpacity();
			}
		}
		if(x != newX || y != newY) {
			move();
		}	
	
	}
	
	public void dieOut() {
		opacity -= fadingSpeed;
	}
	
	
	public void fadeIn() {
		opacity = 0;
		fadingIn = true;
	}
	
	public void fadeOut() {
		opacity = 1.0f;
		fadingOut = true;
	}
	
	public void move() {
		
		double minSpeed = speed * 0.6;
		double newX = finX + width * col + space * col;
		double newY = finY + height * row + space * row;
		
		if(!tokens.isEmpty()) {			
			setCardDestCoords((newX - x) / speed, (newY - y) / speed);
		}
		
		double distX = newX - x != 0 && Math.abs(newX - x) < minSpeed ? Math.signum(newX - x) * minSpeed : newX - x;
		double distY = newY - y != 0 && Math.abs(newY - y) < minSpeed ? Math.signum(newY - y) * minSpeed : newY - y;
		x += distX / speed;
		y += distY / speed;
		
		if(!handler.getGameState().getCurrAction().equals(Actions.Collapse)) {
			if(x < finX || x + width > finX + handler.getGameState().getNumCols() * width + space * (handler.getGameState().getNumCols() - 1)
			|| y < finY || y + height > finY + handler.getGameState().getNumRows() * height + space * (handler.getGameState().getNumRows() - 1)) {
				calculateOpacity();
			}
		}
		
	}
	
	private void calculateOpacity() {
		double boundX = finX + handler.getGameState().getNumCols() * width + space * (handler.getGameState().getNumCols() - 1);
		double boundY = finY + handler.getGameState().getNumRows() * height + space * (handler.getGameState().getNumRows() - 1);
		double dist = 1, left = 0;
		if(x < finX) {
			dist = width + space;
			left = finX - x;
		} else if(x + width > boundX) {
			dist = width + space;
			left = x + width - boundX;
		} else if(y < finY) {
			dist = height + space;
			left = finY - y;
		} else if(y + height > boundY) {
			dist = height + space;
			left = y + height - boundY;
		}
		if(left <= dist) {			
			opacity = (float) (1 - left/dist);
		} else {
			opacity = 0f;
		}
	}
	
	private void setCardDestCoords(double cardDestX, double cardDestY) {
		for(Token t: tokens) {
			t.setCardDestX(cardDestX);
			t.setCardDestY(cardDestY);
		}
	}
	
	public void moveTo(double x, double y) {
		
		tempX = x;
		tempY = y;
		
		this.x += (x - this.x) / speed;
		this.y += (y - this.y) / speed;
		
	}
	
	public void moveTo() {
		
		x += (tempX - x) / speed;
		y += (tempY - y) / speed;
		
	}
	
	public void updateTexture() {
		smallTexture = newTexture;
		smallDeadTexture = newDeadTexture;
	}
	
	public void scale(int newWidth, int newHeight) {
		
		smallTexture = ImageManager.scaleImage(srcTexture, newWidth, newHeight);
		smallDeadTexture = ImageManager.scaleImage(srcDeadTexture, newWidth, newHeight);
		
		width = smallTexture.getWidth();
		height = smallTexture.getHeight();
		
		ratio = (double) width / (double) srcTexture.getWidth();

		finX = handler.getGameState().getCm().getFinX();
		finY = handler.getGameState().getCm().getFinY();
		
	}
	
	public void scale() {
		
		int newWidth = handler.getGameState().getCm().getCardW();
		int newHeight = handler.getGameState().getCm().getCardH();
		
		scale(newWidth, newHeight);
		
	}
	
	public void deactivateAll() {
		handler.getGameState().getCm().deactivateAll();
	}
	
	// Actions
	
	public void canvas() {
		Packet12Canvas packet = new Packet12Canvas(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void capture() {
		Packet06Capture packet = new Packet06Capture(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void collapse() {
		selected = true;
		deactivateAll();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).processButtonPress();
			}
		}
	}
	
	public void autopsy() {
		Packet46Autopsy packet = new Packet46Autopsy(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void murder() {
		Packet16Murder packet = new Packet16Murder(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
		if(exonerated) {
			canvas();
		}
	}
	
	public void accuse() {
		Packet17Accuse packet = new Packet17Accuse(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void investigate() {
		Packet26Investigate packet = new Packet26Investigate(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void kill() {
		Packet23Kill packet = new Packet23Kill(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void charge() {
		Packet25Charge packet = new Packet25Charge(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void steal() {
		Packet27Steal packet = new Packet27Steal(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void bomb() {
		Packet42Bomb packet = new Packet42Bomb(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void catchThief() {
		Packet64Catch packet = new Packet64Catch(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void detonate() {
		((MvsFBI) handler.getGameState().getCurrMode()).getData().add(new int[] {row, col});
		deactivateAll();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setDisplayed(true);
			}
		}
		selected = true;
		boolean bomb = false;
		for(Token t: tokens) {			
			if(t.getType().equals(Tokens.BOMB)) {
				bomb = true;
				break;
			}
		}
		if(bomb) {
			for(int row = this.row - 1; row <= this.row + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(int col = this.col - 1; col <= this.col + 1; col++) {
					if(col < 0 || col >= handler.getGameState().getNumCols()) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].isDead()
							|| handler.getGameState().getMap()[row][col].equals(this)) {
						continue;
					}
					boolean data = false;
					for(int[] i: ((MvsFBI) handler.getGameState().getCurrMode()).getData()) {
						if(i[0] == row && i[1] == col) {
							data = true;
							break;
						}
					}
					if(data) {
						continue;
					}
					handler.getGameState().getMap()[row][col].setActive(true);
				}
			}
		} else {
			handler.getGameState().getCm().deselectAll();
			((MvsFBI) handler.getGameState().getCurrMode()).getData();
			int[][] coords = new int[((MvsFBI) handler.getGameState().getCurrMode()).getData().size()][2];
			for(int i = 0; i < coords.length; i++) {
				coords[i][0] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[0];
				coords[i][1] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[1];
			}
			Packet43Detonate packet = new Packet43Detonate(handler.getPlayer().getUsername(), coords);
			packet.writeData(handler.getSocketClient());
		}
	}
	
	public void mKill() {
		Packet37MKill packet = new Packet37MKill(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}

	public void swap() {
		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				Card c = handler.getGameState().getMap()[row][col];
				if(c.isSelected()) {
					c.setSelected(false);
					int[][] ints = new int[][] {{c.getRow(), c.getCol()}, {this.row, this.col}};
					Packet41Swap packet = new Packet41Swap(handler.getPlayer().getUsername(), ints);
					packet.writeData(handler.getSocketClient());
				}
			}
		}
	}
	
	public void marker() {
		Packet51Marker packet = new Packet51Marker(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
		((MvsFBI) handler.getGameState().getCurrMode()).activateButtons();
	}
	
	public void threat() {
		active = false;
		selected = true;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setDisplayed(false);
			}
			if(b instanceof DoneButton) {
				b.setActive(true);
			}
		}
		((MvsFBI) handler.getGameState().getCurrMode()).getData().add(new int[] {row, col});
		if(((MvsFBI) handler.getGameState().getCurrMode()).getData().size() >= 3) {
			handler.getGameState().getCm().deactivateAll();
			handler.getGameState().getCm().deselectAll();
			int[][] ints = new int[((MvsFBI) handler.getGameState().getCurrMode()).getData().size()][2];
			for(int i = 0; i < ((MvsFBI) handler.getGameState().getCurrMode()).getData().size(); i++) {
				ints[i][0] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[0];
				ints[i][1] = ((MvsFBI) handler.getGameState().getCurrMode()).getData().get(i)[1];
			}
			Packet38Threat packet = new Packet38Threat(handler.getPlayer().getUsername(), ints);
			packet.writeData(handler.getSocketClient());
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CollapseButton) {
					((CollapseButton) b).setDisplayed(true);
					break;
				}
			}
		}
	}
	
	public void fbiAccuse() {
		Packet39FBIAccuse packet = new Packet39FBIAccuse(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void snipe() {
		Packet44Snipe packet = new Packet44Snipe(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void disable() {
		Packet54Disable packet = new Packet54Disable(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void adSwap() {
		Packet60AdSwap packet = new Packet60AdSwap(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void insideJob() {
		Packet56InsideJob packet = new Packet56InsideJob(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	public void silence() {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof SilenceButton) {
				((SilenceButton) b).addKill();
			}
		}
		Packet58Silence packet = new Packet58Silence(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
		deactivateAll();
	}
	
	// Input
	
	@Override
	public void onMousePressed(MouseEvent m) {
		if(hovering && active && handler.getSm().getCurrState() == StateManager.GAME) {
			if(m.getButton() == MouseEvent.BUTTON1) {
				if(handler.getGameState().getCurrAction().equals(Actions.Swap)
						|| handler.getGameState().getCurrAction().equals(Actions.OfSwap)) {
					CardsManager.AL = new ArrowLine(handler, this);
					handler.getGameState().setCurrAction(Actions.SwapTo);
					deactivateAll();
					for(int row = this.row - 1; row <= this.row + 1; row++) {
						if(row < 0 || row >= handler.getGameState().getNumRows()) {
							continue;
						}
						for(int col = this.col - 1; col <= this.col + 1; col++) {
							if(col < 0 || col >= handler.getGameState().getNumCols()) {
								continue;
							}
							if(col == this.col && row == this.row) {
								continue;
							}
							handler.getGameState().getMap()[row][col].setActive(true);
						}
					}
					selected = true;
					setActive(true);
				}
				pressed = true;
			}
		}
	}

	@Override
	public void onKeyPressed(KeyEvent k) {}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(hovering && active && handler.getSm().getCurrState() == StateManager.GAME) {
			if(m.getButton() == MouseEvent.BUTTON1) {
				switch(handler.getGameState().getCurrAction()) {
				case Init:
					break;
				case Idle:
					break;
				case SwapTo:
					if(!selected) swap();
					break;
				case Disable:
					disable();
					break;
				case AdSwap:
					adSwap();
					break;
				case InsideJob:
					insideJob();
					break;
				case Silence:
					silence();
					break;
				case Catch:
					catchThief();
					break;
				case Marker:
					marker();
					break;
				case Threat:
					threat();
					break;
				case Canvas:
					canvas();
					handler.getPlayer().setDone(true);
					break;
				case FBIAccuse:
					fbiAccuse();
					break;
				case Capture:
					capture();
					handler.getPlayer().setDone(true);
					break;
				case Collapse:
					collapse();
					break;
				case Shift:
					break;
				case Murder:
					murder();
					break;
				case Accuse:
					accuse();
					break;
				case Kill:
					kill();
					break;
				case Charge:
					charge();
					break;
				case Autopsy:
					autopsy();
					break;
				case Investigate:
					investigate();
					break;
				case Steal:
				case StealWholeBoard:
					steal();
					break;
				case Bomb:
					bomb();
					break;
				case Detonate:
					detonate();
					break;
				case MKill:
					mKill();
					break;
				case Snipe:
					snipe();
					break;
				default:
					break;
				}
				if(!handler.getGameState().getCurrAction().equals(Actions.Collapse)) {
					for(Button b: handler.getGameState().getCurrMode().getButtons()) {
						if(b instanceof ShiftButton) {
							((ShiftButton) b).notShifted();
						} else if(b instanceof CancelButton) {
							b.setActive(false);
						}
					}
				}
			}
		} else if(hovering && handler.getGameState().getCurrAction().equals(Actions.Deputize)) {
			ev.onMouseReleased(m);
		}
		pressed = false;
	}

	@Override
	public void onKeyReleased(KeyEvent k) {

	}
	
	public void drawSquare(Graphics2D g2d) {
		if(square) {
			double offset = 1;
			g2d.setColor(Color.CYAN);
			g2d.setStroke(new BasicStroke((float) (3.0 * Window.SCALE), BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 10.0f, new float[]{20.0f, 10.0f}, (float) animInd));  
			g2d.drawRoundRect((int) (x - space / 2 - offset), (int) (y - space / 2 - offset),
					(int) (width + space), (int) (height + space), 10, 10);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.fillRoundRect((int) (x - space / 2 - offset), (int) (y - space / 2 - offset),
					(int) (width + space), (int) (height + space), 10, 10);
		} else if(bigSquare) {
			g2d.setColor(Color.CYAN);
			double x, y, width, height, offset;
			offset = 1;
			if(handler.getGameState().getMode().equals(gameModes.Heist)) {
				x = this.x - space / 2;
				y = this.y - space / 2;
				width = (space + this.width) * 4;
				height = (space + this.height) * 4;
			} else {				
				if(col == 0) {
					x = this.x - space / 2;
					width = (space + this.width) * 2;
				} else if(col == handler.getGameState().getNumCols() - 1){
					x = this.x - this.width - space - space / 2;
					width = (space + this.width) * 2;
				} else {
					x = this.x - this.width - space - space / 2;
					width = (space + this.width) * 3;
				}
				if(row == 0) {
					y = this.y - space /2;
					height = (space + this.height) * 2;
				} else if(row == handler.getGameState().getNumRows() - 1){
					y = this.y - this.height - space - space / 2;
					height = (space + this.height) * 2;
				} else {
					y = this.y - this.height - space - space / 2;
					height = (space + this.height) * 3;
				}
			}
			
			g2d.setStroke(new BasicStroke((float) (3.0 * Window.SCALE), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
					new float[] {20.0f,10.0f}, (float) animInd));
			g2d.drawRoundRect((int) (x - offset), (int) (y - offset),
					(int) (width), (int) (height), 10, 10);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.fillRoundRect((int) (x - offset), (int) (y - offset),
					(int) (width), (int) (height), 10, 10);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			for(int row = this.row - 1; row <= this.row + 1; row++) {
				if(row < 0  || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(int col = this.col - 1; col <= this.col + 1; col++) {
					if(col < 0 || col >= handler.getGameState().getNumCols()) {
						continue;
					}
					if((row == this.row && col == this.col) || (row == this.row && col == this.col + 1)) {
						continue;
					}
					handler.getGameState().getMap()[row][col].render(g2d);
				}
			}
		}
	}
	
	private void flip(Graphics2D g2d) {
		double scaleRate = handler.getGameState().getCm().getScaleRate();
		double flipRate = handler.getGameState().getCm().getFlipRate();

		if(flipUp) {
			flipSpeed += flipRate;
			if((3.14159 - flipSpeed) / flipRate <= 0.2 / scaleRate) {
				scalingDown = true;
			}
			if(flipSpeed > 3.14159) {
				flipUp = false;
				flipping = false;
			}			
		} else {
			flipSpeed -= flipRate;
			if(flipSpeed / flipRate <= 0.2 / scaleRate) {
				scalingDown = true;
			}
			if(flipSpeed < 0) {
				flipSpeed = 0;
				flipping = false;
			}
		}
		if(Math.cos(flipSpeed) <= 0) {
			flipIt = -1.2f - (float) (1.2 * Math.cos(flipSpeed));
		} else {
			flipIt = 1.2f - (float) (1.2 * Math.cos(flipSpeed));
		}

		if(scalingUp) {				
			scaleIt += scaleRate;
			if(scaleIt >= 1.2) {
				scaleIt = 1.2;
				scalingUp = false;
			}
		} else if(scalingDown) {
			scaleIt -= scaleRate;
			if(scaleIt <= 1) {
				scaleIt = 1;
				scalingDown = false;
			}
		}
				
		new PerspectiveTransform();
		PerspectiveTransform pt = PerspectiveTransform.getQuadToQuad(
				0, 0, width, 0, 0, height, width, height,
				0, 0 - width / 10 * flipIt,
				width, 0 + width / 10 * flipIt,
				0, height + width / 10 * flipIt,
				width, height - width / 10 * flipIt);
		Warp warpPerspective = new WarpPerspective(pt);
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
		RenderedOp op;
		if(dead) {	
			if(flipDown) {
				if(flipSpeed > 1.570796) {				
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);
				} else {				
					op = ImageManager.createWarpImage(smallDeadTexture, warpPerspective);
				}
			} else {				
				if(flipSpeed > 1.570796) {				
					op = ImageManager.createWarpImage(smallDeadTexture, warpPerspective);
				} else {				
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);
				}
			}
		} else {
			if(flipDown) {
				if(flipSpeed > 1.570796) {				
					op = ImageManager.createWarpImage(smallDeadTexture, warpPerspective);
				} else {				
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);
				}
			} else {				
				if(flipSpeed > 1.570796) {				
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);
				} else {				
					op = ImageManager.createWarpImage(smallDeadTexture, warpPerspective);
				}
			}
		}
		RenderedImage image = op.createInstance();		
		g2d.drawRenderedImage(image, new AffineTransform(scaleIt - Math.abs(flipIt), 0, 0, scaleIt,
				x + width / 2 * (Math.abs(flipIt) - (scaleIt - 1)), y - height / 2 * (scaleIt - 1)));
		
		if(flipSpeed > 3.14159) {
			flipSpeed = 0;
		}
		if(flipDown && flipSpeed <= 0) {
			flipDown = false;
		}
	}

	// Tick and Render

	@Override
	public void tick() {
		
		if(handler.getGameState().getCurrAction().equals(Actions.Deputize) && exonerated) {
			highlighted = true;
		} else if(handler.getGameState().getCm().getChoicePoll().isEmpty()) {
			boolean highlighted = false;
			for(Player p: handler.getConnectedPlayers()) {	    		
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == index && !ev.isHidden() && ev.isHovering()) {
						highlighted = true;
						break;
					}
				}
				if(highlighted) {
					break;
				}
			}
			this.highlighted = highlighted;
	    } else {
	    	boolean highlighted = false;
	    	for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
	    		if(ev.getIndex() == index) {
	    			highlighted = true;
	    			break;
	    		}
	    	}
	    	this.highlighted = highlighted;
	    }
		
		animInd++;
		if(animInd == 30) {
			animInd = 0;
		}
		
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX >= x && mouseX <= x + width
		&& mouseY >= y && mouseY <= y + height
		&& handler.getSm().getCurrState() == StateManager.GAME) {
			hovering = true;
			if(CardsManager.AL != null && active) {
				CardsManager.AL.setTarget(this);
			}
		} else {
			hovering = false;
			if(CardsManager.AL != null && CardsManager.AL.getTarget() == this) {
				CardsManager.AL.setTarget(null);
			}
		}
		
		if(fadingIn) {
			opacity += fadingSpeed;
			if(opacity >= 1) {
				opacity = 1;
				fadingIn = false;
//				handler.getGameState().getCm().setTempCard(null);
				fadingSpeed = 1.0f / 30;
			}
		}
		
		if(fadingOut) {
			opacity -= fadingSpeed;
			if(opacity <= 0) {
				opacity = 0;
				fadingOut = false;
				if(resizing) {
					resizing = false;
					System.out.print(row + "," + col + "; ");
					finishResizing();
				}
			}
		}
		
		checkCoordinates();
		
		if(ev != null) {
			ev.tick();			
		}
		
		if(!tokens.isEmpty()) {
			for(Token t: tokens) {
				t.tick();
			}
		}
		
	}

	@Override
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		drawSquare(g2d);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
//		g2d.drawImage(shadow, (int) (x - Math.floor(20 * ratio)), (int) (y - Math.floor(20 * ratio)),
//				(int) (width + 40 * ratio), (int) (height + 40 * ratio), null);
		
		if(highlighted) {
			g2d.drawImage(highlight, (int) (x - Math.floor(20 * ratio)), (int) (y - Math.floor(20 * ratio)),
					(int) (width + 40 * ratio), (int) (height + 40 * ratio), null);
		}
		if(flipping) {
			flip(g2d);			
		} else {			
			if(dead) { 
				g2d.drawImage(smallDeadTexture, (int) x, (int) y, width, height, null);
			} else {
				g2d.drawImage(smallTexture, (int) x, (int) y, width, height, null);
			}
		}
		
		if(ev != null) {
			ev.render(g);			
		}
		
		if(!tokens.isEmpty()) {
			for(Token t: tokens) {
				t.render(g);
			}
		}
		
//		g2d.drawImage(shade, (int) x, (int) y, width, height, null);
		
		if(target) {
			g2d.drawImage(((HvsS) handler.getGameState().getCurrMode()).getTarget(), (int) x, (int) y, width, height, null);
		}
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		if(hovering) {
			int previewEvX = handler.getGameState().getPreviewEvX();
			int previewEvY = handler.getGameState().getPreviewEvY();
			if(dead) {
				g.drawImage(largeDeadTexture, x0 + previewEvX, y0 + previewEvY, null);
			} else {
				g.drawImage(largeTexture, x0 + previewEvX, y0 + previewEvY, null);
			}
		}
		
		if(active) {			
			int x = (int) (this.x + (width - actIconPress.getWidth()) / 2);
			int y = (int) (this.y + (height - actIconPress.getHeight()) / 2);
			int w = actIconPress.getWidth();
			int h = actIconPress.getHeight();
			if(pressed) {
				g2d.drawImage(actIconPress, x, y, w, h, null);
			} else if (hovering) {
				g2d.drawImage(actIconSel, x, y, w, h, null);
			} else {
				g2d.drawImage(actIconPend, x, y, w, h, null);
			}
		} else if(selected && !handler.getGameState().getCurrAction().equals(Actions.SwapTo)) {
			int x = (int) (this.x + (width - check.getWidth()) / 2);
			int y = (int) (this.y + (height - check.getHeight()) / 2);
			int w = (int) (check.getWidth() * Window.SCALE);
			int h = (int) (check.getHeight() * Window.SCALE);
			g2d.drawImage(check, x, y, w, h, null);
		}
		
	}
	
	public java.lang.Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Getters and Setters
	
	public Image getTexture() {
		return texture;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public int getIndex() {
		return index;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void setFadingIn(boolean fadingIn) {
		this.fadingIn = fadingIn;
	}

	public void setFadingOut(boolean faidingOut) {
		this.fadingOut = faidingOut;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		if(this.dead != dead) {			
			this.dead = dead;
			scalingDown = false;
			scalingUp = true;
			flipDown = false;
			if(flipUp) {			
				flipUp = false;
				flipDown = true;
			} else {
				flipUp = true;
			}
			flipping = true;
		}
	}
	
	public void die(boolean dead) {
		setDead(true);
		tokens.clear();
		if(handler.getPlayer().getCard() == null) {
			return;
		}
		
		if(handler.getGameState().getMode().equals(gameModes.SpyTag) || handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
			if(handler.getPlayer().getCard().getIndex() == index) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, false, true, 1);
				packet.writeData(handler.getSocketClient());
			}
		} else if(handler.getGameState().getMode().equals(gameModes.HvsS)) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getCard().getIndex() == index){
					if(p.getRole().equals(Roles.Hitman)) {
						break;
					}
					p.removeIdentity(false);
					if(p.equals(handler.getPlayer()) && !p.getHand().isEmpty()) {
						handler.getGameState().setCurrAction(Actions.Change);
						handler.getGameState().getCm().drawCard(p, false, true, false, false, p.getHand().size());
					}
				}
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if(active) {
			switch(handler.getGameState().getCurrAction()) {
			case Collapse:
				actIconPend = handler.getAssets().getCollapseCheck()[1];
				actIconSel = handler.getAssets().getCollapseCheck()[2];
				actIconPress = handler.getAssets().getCollapseCheck()[3];
				break;
			case Canvas:
				actIconPend = handler.getGameState().getCm().getCanvas()[0];
				actIconSel = handler.getGameState().getCm().getCanvas()[1];
				actIconPress = handler.getGameState().getCm().getCanvas()[2];
				break;
			case Disable:
				actIconPend = handler.getGameState().getCm().getDisable()[0];
				actIconSel = handler.getGameState().getCm().getDisable()[1];
				actIconPress = handler.getGameState().getCm().getDisable()[2];
				for(BufferedImage bi: handler.getGameState().getCm().getDisable()) {
					if(bi == null) {
						System.out.println("null");
					}
				}
				System.out.println("Icons loaded");
				break;
			case Detonate:
				actIconPend = handler.getGameState().getCm().getDetonate()[0];
				actIconSel = handler.getGameState().getCm().getDetonate()[1];
				actIconPress = handler.getGameState().getCm().getDetonate()[2];
				break;
			case Swap:
			case OfSwap:
				actIconPend = handler.getGameState().getCm().getSwapFrom()[0];
				actIconSel = handler.getGameState().getCm().getSwapFrom()[1];
				actIconPress = handler.getGameState().getCm().getSwapFrom()[2];
				break;
			case SwapTo:
				actIconPend = handler.getGameState().getCm().getSwapTo()[0];
				actIconSel = handler.getGameState().getCm().getSwapTo()[1];
				actIconPress = handler.getGameState().getCm().getSwapTo()[2];
				break;
			case AdSwap:
			case InsideJob:
				actIconPend = handler.getGameState().getCm().getSwap()[0];
				actIconSel = handler.getGameState().getCm().getSwap()[1];
				actIconPress = handler.getGameState().getCm().getSwap()[2];
				break;
			case Autopsy:
				actIconPend = handler.getGameState().getCm().getAutopsy()[0];
				actIconSel = handler.getGameState().getCm().getAutopsy()[1];
				actIconPress = handler.getGameState().getCm().getAutopsy()[2];
				break;
			case Threat:
				actIconPend = handler.getGameState().getCm().getThreat()[0];
				actIconSel = handler.getGameState().getCm().getThreat()[1];
				actIconPress = handler.getGameState().getCm().getThreat()[2];
				break;
			case Marker:
				for(Token t: tokens) {
					if(t.getType().equals(Tokens.PROTECTION)) {
						actIconPend = handler.getGameState().getCm().getProtectionRemove()[0];
						actIconSel = handler.getGameState().getCm().getProtectionRemove()[1];
						actIconPress = handler.getGameState().getCm().getProtectionRemove()[2];
						this.active = true;
						return;
					}
				}
				actIconPend = handler.getGameState().getCm().getProtectionAdd()[0];
				actIconSel = handler.getGameState().getCm().getProtectionAdd()[1];
				actIconPress = handler.getGameState().getCm().getProtectionAdd()[2];
				break;
			case Bomb:
				actIconPend = handler.getGameState().getCm().getBomb()[0];
				actIconSel = handler.getGameState().getCm().getBomb()[1];
				actIconPress = handler.getGameState().getCm().getBomb()[2];
				break;
			case Silence:
				actIconPend = handler.getGameState().getCm().getSilence()[0];
				actIconSel = handler.getGameState().getCm().getSilence()[1];
				actIconPress = handler.getGameState().getCm().getSilence()[2];
				break;
			case Steal:
			case StealWholeBoard:
				actIconPend = handler.getGameState().getCm().getSteal()[0];
				actIconSel = handler.getGameState().getCm().getSteal()[1];
				actIconPress = handler.getGameState().getCm().getSteal()[2];
				break;
			case Capture:
				actIconPend = handler.getGameState().getCm().getCapture()[0];
				actIconSel = handler.getGameState().getCm().getCapture()[1];
				actIconPress = handler.getGameState().getCm().getCapture()[2];
				break;
			case Kill:
			case Snipe:
				actIconPend = handler.getGameState().getCm().getKill()[0];
				actIconSel = handler.getGameState().getCm().getKill()[1];
				actIconPress = handler.getGameState().getCm().getKill()[2];
				break;
			case Murder:
			case MKill:
				actIconPend = handler.getGameState().getCm().getMurder()[0];
				actIconSel = handler.getGameState().getCm().getMurder()[1];
				actIconPress = handler.getGameState().getCm().getMurder()[2];
				break;
			default:
				actIconPend = handler.getGameState().getCm().getAccuse()[0];
				actIconSel = handler.getGameState().getCm().getAccuse()[1];
				actIconPress = handler.getGameState().getCm().getAccuse()[2];
				break;
			}
		}
		this.active = active;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setResizing(boolean resizing) {
		this.resizing = resizing;
	}

	public BufferedImage getSrcTexture() {
		return srcTexture;
	}

	public BufferedImage getSrcDeadTexture() {
		return srcDeadTexture;
	}

	public void setFinX(int finX) {
		this.finX = finX;
	}

	public void setFinY(int finY) {
		this.finY = finY;
	}

	public BufferedImage getLargeTexture() {
		return largeTexture;
	}

	public void setTemp(boolean temp) {
	}

	public void setSmallTexture(BufferedImage smallTexture) {
		this.smallTexture = smallTexture;
	}
	
	public boolean isHovering() {
		return hovering;
	}

	public void setCharging(boolean charging) {
		this.square = charging;
	}

	public void setInterrogating(boolean interrogating) {
		this.bigSquare = interrogating;
	}

	public boolean isExonerated() {
		return exonerated;
	}

	public void setExonerated(boolean exonerated) {
		this.exonerated = exonerated;
	}

	public void setColnRow(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public BufferedImage getHighlight() {
		return highlight;
	}

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public Evidence getEv() {
		return ev;
	}

	public void setEv(Evidence ev) {
		this.ev = ev;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}
	
	public void addToken(Token t) {
		tokens.add(t);
		for(Token token: tokens) {
			token.setMoving(true);
		}
	}
	
	public void removeToken(Token t) {
		t.setFadingOut(true);
		for(Token token: tokens) {
			token.setMoving(true);
		}
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public boolean isFlipping() {
		return flipping;
	}

}
