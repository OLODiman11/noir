package com.olodiman11.noir.objects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.gamemodes.TvsC;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet13Deck;
import com.olodiman11.noir.net.packets.Packet14Ready;
import com.olodiman11.noir.net.packets.Packet67Shuffle;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.gameModes;

public class CardsManager {
	
	private Handler handler;
	private ArrayList<Card> deck;
	private ArrayList<Evidence> evDeck;
	private ArrayList<Evidence> evidence;
	private int numCols, numRows;
	private int boardW, boardH;
	private int cardW, cardH;
	private int space, handSpace;
	private int x0, y0, x1, y1, boardX, boardY, finX, finY;
	private Card[][] map;
	private Card tempCard, tempCard2;
	private boolean scaling;
	private ArrayList<Card> dyingOut;
	private ArrayList<Evidence> temps;
	private ArrayList<Evidence> choicePoll;
	private ArrayList<int[]> toShuffle;
	private BufferedImage[] tokens;
	private BufferedImage[] capture, canvas, disable, silence, accuse, steal, kill, murder,
	autopsy, threat, bomb, protectionAdd, protectionRemove, swap, swapFrom, swapTo, detonate;
	private BufferedImage tknHighlight;
	private float flipRate, scaleRate;
	public static ArrowLine AL;
	
	public CardsManager(Handler handler) {
		
		this.handler = handler;
		
		scaling = false;
		
		setFlipRate(0.15f);
		setScaleRate(0.05f);
		
		tokens = new BufferedImage[handler.getAssets().getTokens().length];
		dyingOut = new ArrayList<Card>();
		temps = new ArrayList<Evidence>();
		evDeck = new ArrayList<Evidence>();
		choicePoll = new ArrayList<Evidence>();
		deck = new ArrayList<Card>();
		evidence = new ArrayList<Evidence>();
		toShuffle = new ArrayList<int[]>();
		capture = new BufferedImage[3];
		canvas = new BufferedImage[3];
		disable = new BufferedImage[3];
		silence = new BufferedImage[3];
		accuse = new BufferedImage[3];
		steal = new BufferedImage[3];
		kill = new BufferedImage[3];
		murder = new BufferedImage[3];
		autopsy = new BufferedImage[3];
		threat = new BufferedImage[3];
		bomb = new BufferedImage[3];
		protectionAdd = new BufferedImage[3];
		protectionRemove = new BufferedImage[3];
		swap = new BufferedImage[3];
		swapFrom = new BufferedImage[3];
		swapTo = new BufferedImage[3];
		detonate = new BufferedImage[3];
		
		handSpace = (int) (3 * Window.SCALE);
		space = (int) (10 * Window.SCALE);
		
		this.boardW = handler.getGameState().getBoardW();
		this.boardH = handler.getGameState().getBoardH();
		this.boardX = handler.getGameState().getBoardX();
		this.boardY = handler.getGameState().getBoardY();
		x0 = handler.getFrameWidth() / 2 - handler.getWidth() / 2;
		y0 = handler.getFrameHeight() / 2 - handler.getHeight() / 2;
		
		for(int i = 0; i < tokens.length; i++) {
			tokens[i] = handler.getAssets().getTokens()[i];
		}
		tknHighlight = handler.getAssets().getTknHighlight();
		
	}
	
	public void createDecks() {
		for(int i = 0; i < 50; i++) {
			deck.add(new Card(handler, -500, 0, i));
			evidence.add(new Evidence(handler, -500, 0, deck.get(i)));
		}
	}
	
	public void createEvDeck() {
		numCols = handler.getGameState().getNumCols();
		numRows = handler.getGameState().getNumRows();
		
		Random rand = new Random();
		int index;
		int[] deck = new int[numCols * numRows];
		for(int i = 0; i < numCols * numRows; i++) {
			index = rand.nextInt(numCols * numRows - i);
			evDeck.add(evidence.get(index));
			deck[i] = evidence.get(index).getIndex();
			evidence.remove(index);
		}
		
		Packet13Deck packet = new Packet13Deck(handler.getPlayer().getUsername(), deck);
		handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
	}
	
	public void createEvDeck(int[] indexes) {
		numCols = handler.getGameState().getNumCols();
		numRows = handler.getGameState().getNumRows();
		
		for(int i = 0; i < numCols * numRows; i++) {
			evDeck.add(evidence.get(indexes[i]));	
		}
	}
	
