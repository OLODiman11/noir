package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Repository;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.KvsI;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet14Ready;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet20Identity;
import com.olodiman11.noir.net.packets.Packet21Exonerate;
import com.olodiman11.noir.net.packets.Packet24Defend;
import com.olodiman11.noir.net.packets.Packet28Change;
import com.olodiman11.noir.net.packets.Packet29Deputize;
import com.olodiman11.noir.net.packets.Packet48FBICanvas;
import com.olodiman11.noir.net.packets.Packet50Profile;
import com.olodiman11.noir.net.packets.Packet59Duplicate;
import com.olodiman11.noir.net.packets.Packet66Officers;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.gameModes;

public class Evidence extends Object implements Cloneable{

	private int index, slideH, slideOthH, choiceW, choiceH, offset;
	private boolean hand, choice, selected, othersHand, fadingIn, fadingOut, hovering, hidden,
	exonerated, flipping, scalingUp, scalingDown, flipUp, flipDown;
	private float opacity, fadingSpeed;
	private BufferedImage largeTexture, handTexture, faceDownTexture, smallTexture, smallFaceDown, selectedImg, deceased;
	private double destX, destY, speed, ratio, choiceX, choiceY;
	private float flipIt = 0, flipSpeed;
	private double scaleIt = 1;
	private Card c;
	private long commentTimer, lastTime, now, elapsed;
	public static boolean UP_TO_CHOOSE = false;
	
	public Evidence(Handler handler, double x, double y, Card c) {
		super(handler, x, y);
		this.c = c;
		destX = x;
		destY = y;
		choiceX = x0 + 583 * Window.SCALE;
		choiceY = y0 + 625 * Window.SCALE;
		choiceW = (int) (200 * Window.SCALE);
		choiceH = (int) (100 * Window.SCALE);
		speed = 12.6;
		commentTimer = 400;
		now = 0;
		lastTime = 0;
		index = c.getIndex();
		fadingSpeed = 1.0f / 30;
		offset = (int) (3 * Window.SCALE);
		opacity = 1;
		hand = false;
		choice = false;
		othersHand = false;
		exonerated = false;
		largeTexture = ImageManager.scaleImage(handler.getAssets().getEvidence()[index],
				handler.getGameState().getPreviewW(), handler.getGameState().getPreviewH());
		selectedImg = handler.getAssets().getSelected();
		handTexture = ImageManager.scaleImage(largeTexture,
				(int) (handler.getGameState().getHandH() * 480D / 680D), handler.getGameState().getHandH());
		faceDownTexture = ImageManager.scaleImage(handler.getAssets().getFaceDown(), handTexture.getWidth(), handTexture.getHeight());
		width = handTexture.getWidth();
		height = handTexture.getHeight();
		slideH = (int) (0.7 * height);
		slideOthH = (int) (0.3 * height);
		ratio = (double) largeTexture.getWidth() / (double) handler.getAssets().getEvidence()[index].getWidth();
		deceased = handler.getAssets().getDeceased();
	}

	public void scale(int cardW, int cardH) {
		smallTexture = ImageManager.scaleImage(handler.getAssets().getEvidence()[index], cardW, cardH);
		smallFaceDown = ImageManager.scaleImage(handler.getAssets().getFaceDown(), cardW, cardH);
	}

	public void fadeIn() {
		opacity = 0;
		fadingIn = true;
	}
	
