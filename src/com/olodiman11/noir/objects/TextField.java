package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;

public class TextField extends Object{
	
	private int space;
	private BufferedImage texture, cursorTexture;
	private double xCur, yCur;
	private long timeElapsed, lastTime, now, timer;
	private boolean focused, cursor, hovering, transparent;
	private ArrayList<Character> chars;
	private String input;
	private float size;
	private String initText;
	

	public TextField(Handler handler, double x, double y, BufferedImage texture, float size, String initText) {
	
		super(handler, x, y);

		this.initText = initText;
		if(initText.equalsIgnoreCase("Порт")) {
			transparent = true;
		}
		this.size = size;
		chars = new ArrayList<Character>();
		space = (int) (3 * Window.SCALE);
		focused = cursor = false;
		timer = 500;
		this.texture = texture;
		cursorTexture = handler.getAssets().getCursor();
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		yCur = y + height / 2 - size / 2;
		for(Character c: initText.toCharArray()) {			
			chars.add(c);
		}
		
	}
	
	public void onMouseReleased(MouseEvent m) {
		if(m.getButton() == MouseEvent.BUTTON1) {
			if(hovering) {
				focused = true;
				if(!chars.isEmpty()) {
					input = "";
					for(Character c: chars) {
						input += c;
					}
					if(input.equalsIgnoreCase("IP Адрес") || input.equalsIgnoreCase("Порт")) {
						chars.clear();
						if(input.equalsIgnoreCase("Порт")) {
							transparent = false;
						}
					}
				}
			} else {
				if(focused) {					
					input = "";
					for(Character c: chars) {
						input += c;
					}
					if(chars.isEmpty()) {
						for(Character c: initText.toCharArray()) {
							chars.add(c);
						}
						if(initText.equalsIgnoreCase("Порт")) {
							transparent = true;
						}
					}
					focused = false;
					cursor = false;
				}
			}
		}
	}
	
	public void onKeyReleased(KeyEvent k) {
		if(focused && !k.isControlDown()) {
			if(k.getKeyCode() == KeyEvent.VK_ENTER) {
				if(!chars.isEmpty()) {
					input = "";
					for(Character c: chars) {
						input += c;
					}
				}
				focused = false;
				cursor = false;
			} else {
				if(k.getKeyChar() != KeyEvent.CHAR_UNDEFINED && k.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
					if(initText.equalsIgnoreCase("Игрок")
					&& (k.getKeyChar() == KeyEvent.VK_SLASH
					|| k.getKeyChar() == '~'
					|| k.getKeyChar() == KeyEvent.VK_COMMA
					|| k.getKeyChar() == KeyEvent.VK_SEMICOLON)) {
						handler.getSm().createComment("Данный символ запрещен. Запрещенные символы: '/' '~' ',' ';'", this, true);
					} else {
						String str = "";
						for(Character c: chars) {
							str += c;
						}
						str += k.getKeyChar();
						if(handler.getCanvas().getGraphics().getFontMetrics(Text.ARIALB.deriveFont(size)).stringWidth(str) < width) {							
							chars.add(k.getKeyChar());
						}
					}
				}
			}
		}
	}
	
	public void onKeyPressed(KeyEvent k) {
		if(focused) {			
			if(k.getKeyCode() == KeyEvent.VK_V) {
				if(k.isControlDown()) {
					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable t = c.getContents(this);
					if (t == null)
						return;
					try {
						String str = "";
						for(Character ch: ((String) t.getTransferData(DataFlavor.stringFlavor)).toCharArray()) {
							str += ch;
						}
						if(handler.getCanvas().getGraphics().getFontMetrics(Text.ARIALB.deriveFont(size)).stringWidth(str) < width) {
							char[] chr = str.toCharArray();
							for(Character chrc: chr) {								
								chars.add(chrc);
							}
						} else {
							handler.getSm().createComment("Текст, который вы пытаетесь вставить, слишком большой.", this, true);
						}
					} catch (Exception e) {
						handler.getSm().createComment("Ошибка! Одна ошибка и ты ошибся", this, true);
						e.printStackTrace();
					}
				}
			} else if(k.getKeyCode() == KeyEvent.VK_BACK_SPACE){
				if(!chars.isEmpty()) {
					chars.remove(chars.size() - 1);
				}
			}
		}
	}
	
	public void tick() {
		if(handler.getMm().getX() >= x && handler.getMm().getX() <= x + width
		&& handler.getMm().getY() >= y && handler.getMm().getY() <= y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
		if(focused) {
			lastTime = now;
			now = System.currentTimeMillis();
			timeElapsed += now - lastTime;
			if(timeElapsed >= timer) {
				timeElapsed = 0;
				if(cursor) {
					cursor = false;
				} else {
					cursor = true;
				}
			}	
		}
		
		input = "";
		try {
			if(!chars.isEmpty()) {
				for(Character c: chars) {
					input += c;
				}
			}
		} catch(ConcurrentModificationException e) {}
	}
	
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		if(transparent) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
		
		if(input != null) {
			xCur = x + width / 2 + g2d.getFontMetrics(Text.ARIALB.deriveFont(size)).stringWidth(input) / 2 + space;			
		} else {
			xCur = x + width / 2 + space;
		}
		
		g2d.drawImage(texture, (int) x, (int) y, width, height, null);
		
		if(input != null) {			
			Text.drawCenteredText(input, x + width / 2, y + height / 2, g2d, size);
		}
		
		if(cursor) {
			double ratio = (double) size / (double) cursorTexture.getHeight();
			g2d.drawImage(cursorTexture, (int) xCur, (int) yCur,
					(int) (cursorTexture.getWidth() * ratio), (int) size, null);
		}
		
	}

	public String getInput() {
		return input;
	}

	public boolean isHovering() {
		return hovering;
	}

	public boolean isFocused() {
		return focused;
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}
	
}