	public void scaleCards() {
		
		numCols = handler.getGameState().getNumCols();
		numRows = handler.getGameState().getNumRows();

		cardW = (boardW - space * (numCols - 1)) / numCols;
		cardH = (int) (cardW * 680D / 480D);
		if(cardH > (boardH - space * (numRows - 1)) / numRows) {
			cardH = (boardH - space * (numRows - 1)) / numRows;
			cardW = (int) (cardH * 480D / 680D);
		}
		
		x1 = (boardW - (cardW * numCols + space * (numCols - 1))) / 2;
		y1 = (boardH - (cardH * numRows + space * (numRows - 1))) / 2;
		
		finX = x0 + boardX + x1;
		finY = y0 + boardY + y1;
		
		if(map == null) {
			for(int i = 0; i < numCols * numRows; i++) {
				deck.get(i).scale(cardW, cardH);
				evidence.get(i).scale(cardW, cardH);
			}
		}
		
		double ratio = (double) cardW / 290D;
		for(int i = 0; i < tokens.length; i++) {
			tokens[i] = ImageManager.scaleImage(handler.getAssets().getTokens()[i],
					(int) (handler.getAssets().getTokens()[i].getWidth() * ratio),
					(int) (handler.getAssets().getTokens()[i].getHeight() * ratio));
		}
		tknHighlight = ImageManager.scaleImage(handler.getAssets().getTknHighlight(),
				(int) (handler.getAssets().getTknHighlight().getWidth() * ratio),
				(int) (handler.getAssets().getTknHighlight().getHeight() * ratio));
		
		scaleIcons();
			
	}
	
	public void scaleIcons() {
		for(int i = 0; i < 3; i++) {			
			switch(handler.getPlayer().getRole()) {
			case Bomber:
				bomb[i] = ImageManager.scaleImage(handler.getAssets().getBomb()[i], cardW, cardW);
				detonate[i] = ImageManager.scaleImage(handler.getAssets().getDetonate()[i], cardW, cardW);
				break;
			case Silencer:
			case Cleaner:
			case Infiltrator:
			case Insider:
				silence[i] = ImageManager.scaleImage(handler.getAssets().getSilence()[i], cardW, cardW);
				disable[i] = ImageManager.scaleImage(handler.getAssets().getDisable()[i], cardW, cardW);
				swap[i] = ImageManager.scaleImage(handler.getAssets().getSwap()[i], cardW, cardW);
				break;
			case ChiefOfPolice:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Detective:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Hitman:
				kill[i] = ImageManager.scaleImage(handler.getAssets().getKill()[i], cardW, cardW);
				break;
			case Inspector:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Killer:
				murder[i] = ImageManager.scaleImage(handler.getAssets().getMurder()[i], cardW, cardW);
				break;
			case MasterThief:
				steal[i] = ImageManager.scaleImage(handler.getAssets().getSteal()[i], cardW, cardW);
				break;
			case Profiler:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Psycho:
				threat[i] = ImageManager.scaleImage(handler.getAssets().getThreat()[i], cardW, cardW);
				swap[i] = ImageManager.scaleImage(handler.getAssets().getSwap()[i], cardW, cardW);
				swapFrom[i] = ImageManager.scaleImage(handler.getAssets().getSwapFrom()[i], cardW, cardW);
				swapTo[i] = ImageManager.scaleImage(handler.getAssets().getSwapTo()[i], cardW, cardW);
				break;
			case SecurityChief:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				swap[i] = ImageManager.scaleImage(handler.getAssets().getSwap()[i], cardW, cardW);
				swapFrom[i] = ImageManager.scaleImage(handler.getAssets().getSwapFrom()[i], cardW, cardW);
				swapTo[i] = ImageManager.scaleImage(handler.getAssets().getSwapTo()[i], cardW, cardW);
				break;
			case Sleuth:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Sniper:
				kill[i] = ImageManager.scaleImage(handler.getAssets().getKill()[i], cardW, cardW);
				break;
			case Spy:
				capture[i] = ImageManager.scaleImage(handler.getAssets().getCapture()[i], cardW, cardW);
				canvas[i] = ImageManager.scaleImage(handler.getAssets().getCanvas()[i], cardW, cardW);
				break;
			case Suit:
				protectionAdd[i] = ImageManager.scaleImage(handler.getAssets().getProtectionAdd()[i], cardW, cardW);
				protectionRemove[i] = ImageManager.scaleImage(handler.getAssets().getProtectionRemove()[i], cardW, cardW);
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				break;
			case Thug:
				murder[i] = ImageManager.scaleImage(handler.getAssets().getMurder()[i], cardW, cardW);
				break;
			case Undercover:
				accuse[i] = ImageManager.scaleImage(handler.getAssets().getAccuse()[i], cardW, cardW);
				autopsy[i] = ImageManager.scaleImage(handler.getAssets().getAutopsy()[i], cardW, cardW);
				break;
			default:
				break;
		}
		
		}
	}
	