	public void fadeOut() {
		System.out.println(Repository.NAMES[index][1] + " fadingOut");
		opacity = 1;
		fadingOut = true;
	}
	
	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(hovering && UP_TO_CHOOSE) {
			switch(handler.getGameState().getCurrAction()) {
			case Init:
				if(handler.getGameState().getCm().getCard(index) != null
						&& !c.isDead()) {
					if(handler.getGameState().getMode().equals(gameModes.Heist)) {
						if(!((Heist) handler.getGameState().getCurrMode()).isSelected()) {
							selected = !selected;
						} else if(!selected){
							UP_TO_CHOOSE = false;
							for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
								ev.fadeOut();
							}
							Game.sleep(handler.getGameState().getFadingTime());
							ArrayList<Evidence> choicePoll = new ArrayList<Evidence>();
							choicePoll.addAll(handler.getGameState().getCm().getChoicePoll());
							handler.getGameState().getCm().getChoicePoll().clear();
							Packet20Identity identity = new Packet20Identity(handler.getPlayer().getUsername(), handler.getPlayer().getUsername(), index);
							identity.writeData(handler.getSocketClient());
							choice = false;
							handler.getPlayer().setCard(this);
							for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
								ev.setChoice(false);
								handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
								ev.fadeIn();
								ev.setExonerated(true);
							}
							for(Evidence ev: choicePoll) {
								if(!ev.isSelected() && !handler.getPlayer().getCard().equals(ev)) {
									handler.getGameState().getCm().shuffleInDeck(ev);
								}
								ev.setSelected(false);
							}
							int[] ints = new int[((Heist) handler.getGameState().getCurrMode()).getOfficers().size()];
							for(int i = 0; i < ints.length; i++) {
								ints[i] = ((Heist) handler.getGameState().getCurrMode()).getOfficers().get(i).getIndex();
							}
							Packet66Officers officers = new Packet66Officers(handler.getPlayer().getUsername(), ints);
							officers.writeData(handler.getSocketClient());
							handler.getGameState().getCm().sendToShuffle();
							handler.getGameState().setCurrAction(Actions.Idle);
						}
					} else {
						UP_TO_CHOOSE = false;
						for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
							ev.fadeOut();
						}
						Game.sleep(handler.getGameState().getFadingTime());
						Packet20Identity identity = new Packet20Identity(handler.getPlayer().getUsername(), handler.getPlayer().getUsername(), index);
						identity.writeData(handler.getSocketClient());
						returnInHand();
						handler.getPlayer().setCard(this);
						handler.getGameState().setCurrAction(Actions.Idle);
					}
					if(handler.getGameState().getMode().equals(gameModes.KvsI)) {					
						handler.getGameState().getCurrMode().startTurn();
					} else if(handler.getGameState().getMode().equals(gameModes.HvsS) || handler.getGameState().getMode().equals(gameModes.TvsC)
							|| handler.getGameState().getMode().equals(gameModes.Heist)) {
						Packet14Ready packet = new Packet14Ready(handler.getPlayer().getUsername());
						packet.writeData(handler.getSocketClient());
					}
				}
				break;
			case Exonerate:
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
				}
				Game.sleep(handler.getGameState().getFadingTime());
				returnInHand();
				Packet21Exonerate exonerate = new Packet21Exonerate(handler.getPlayer().getUsername(), index);
				exonerate.writeData(handler.getSocketClient());
				if(handler.getGameState().getCm().getCard(index) != null
						&& !c.isDead()) {
					handler.getGameState().getLog().addLine(handler.getPlayer().getUsername() + " оправдывает " + Repository.NAMES[index][0]
							+ "(" + String.valueOf(c.getRow() + 1) + ","
							+ String.valueOf(c.getCol() + 1) + ").");
					choice = false;
					hand = false;
					othersHand = false;
					exonerated = true;
					c.setEv(this);
					c.setExonerated(true);
					fadeIn();
					Game.sleep(1000);
					c.canvas();
				} else {
					if(handler.getGameState().getCm().getCard(index) != null) {								
						handler.getGameState().getLog().addLine(handler.getPlayer().getUsername()
								+ " сбрасывает карту доказательства " + Repository.NAMES[index][1]
								+ "(" + String.valueOf(c.getRow() + 1) + ","
								+ String.valueOf(c.getCol() + 1) + ").");
					} else {
						handler.getGameState().getLog().addLine(handler.getPlayer().getUsername()
								+ " сбрасывает карту доказательства " + Repository.NAMES[index][1]);								
					}
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Это мне уже не пригодится.", handler.getPlayer()));
					handler.getGameState().getCurrMode().endTurn();
				}
				handler.getGameState().getCm().getChoicePoll().clear();
				break;
			case Defend:
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
				}
				Game.sleep(handler.getGameState().getFadingTime());
				returnInHand();
				Packet24Defend defend = new Packet24Defend(handler.getPlayer().getUsername(), index);
				defend.writeData(handler.getSocketClient());
				if(handler.getGameState().getCm().getCard(index) != null
						&& !c.isDead()) {
					handler.getGameState().getLog().addLine(handler.getPlayer().getUsername() + " оправдывает " + Repository.NAMES[index][0]
							+ "(" + String.valueOf(c.getRow() + 1) + ","
							+ String.valueOf(c.getCol() + 1) + ").");
					choice = false;
					hand = false;
					othersHand = false;
					exonerated = true;
					c.setEv(this);
					c.setExonerated(true);
					fadeIn();
					Game.sleep(1000);
					c.canvas();
				} else {
					if(handler.getGameState().getCm().getCard(index) != null) {								
						handler.getGameState().getLog().addLine(handler.getPlayer().getUsername()
								+ " сбрасывает карту доказательства " + Repository.NAMES[index][1]
								+ "(" + String.valueOf(c.getRow() + 1) + ","
								+ String.valueOf(c.getCol() + 1) + ").");
					} else {
						handler.getGameState().getLog().addLine(handler.getPlayer().getUsername()
								+ " сбрасывает карту доказательства " + Repository.NAMES[index][1]);								
					}
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Это мне уже не пригодится.", handler.getPlayer()));
					handler.getGameState().getCurrMode().endTurn();
				}
				handler.getGameState().getCm().getChoicePoll().clear();			
				break;
			case Deputize:
				handler.getGameState().setCurrAction(Actions.Idle);
				System.out.println(index);
				Packet29Deputize deputize = new Packet29Deputize(handler.getPlayer().getUsername(), index);
				deputize.writeData(handler.getSocketClient());
				break;
			case Change:
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
				}
				Game.sleep(handler.getGameState().getFadingTime());
				returnInHand();
				Packet28Change identity = new Packet28Change(handler.getPlayer().getUsername(), index);
				identity.writeData(handler.getSocketClient());
				handler.getPlayer().setCard(this);
				break;
			case FBICanvas:
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
				}
				Game.sleep(handler.getGameState().getFadingTime());
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					if(ev.equals(this)) {
						ev.setX(c.getX());
						ev.setY(c.getY());
						ev.setChoice(false);
						ev.setHand(false);
						ev.setOthersHand(false);
						ev.setExonerated(true);
						c.setEv(this);
						c.setExonerated(true);
						ev.fadeIn();
					} else {
						handler.getGameState().getCm().returnInDeck(ev);
					}
				}
				handler.getGameState().getCm().getChoicePoll().clear();
				Packet48FBICanvas canvas = new Packet48FBICanvas(handler.getPlayer().getUsername(), index);
				canvas.writeData(handler.getSocketClient());
				Game.sleep(1000);
				c.canvas();
				break;
			case Profile:
				if(handler.getGameState().getCm().getCard(index) != null
						&& c.isDead()) {
					return;
				}
				ArrayList<Evidence> dead = new ArrayList<Evidence>();
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
					if(handler.getGameState().getCm().getCard(ev.getIndex()) != null
							&& handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
						dead.add(ev);
					}
				}
				Game.sleep(handler.getGameState().getFadingTime());
				returnInHand();
				Evidence[] evs = new Evidence[dead.size()];
				for(int i = 0; i < evs.length; i++){
					evs[i] = dead.get(i);
				}
				handler.getPlayer().removeFromHand(evs);
				Packet50Profile profile = new Packet50Profile(handler.getPlayer().getUsername(), index);
				profile.writeData(handler.getSocketClient());			
				choice = false;
				hand = false;
				othersHand = false;
				exonerated = true;
				c.setEv(this);
				c.setExonerated(true);
				fadeIn();
				Game.sleep(1000);
				c.canvas();
				Game.sleep(1000);
				Packet19Draw draw = new Packet19Draw(handler.getPlayer().getUsername(), true, false, false,
						4 - handler.getPlayer().getHand().size());
				draw.writeData(handler.getSocketClient());
				break;
			case Duplicate:
				UP_TO_CHOOSE = false;
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.fadeOut();
				}
				choice = false;
				Evidence e = handler.getPlayer().getCard();
				handler.getPlayer().removeIdentity(false);
				handler.getGameState().getCm().shuffleInDeck(e);
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					if(ev.getIndex() != index) {
						handler.getGameState().getCm().shuffleInDeck(ev);
					}
				}
				handler.getGameState().getCm().sendToShuffle();
				handler.getPlayer().setCard(this);
				Packet59Duplicate duplicate = new Packet59Duplicate(handler.getPlayer().getUsername(), index);
				duplicate.writeData(handler.getSocketClient());
				handler.getGameState().getCm().getChoicePoll().clear();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}
	
	private void returnInHand() {
		Evidence[] evs = new Evidence[handler.getGameState().getCm().getChoicePoll().size() - 1];
		int ind = 0;
		for (int i = 0; i < handler.getGameState().getCm().getChoicePoll().size(); i++) {
			Evidence ev = handler.getGameState().getCm().getChoicePoll().get(i);
			if(ev.equals(this)) {
				ev.setChoice(false);
				ev.setHand(false);
				ev.setOthersHand(false);
				ev.setExonerated(false);
				continue;
			}
			evs[ind++] = ev;
		}
		handler.getPlayer().addInHand(evs);
		handler.getGameState().getCm().getChoicePoll().clear();
	}
	
	public void move(double x, double y) {
		destX = x;
		destY = y;
	}
	
	public void move(double x, double y, double xTo, double yTo) {
		this.x = x;
		this.y = y;
		destX = xTo;
		destY = yTo;
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
				
		int width = exonerated ? smallTexture.getWidth() : handTexture.getWidth();
		int height = exonerated ? smallTexture.getHeight() : handTexture.getHeight();
		
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
		if(hidden) {		
			if(flipSpeed > 1.570796) {		
				if(!exonerated) {
					op = ImageManager.createWarpImage(faceDownTexture, warpPerspective);
				} else {
					op = ImageManager.createWarpImage(smallFaceDown, warpPerspective);					
				}
			} else {
				if(!exonerated) {
					op = ImageManager.createWarpImage(handTexture, warpPerspective);
				} else {
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);					
				}
			}
		} else {
			if(flipSpeed > 1.570796) {
				if(!exonerated) {
					op = ImageManager.createWarpImage(handTexture, warpPerspective);
				} else {
					op = ImageManager.createWarpImage(smallTexture, warpPerspective);					
				}
			} else {				
				if(!exonerated) {
					op = ImageManager.createWarpImage(faceDownTexture, warpPerspective);
				} else {
					op = ImageManager.createWarpImage(smallFaceDown, warpPerspective);					
				}
			}
		}
		RenderedImage image = op.createInstance();		
		g2d.drawRenderedImage(image, new AffineTransform(scaleIt - Math.abs(flipIt), 0, 0, scaleIt,
				x + width / 2 * (Math.abs(flipIt) - (scaleIt - 1)), y - height / 2 * (scaleIt - 1)));
		
		if(flipSpeed > 3.14159) {
			flipSpeed = 0;
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
	
	@Override
	public void tick() {
		
		Player p = null;
		for(Player pl: handler.getConnectedPlayers()) {
			for(Evidence ev: pl.getHand()) {
				if(ev.equals(this)) {
					p = pl;
					break;
				}
			}
			if(p != null) {
				break;
			}
		}
		
		int size, space, handX, handY, handW, index;
		
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		double xOffset = 0;
		double yOffset = 0;
		if(p != null) {			
			if(hand || othersHand) {
				size = p.getHand().size();
				space = handler.getGameState().getCm().getHandSpace();
				handX = x0 + handler.getGameState().getHandX();
				handY = y0 + handler.getGameState().getHandY();
				handW = handler.getGameState().getHandW();
				index = p.getHand().indexOf(this);
				if(size * width + (size - 1) * space > handW) {
					if(index != size - 1) {
						xOffset = width - (x - p.getHand().get(index + 1).getX()); 
					}
				} else {
					xOffset = 0;
				}
				if(othersHand) {
					if(p.getNamePlate().isHovering()) {
						if(hovering) {
							yOffset = -handTexture.getHeight() + slideH;
						} else if(p.getNamePlate().isHovering()){
							yOffset = -handTexture.getHeight() + slideOthH;
						} else {
							yOffset = -handTexture.getHeight();
						}
					} else {
						yOffset = -handTexture.getHeight();
					}
				}
			}
		}
		
		if(choice) {
			if(handler.getGameState().getCm().getChoicePoll().size() > 4) {
				if(handler.getGameState().getCm().getChoicePoll().indexOf(this) % 2 == 0) {
					int i = handler.getGameState().getCm().getChoicePoll().indexOf(this);
					if(i == 0) {
						if(!handler.getGameState().getCm().getChoicePoll().get(i + 1).isHovering()
						&& mouseX > x && mouseX < x + largeTexture.getWidth()
						&& mouseY > y && mouseY < y + largeTexture.getHeight()) {
							hovering = true;
						} else {
							hovering = false;
						}
					} else if(i == handler.getGameState().getCm().getChoicePoll().size() - 1) {
						if(!handler.getGameState().getCm().getChoicePoll().get(i - 1).isHovering()
						&& mouseX > x && mouseX < x + largeTexture.getWidth()
						&& mouseY > y && mouseY < y + largeTexture.getHeight()) {
							hovering = true;
						} else {
							hovering = false;
						}
					} else {
						if(!handler.getGameState().getCm().getChoicePoll().get(i - 1).isHovering()
						&& !handler.getGameState().getCm().getChoicePoll().get(i + 1).isHovering()
						&& mouseX > x && mouseX < x + largeTexture.getWidth()
						&& mouseY > y && mouseY < y + largeTexture.getHeight()) {
							hovering = true;
						} else {
							hovering = false;
						}
					}
				} else {
					if(mouseX > x && mouseX < x + largeTexture.getWidth()
					&& mouseY > y && mouseY < y + largeTexture.getHeight()) {
						hovering = true;
					} else {
						hovering = false;
					}
				}
			} else {				
				if(mouseX > x && mouseX < x + largeTexture.getWidth()
				&& mouseY > y && mouseY < y + largeTexture.getHeight()) {
					hovering = true;
				} else {
					hovering = false;
				}
			}
			if(!fadingIn && !fadingOut) {				
				if(mouseX > choiceX && mouseX < choiceX + choiceW
				&& mouseY > choiceY && mouseY < choiceY + choiceH) {
					opacity -= fadingSpeed * 2;
					if(opacity <= 0) {
						opacity = 0;
					}
				} else {
					opacity += fadingSpeed * 2;
					if(opacity >= 1) {
						opacity = 1;
					}
				}
			}
			if(!handler.getGameState().getCurrAction().equals(Actions.Init)
					&& handler.getGameState().getCurrMode() instanceof KvsI) {						
				if(hovering && c.isDead()) {
					lastTime = now;
					now = System.currentTimeMillis();
					elapsed += now - lastTime;
					if(elapsed >= commentTimer) {
						handler.getSm().createComment("Выбрав эту карту, вы сбросите её. После этого ваш ход закончится.",
								this, true, largeTexture.getWidth(), largeTexture.getHeight());
					}
				} else if(c.isDead()){
					now = System.currentTimeMillis();
					elapsed = 0;
					if(handler.getSm().getComment() != null && handler.getSm().getComment().getObj().equals(this)) {
						handler.getSm().setComment(null);
					}
				}
			}
		} else if(mouseX > x + xOffset && mouseX < x + width
			&& mouseY > y - yOffset && mouseY < y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
		
		if(fadingIn) {
			opacity += fadingSpeed;
			if(opacity >= 1) {
				opacity = 1;
				fadingIn = false;
			}
			if(choice) {
				if(!handler.getGameState().getCm().getChoicePoll().get(0).equals(this)
				&& handler.getGameState().getCm().getChoicePoll().get(0).getOpacity() < opacity) {
					fadingIn = false;
					opacity = handler.getGameState().getCm().getChoicePoll().get(0).getOpacity();
				}
			}
		}
		if(fadingOut) {
			opacity -= fadingSpeed;
			if(opacity <= 0) {
				opacity = 0;
				fadingOut = false;
			}
		}
		
		if(x != destX || y != destY) {
			double minSpeed = speed * 0.6;
			double distX = destX - x != 0 && Math.abs(destX - x) < minSpeed ? Math.signum(destX - x) * minSpeed : destX - x;
			double distY = destY - y != 0 && Math.abs(destY - y) < minSpeed ? Math.signum(destY - y) * minSpeed : destY - y;
			x += distX / speed;
			y += distY / speed;
			
			int minDist = 1;
			if(Math.abs(destX - x) <= minDist) {
				x = destX;
			}
			if(Math.abs(destY - y) <= minDist) {
				y = destY;
			}
		}
		if(p != null) {		
			if(hand) {
				size = p.getHand().size();
				space = handler.getGameState().getCm().getHandSpace();
				handX = x0 + handler.getGameState().getHandX();
				handY = y0 + handler.getGameState().getHandY();
				handW = handler.getGameState().getHandW();
				index = p.getHand().indexOf(this);
				if(size * width + (size - 1) * space > handW) {
					destX = handX + (handW - width) / (size - 1) * (size - index - 1);
				} else {
					destX = handX + (handW - size * width - (size - 1) * space) / 2 + (width + space) * (size - index - 1);
				}
				if(hovering) {
					destY = handY - slideH;
					height = handTexture.getHeight() + slideH;
				} else {
					destY = handY;
					height = handTexture.getHeight();
				}
			} else if(othersHand) {
				size = p.getHand().size();
				space = handler.getGameState().getCm().getHandSpace();
				handX = x0 + handler.getGameState().getOthHandX();
				handY = y0 + handler.getGameState().getOthHandY();
				handW = p.getNamePlate().getWidth() - (int) (20 * Window.SCALE);
				index = p.getHand().indexOf(this);
				if(size * width + (size - 1) * space > handW) {
					destX = p.getNamePlate().getX() + handX + (handW - width) / (size - 1) * (size - index - 1);
				} else {
					destX = p.getNamePlate().getX() + handX + (handW - size * width - (size - 1) * space) / 2
							+ (width + space) * (size - index - 1);
				}
				if(p.getNamePlate().isHovering()) {
					if(hovering) {
						destY = handY - handTexture.getHeight() + slideH;
						height = handTexture.getHeight();					
					} else {
						destY = handY - handTexture.getHeight() + slideOthH;
						height = handTexture.getHeight();					
					}
				} else {
					destY = handY - handTexture.getHeight();
					height = handTexture.getHeight();
				}
			}
		} else if(exonerated) {
			double x = c.getX();
			double y = c.getY();
			destX = x + offset;
			destY = y + offset;
			this.x = x + offset;
			this.y = y + offset;
			if(!fadingIn && !fadingOut) {
				opacity = c.getOpacity();
			}
		}
		
	}

	@Override
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		BufferedImage texture;
		if(flipping) {
			flip(g2d);	
		}else {
			if(hidden) {
				if(exonerated) {
					texture = smallFaceDown;
				}	else {				
					texture = faceDownTexture;
				}
			} else if(choice) {
				if(hovering) {
					g.drawImage(handler.getAssets().getHighlight(),
							(int) (x - Math.floor(20 * ratio)), (int) (y - Math.floor(20 * ratio)),
							(int) (largeTexture.getWidth() + 40 * ratio),
							(int) (largeTexture.getHeight() + 40 * ratio),null);
				}
				texture = largeTexture;
			} else if(exonerated) {
				texture = smallTexture;
			} else {			
				texture = handTexture;
			}
			g2d.drawImage(texture, (int) x, (int) y, null);
			if(selected) {
				g2d.drawImage(selectedImg, (int) x, (int) y, null);
			}
			if(c.isDead() && !hidden && !exonerated) {
				g2d.drawImage(deceased, (int) x, (int) y, texture.getWidth(), texture.getHeight(), null);
			}
		}
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}

	public int getIndex() {
		return index;
	}

	public BufferedImage getLargeEvidenceTexture() {
		return largeTexture;
	}

	public boolean isHovering() {
		return hovering;
	}
	
	@Override
	public void setX(double x) {
		this.x = x;
		destX = x;
	}
	
	@Override
	public void setY(double y) {
		this.y = y;
		destY = y;
	}

	public BufferedImage getHandTexture() {
		return handTexture;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}

	public void setHand(boolean hand) {
		this.hand = hand;
	}

	public void setOthersHand(boolean othersHand) {
		this.othersHand = othersHand;
	}
	
	public void setExonerated(boolean exonerated) {
		this.exonerated = exonerated;
	}

	public float getOpacity() {
		return opacity;
	}

	public boolean isHidden() {
		return hidden;
	}
	
	public void flip() {
		hidden = !hidden;
		scalingDown = false;
		scalingUp = true;
		flipDown = false;
		if(flipUp) {			
			flipUp = false;
		} else {
			flipUp = true;
		}
		flipping = true;
	}
	
	public void flipNoScale() {
		hidden = !hidden;
		flipDown = false;
		if(flipUp) {			
			flipUp = false;
		} else {
			flipUp = true;
		}
		flipping = true;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isExonerated() {
		return exonerated;
	}
	
	public void setCard(Card c) {
		this.c = c;
	}

	public BufferedImage getFaceDownTexture() {
		return faceDownTexture;
	}

	public BufferedImage getLargeTexture() {
		return largeTexture;
	}

	public BufferedImage getSmallTexture() {
		return smallTexture;
	}

	public BufferedImage getSmallFaceDown() {
		return smallFaceDown;
	}
	
}