	public void layoutCards() {
		
		Random rand = new Random();
		int cardInd;
		
		map = new Card[numRows][numCols];
		
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				cardInd = rand.nextInt(numCols * numRows - (col + row * numCols));
				Card c = deck.get(cardInd);
				c.setRow(row);
				c.setCol(col);
				c.setFinX(finX);
				c.setFinY(finY);
				c.setX(finX + cardW * col + space * col);
				c.setY(finY + cardH * row + space * row);
				map[row][col] = c;
				deck.remove(cardInd);
			}
		}
		
	}
	
	public void layoutCards(int[][] mapIndex) {
		map = new Card[numRows][numCols];
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				int index = 0;
				for(int i = 0; i < numCols * numRows - (col + numCols * row); i++) {
					if(mapIndex[row][col] == deck.get(i).getIndex()) {
						index = i;
						break;
					}
				}
				Card c = deck.get(index);
				c.setCol(col);
				c.setRow(row);
				c.setFinX(finX);
				c.setFinY(finY);
				c.setX(finX + cardW * col + space * col);
				c.setY(finY + cardH * row + space * row);
				map[row][col] = c;
				deck.remove(index);
			}
		}
	}
	
	public void initFastMovement(int dir, int rc) {
		
		Card c;
		Card temp, temp2;
		
		switch(dir) {
		case ShiftButton.RIGHT:
			temp = map[rc][0];
			temp2 = map[rc][1];
			for(int i = 0; i < numCols; i++) {
				if(i >= 2) {
					c = temp2;
					temp2 = temp;
				} else {
					temp2 = temp;
					c = map[rc][i];					
				}
				if(i + 2 >= numCols) {
					if(i + 1 == numCols) {						
						createTempCard(c, rc, i + 2);
					} else if(i + 2 == numCols) {
						createFastTempCard(c, rc, i + 2);
					}
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setX(finX - (numCols - i) * cardW - (numCols - i) * space + t.getX() - c.getX());
						}
					}
					c.setOpacity(0f);
					c.setX(finX - (numCols - i) * cardW - (numCols - i) * space);
//					c.fadeIn();
					c.setCol(2 - (numCols - i));
					map[rc][2 - (numCols - i)] = c;
				} else {
					temp = map[rc][i + 2];
					c.setCol(i + 2);
					map[rc][i + 2] = c;
				}
			}
			break;
		case ShiftButton.LEFT:
			temp = map[rc][numCols - 1];
			temp2 = map[rc][numCols - 2];
			for(int i = numCols - 1; i >= 0; i--) {
				if(i <= numCols - 3) {
					c = temp2;
					temp2 = temp;
				} else {
					temp2 = temp;
					c = map[rc][i];					
				}
				if(i - 2 < 0) {
					if(i == 0) {						
						createTempCard(c, rc, i - 2);
					} else if(i == 1) {
						createFastTempCard(c, rc, i - 2);
					}
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setX(finX + cardW * (numCols + i) + space * (numCols + i) + t.getX() - c.getX());
						}
					}
					c.setOpacity(0f);
					c.setX(finX +  cardW * (numCols + i) + space * (numCols + i));
//					c.fadeIn();
					c.setCol(numCols - 2 + i);
					map[rc][numCols - 2 + i] = c;
				} else {
					temp = map[rc][i - 2];
					c.setCol(i - 2);
					map[rc][i - 2] = c;
				}
			}
			break;
		case ShiftButton.DOWN:
			temp = map[0][rc];
			temp2 = map[1][rc];
			for(int i = 0; i < numRows; i++) {
				if(i >= 2) {
					c = temp2;
					temp2 = temp;
				} else {
					temp2 = temp;
					c = map[i][rc];					
				}
				if(i + 2 >= numRows) {
					if(i + 1 == numRows) {						
						createTempCard(c, i + 2, rc);
					} else if(i + 2 == numRows) {
						createFastTempCard(c, i + 2, rc);
					}
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setY(finY - (numRows - i) * cardH - (numRows - i) * space + t.getY() - c.getY());
						}
					}
					c.setOpacity(0f);
					c.setY(finY - (numRows - i) * cardH - (numRows - i) * space);
//					c.fadeIn();
					c.setRow(2 - (numRows - i));
					map[2 - (numRows - i)][rc] = c;
				} else {
					temp = map[i + 2][rc];
					c.setRow(i + 2);
					map[i + 2][rc] = c;
				}
			}
			break;
		case ShiftButton.UP:
			temp = map[numRows - 1][rc];
			temp2 = map[numRows - 2][rc];
			for(int i = numRows - 1; i >= 0; i--) {
				if(i <= numRows - 3) {
					c = temp2;
					temp2 = temp;
				} else {
					temp2 = temp;
					c = map[i][rc];					
				}
				if(i - 2 < 0) {
					if(i == 0) {						
						createTempCard(c, i - 2, rc);
					} else if(i == 1) {
						createFastTempCard(c, i - 2, rc);
					}
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setY(finY + cardH * (numRows + i) + space * (numRows + i) + t.getY() - c.getY());
						}
					}
					c.setOpacity(0f);
					c.setY(finY + cardH * (numRows + i) + space * (numRows + i));
//					c.fadeIn();
					c.setRow(numRows - 2 + i);
					map[numRows - 2 + i][rc] = c;
				} else {
					temp = map[i - 2][rc];
					c.setRow(i - 2);
					map[i - 2][rc] = c;
				}
			}
			break;
		}
	}
	
	private void createFastTempCard(Card c, int row, int col) {
		tempCard2 = (Card) c.clone();
		tempCard2.setOpacity(1f);
		tempCard2.setRow(row);
		tempCard2.setCol(col);
		if(tempCard2.getEv() != null) {
			tempCard2.setEv((Evidence) c.getEv().clone());
			tempCard2.getEv().setCard(tempCard2);
		}
		if(!tempCard2.getTokens().isEmpty()) {
			tempCard2.setTokens(new ArrayList<Token>());
			for(Token t: c.getTokens()) {
				Token tkn = new Token(handler, t.getType(), tempCard2);
				tempCard2.addToken(tkn);
				tkn.setOpacity(1f);
			}
		}
	}
	
	public void initMovement(int dir, int rc) {
		
		Card c;
		Card temp;
		
		switch(dir) {
		case ShiftButton.RIGHT:
			temp = map[rc][0];
			for(int i = 0; i < numCols; i++) {
				c = temp;
				if(i + 1 >= numCols) {
					createTempCard(c, rc, i + 1);
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setX(finX - cardW - space + t.getX() - c.getX());
						}
					}
					c.setOpacity(0f);
					c.setX(finX - cardW - space);
//					c.fadeIn();
					c.setCol(0);
					map[rc][0] = c;
				} else {
					temp = map[rc][i + 1];
					c.setCol(i + 1);
					map[rc][i + 1] = c;
				}
			}
			break;
		case ShiftButton.LEFT:
			temp = map[rc][numCols - 1];
			for(int i = numCols - 1; i >= 0; i--) {
				c = temp;
				if(i - 1 < 0) {
					createTempCard(c, rc, i - 1);
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setX(finX + cardW * numCols + space * numCols + t.getX() - c.getX());
						}
					}
					c.setOpacity(0f);
					c.setX(finX + cardW * numCols + space * numCols);
//					c.fadeIn();
					c.setCol(numCols - 1);
					map[rc][numCols - 1] = c;
				} else {
					temp = map[rc][i - 1];
					c.setCol(i - 1);
					map[rc][i - 1] = c;
				}
			}
			break;
		case ShiftButton.DOWN:
			temp = map[0][rc];
			for(int i = 0; i < numRows; i++) {
				c = temp;
				if(i + 1 >= numRows) {
					createTempCard(c, i + 1, rc);
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setY(finY - cardH - space + t.getY() - c.getY());
						}
					}
					c.setOpacity(0f);
					c.setY(finY - cardH - space);
//					c.fadeIn();
					c.setRow(0);
					map[0][rc] = c;
				} else {
					temp = map[i + 1][rc];
					c.setRow(i + 1);
					map[i + 1][rc] = c;
				}
			}
			break;
		case ShiftButton.UP:
			temp = map[numRows - 1][rc];
			for(int i = numRows - 1; i >= 0; i--) {
				c = temp;
				if(i - 1 < 0) {
					createTempCard(c, i - 1, rc);
					if(!c.getTokens().isEmpty()) {
						for(Token t: c.getTokens()) {
							t.setY(finY + cardH * numRows + space * numRows + t.getY() - c.getY());
						}
					}
					c.setOpacity(0f);
					c.setY(finY + cardH * numRows + space * numRows);
//					c.fadeIn();
					c.setRow(numRows - 1);
					map[numRows - 1][rc] = c;
				} else {
					temp = map[i - 1][rc];
					c.setRow(i - 1);
					map[i - 1][rc] = c;
				}
			}
			break;
		}
	}
	
	public void createTempCard(Card c, int row, int col) {
		tempCard = (Card) c.clone();
		tempCard.setOpacity(1f);
		tempCard.setRow(row);
		tempCard.setCol(col);
		if(tempCard.getEv() != null) {
			tempCard.setEv((Evidence) c.getEv().clone());
			tempCard.getEv().setCard(tempCard);
		}
		if(!tempCard.getTokens().isEmpty()) {
			tempCard.setTokens(new ArrayList<Token>());
			for(Token t: c.getTokens()) {
				Token tkn = new Token(handler, t.getType(), tempCard);
				tempCard.addToken(tkn);
				tkn.setOpacity(1f);
			}
		}
//		tempCard.fadeOut();
	}
	
	public void tick() {
	
		if(!dyingOut.isEmpty()) {
			for(Card c: dyingOut) {
				c.dieOut();
				if(c.getOpacity() <= 0) {
					dyingOut.clear();
				}
			}
		}

		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				if(map[row][col] != null) {
					map[row][col].tick();
				}
			}
		}
		
		if(tempCard != null) {
			tempCard.tick();
			if(tempCard.getOpacity() <= 0) {
				tempCard = null;
			}
		}
		if(tempCard2 != null) {
			tempCard2.tick();
			if(tempCard2.getOpacity() <= 0) {
				tempCard2 = null;
			}
		}
		
		try {			
			if(!temps.isEmpty()) {
				for(Evidence ev: temps) {
					ev.tick();
				}
			}

			if(!choicePoll.isEmpty()) {
				for(Evidence ev: choicePoll) {
					ev.tick();
				}
			}
		} catch(ConcurrentModificationException e) {}
		
	}
	
	public void render(Graphics g) {
		
		if(!dyingOut.isEmpty()) {
			for(Card c: dyingOut) {
				c.render(g);
			}
		}
		
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				if(map[row][col] != null) {
					map[row][col].render(g);
				}
			}
		}
		
		if(tempCard != null) {
			tempCard.render(g);
			if(tempCard.getEv() != null) {
				tempCard.getEv().render(g);
			}
			if(!tempCard.getTokens().isEmpty()) {
				for(Token t: tempCard.getTokens()) {
					t.render(g);
				}
			}
		}
		
		if(tempCard2 != null) {
			tempCard2.render(g);
			if(tempCard2.getEv() != null) {
				tempCard2.getEv().render(g);
			}
			if(!tempCard2.getTokens().isEmpty()) {
				for(Token t: tempCard2.getTokens()) {
					t.render(g);
				}
			}
		}
		
		if(AL != null) {
			AL.render((Graphics2D) g);
		}
		
	}
	
	public void deactivateAll() {
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				handler.getGameState().getMap()[row][col].setActive(false);
			}
		}
	}
	
	public void deselectAll() {
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				handler.getGameState().getMap()[row][col].setSelected(false);
			}
		}
	}
	
	public void sendToShuffle() {
		int[][] cards = new int[toShuffle.size()][2];
		for(int i = 0; i < cards.length; i++) {
			cards[i][0] = toShuffle.get(i)[0];
			cards[i][1] = toShuffle.get(i)[1];
		}
		Packet67Shuffle packet = new Packet67Shuffle(handler.getPlayer().getUsername(), cards);
		packet.writeData(handler.getSocketClient());
		toShuffle.clear();
	}
	
	public void shuffleInDeck(Evidence ev, int index) {
		ev.setHand(false);
		ev.setChoice(false);
		ev.setOthersHand(false);
		handler.getGameState().getCm().getTemps().add(ev);
		ArrayList<Evidence> newEvDeck = new ArrayList<Evidence>();
		double x = x0 + handler.getWidth() / 2 - ev.getHandTexture().getWidth() / 2;
		double y = y0 + handler.getHeight() / 2 - ev.getHandTexture().getHeight() / 2;
		ev.move(x, y, x, y0 + handler.getHeight());
		ev.fadeIn();
		Game.sleep(handler.getGameState().getFadingTime());
		for(int i = 0; i < index; i++) {
			newEvDeck.add(evDeck.get(i));
		}
		newEvDeck.add(ev);
		for(int i = index; i < evDeck.size(); i++) {
			newEvDeck.add(evDeck.get(i));
		}
		evDeck.clear();
		evDeck.addAll(newEvDeck);
	}
	
	public void shuffleInDeck(Evidence ev) {
		int index = new Random().nextInt(evDeck.size());
		shuffleInDeck(ev, index);
		toShuffle.add(new int[] {ev.getIndex(), index});
	}
	
	public void returnInDeck(Evidence ev) {
		ev.setHand(false);
		ev.setChoice(false);
		ev.setOthersHand(false);
		handler.getGameState().getCm().getTemps().add(ev);
		ArrayList<Evidence> newEvDeck = new ArrayList<Evidence>();
		double x = x0 + handler.getWidth() / 2 - ev.getHandTexture().getWidth() / 2;
		double y = y0 + handler.getHeight() / 2 - ev.getHandTexture().getHeight() / 2;
		ev.move(x, y, x, y0 + handler.getHeight());
		ev.fadeIn();
		Game.sleep(handler.getGameState().getFadingTime());
		evDeck.remove(ev);
		newEvDeck.add(ev);
		newEvDeck.addAll(evDeck);
		handler.getGameState().getCm().setEvDeck(newEvDeck);
		handler.getGameState().getCm().getTemps().remove(ev);
	}
	
	public void drawCard(Player p, boolean hand, boolean choice, boolean identity, boolean hidden, int amm) {
		for(int i = 0; i < amm; i++) {
			Evidence ev = null;
			if(!evDeck.isEmpty()) {
				ev = this.evDeck.get(this.evDeck.size() - 1);				
			}
			ArrayList<Evidence> evDeck = new ArrayList<Evidence>();
			evDeck.addAll(this.evDeck);
			if(!(choice && !p.equals(handler.getPlayer()))) {				
				this.evDeck.remove(ev);
			}
			ArrayList<Evidence> newEvDeck = null;
			double x = x0 + handler.getWidth() / 2 - ev.getHandTexture().getWidth() / 2;
			double y = y0 + handler.getHeight() + ev.getHandTexture().getHeight();
			double xTo = x;
			double yTo = y0 + handler.getHeight() / 2 - ev.getHandTexture().getHeight() / 2;
			ev.move(x, y, xTo, yTo);
			ev.setHidden(hidden);
			if(!p.getUsername().equalsIgnoreCase(handler.getPlayer().getUsername())) {
				if(!choice) {
					temps.add(ev);
					Game.sleep(handler.getGameState().getFadingTime());				
					if(identity && hidden && (getCard(ev.getIndex()) == null || getCard(ev.getIndex()).isDead())) {
						ev.flipNoScale();
						Game.sleep(handler.getGameState().getFadingTime());
					}
					if(!handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
						ev.fadeOut();
					}
					Game.sleep(handler.getGameState().getFadingTime());			
				}
			} else {
				if(!(choice && !handler.getPlayer().getHand().isEmpty())) {
					temps.add(ev);
					Game.sleep(handler.getGameState().getFadingTime());
					if(identity && hidden && (getCard(ev.getIndex()) == null || getCard(ev.getIndex()).isDead())) {
						ev.flipNoScale();
						Game.sleep(handler.getGameState().getFadingTime());
					}
					if(!handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
						ev.fadeOut();
					}
					Game.sleep(handler.getGameState().getFadingTime());
				}
			}
			
			if(hand) {
				p.addInHand(ev);
				if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
					if(handler.getGameState().getCurrAction().equals(Actions.Init)) {
						if(p.getRole().equals(Roles.Profiler)) {
							if(p.equals(handler.getPlayer())) {
								if(i == amm - 1) {									
									Packet14Ready packet = new Packet14Ready(p.getUsername());
									packet.writeData(handler.getSocketClient());
								}
							}
						}
					}
				}
			} else if(identity && !choice) {
				if(getCard(ev.getIndex()) != null && !getCard(ev.getIndex()).isDead()) {			
					if(p.getCard() != null) {
						if(handler.getGameState().getCm().getCard(p.getCard().getIndex()).isDead()) {
							p.removeIdentity(false);
						} else {							
							switch(p.getRole()) {
							case Killer:
							case Hitman:
							case Undercover:
							case Thug:
								p.removeIdentity(true);							
								break;
							default:
								p.removeIdentity(false);
								break;
							}
						}
					}
					if(!p.equals(handler.getPlayer())) {
						x = p.getNamePlate().getX() + p.getNamePlate().getWidth() / 2 - ev.getHandTexture().getWidth() / 2;
						y = handler.getGameState().getOthHandY() + 2 * ev.getHeight();
						xTo = x;
						yTo = handler.getGameState().getOthHandY() - ev.getHeight();
						ev.move(x, y, xTo, yTo);
						ev.fadeIn();
						Game.sleep(2000);
					} else {
						handler.getGameState().getCm().getTemps().remove(ev);
						handler.getGameState().getCm().getChoicePoll().remove(ev);
						ev.fadeIn();
					}
					p.setCard(ev);
					if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
						if(handler.getGameState().getCurrAction().equals(Actions.Init)) {							
							if(!p.getRole().equals(Roles.Profiler)) {
								if(p.equals(handler.getPlayer())) {									
									Packet14Ready packet = new Packet14Ready(p.getUsername());
									packet.writeData(handler.getSocketClient());
								}
							}
						}
					}
					if(p.isYourTurn()) {
						if(p.equals(handler.getPlayer())) {
							if(!handler.getGameState().getCurrAction().equals(Actions.Change)
							&& !handler.getGameState().getCurrAction().equals(Actions.Disguise)
							&& !handler.getGameState().getCurrAction().equals(Actions.Evade)
							&& !handler.getGameState().getCurrAction().equals(Actions.FBIDisguise)
							&& !handler.getGameState().getCurrAction().equals(Actions.MDisguise))
							handler.getGameState().getCurrMode().startTurn();
						}
					}
				} else {
					if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
						ev.move(xTo, yTo, x, y0 + handler.getHeight());
						ev.fadeIn();
						Game.sleep(handler.getGameState().getFadingTime());
						newEvDeck = new ArrayList<Evidence>();
						evDeck.remove(ev);
						newEvDeck.add(ev);
						newEvDeck.addAll(evDeck);
					}
				}
			} else if(choice) {
				if(p.getUsername().equalsIgnoreCase(handler.getPlayer().getUsername())) {					
					Evidence[] evs = new Evidence[] {ev};
					int plus = 0;
					if(identity) {
						ev = null;
						evs = new Evidence[] {p.getCard()};
						p.removeIdentity(false);
						identity = false;
						amm++;
					}else if(!handler.getPlayer().getHand().isEmpty()) {
						ev = null;
						evs = new Evidence[handler.getPlayer().getHand().size()];
						for (int j = 0; j < evs.length; j++) {
							Evidence e = handler.getPlayer().getHand().get(j);
							evs[j] = e;
						}
						plus = handler.getPlayer().getHand().size() - 1;
						handler.getPlayer().removeFromHand(evs);
					}
					int space;
					if(amm > 4) {
						if(i % 2 == 0) {							
							if(amm % 2 == 0) {
								space = (int) ((handler.getWidth() - evs[0].getLargeEvidenceTexture().getWidth() * amm / 2) / (double) (amm / 2 + 1));
							} else {
								space = (int) ((handler.getWidth() - evs[0].getLargeEvidenceTexture().getWidth()
										* (amm / 2 + 1)) / (double) (amm / 2 + 2));
							}
						} else {
							space = (int) ((handler.getWidth() - evs[0].getLargeEvidenceTexture().getWidth() * (amm / 2)) / (double) (amm / 2 + 1));
						}
					} else {						
						space = (int) ((handler.getWidth() - evs[0].getLargeEvidenceTexture().getWidth() * amm) / (double) (amm + 1));
					}
					for(int j = 0; j < evs.length; j++) {	
						Evidence e = evs[j];
						if(amm > 4) {
							x = x0 + space + (e.getLargeEvidenceTexture().getWidth() + space) * (i / 2 + j / 2);
							if(i % 2 == 0) {
								y = y0 + 50 * Window.SCALE;
							} else {
								y = y0 + (handler.getHeight() - e.getLargeEvidenceTexture().getHeight()) / 2;															
							}
						} else {							
							x = space + (e.getLargeEvidenceTexture().getWidth() + space) * (i + j);
							y = (int) (y0 + 100 * Window.SCALE);
						}
						e.setX(x);
						e.setY(y);
						e.setChoice(true);
						e.fadeIn();
						choicePoll.add(e);
					}	
					i += plus;
					if(i >= amm - 1) {
						Evidence.UP_TO_CHOOSE = true;
					}
				} else {
					int cardsToDraw = amm - p.getHand().size();
					if(identity) {
						cardsToDraw--;
						ev = p.getCard();
						p.getCard().setHidden(true);
						p.addInHand(p.getCard());
						p.setCard(null);
					}
					if(p.isAlly()) {
						drawCard(p, true, false, false, false, cardsToDraw);
					} else {						
						drawCard(p, true, false, false, true, cardsToDraw);
					}
					return;
				}
			} else {
				ev.setHidden(false);
				ev.setX(handler.getGameState().getCm().getCard(ev.getIndex()).getX());
				ev.setY(handler.getGameState().getCm().getCard(ev.getIndex()).getY());
				ev.setChoice(false);
				ev.setHand(false);
				ev.setOthersHand(false);
				ev.setExonerated(true);
				handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
				handler.getGameState().getCm().getCard(ev.getIndex()).setExonerated(true);
				ev.fadeIn();
				if(handler.getGameState().getMode().equals(gameModes.TvsC)) {
					((TvsC) handler.getGameState().getCurrMode()).getOfficers().add(ev);
				}
				Evidence.UP_TO_CHOOSE = true;
			}
			if(ev != null) {
				if(newEvDeck == null) {			
					evDeck.remove(ev);
				} else {
					evDeck = newEvDeck;
				}
				temps.remove(ev);
			}
			this.evDeck = evDeck;
		}
	}

	public int getFinX() {
		return finX;
	}

	public int getFinY() {
		return finY;
	}

	public int getSpace() {
		return space;
	}

	public Card getTempCard() {
		return tempCard;
	}

	public void setTempCard(Card tempCard) {
		this.tempCard = tempCard;
	}

	public void setMap(Card[][] map) {
		this.map = map;
		numRows = handler.getGameState().getNumRows();
		numCols = handler.getGameState().getNumCols();;
	}

	public Card[][] getMap() {
		return map;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}

	public int getCardW() {
		return cardW;
	}

	public int getCardH() {
		return cardH;
	}
	
	public boolean getScaling() {
		return scaling;
	}
	
	public void setScaling(boolean b) {
		scaling = b;
		Card c;
		for(int i = 0; i < numCols; i++) {
			for(int j = 0; j < numRows; j++) {
				c = map[j][i];
				c.setResizing(false);
				c.setWidth(cardW);
				c.setHeight(cardH);
			}
		}
	}

	public void setFinX(int finX) {
		this.finX = finX;
		for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
			for(int j = 0; j < handler.getGameState().getNumCols(); j++) {
				handler.getGameState().getMap()[i][j].setFinX(finX);
			}	
		}
	}

	public void setFinY(int finY) {
		this.finY = finY;
		for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
			for(int j = 0; j < handler.getGameState().getNumCols(); j++) {
				handler.getGameState().getMap()[i][j].setFinY(finY);
			}	
		}
	}
	
	public void removeDyingOut(Card c) {
		dyingOut.remove(c);
	}

	public void addDyingOut(Card c) {
		dyingOut.add(c);
	}

	public int getHandSpace() {
		return handSpace;
	}

	public ArrayList<Evidence> getEvDeck() {
		return evDeck;
	}

	public void setEvDeck(ArrayList<Evidence> evDeck) {
		this.evDeck = evDeck;
	}

	public ArrayList<Evidence> getChoicePoll() {
		return choicePoll;
	}
	
	public Card getCard(int index) {
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				if(map[row][col].getIndex() == index) {
					return map[row][col];
				}
			}
		}
		return null;
	}

	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}

	public ArrayList<Evidence> getTemps() {
		return temps;
	}

	public BufferedImage[] getTokens() {
		return tokens;
	}

	public float getScaleRate() {
		return scaleRate;
	}

	public void setScaleRate(float scaleRate) {
		this.scaleRate = scaleRate;
	}

	public float getFlipRate() {
		return flipRate;
	}

	public void setFlipRate(float flipRate) {
		this.flipRate = flipRate;
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public BufferedImage[] getCapture() {
		return capture;
	}

	public BufferedImage[] getCanvas() {
		return canvas;
	}

	public BufferedImage[] getDisable() {
		return disable;
	}

	public BufferedImage[] getSilence() {
		return silence;
	}

	public BufferedImage[] getAccuse() {
		return accuse;
	}

	public BufferedImage[] getSteal() {
		return steal;
	}

	public BufferedImage[] getKill() {
		return kill;
	}

	public BufferedImage[] getMurder() {
		return murder;
	}

	public BufferedImage[] getAutopsy() {
		return autopsy;
	}

	public BufferedImage[] getThreat() {
		return threat;
	}

	public BufferedImage[] getBomb() {
		return bomb;
	}

	public BufferedImage[] getProtectionAdd() {
		return protectionAdd;
	}

	public BufferedImage[] getProtectionRemove() {
		return protectionRemove;
	}

	public BufferedImage[] getSwap() {
		return swap;
	}

	public BufferedImage[] getSwapFrom() {
		return swapFrom;
	}

	public BufferedImage[] getSwapTo() {
		return swapTo;
	}

	public BufferedImage[] getDetonate() {
		return detonate;
	}

	public BufferedImage getTknHighlight() {
		return tknHighlight;
	}

}
