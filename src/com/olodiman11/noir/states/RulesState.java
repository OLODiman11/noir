package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.objects.buttons.menu.rules.PageButton;
import com.olodiman11.noir.objects.buttons.menu.rules.TurnPageButton;

public class RulesState extends State{

	private BufferedImage[] pages;
	private int currPage;
	private int pageW, pageH;
	
	public RulesState(Handler handler) {
		super(handler);
		bg = ImageManager.getImage("/states/loading/background.png");
		buttons.add(new TurnPageButton(handler, x0 + 50 * Window.SCALE, y0 + handler.getHeight() / 2, 0));
		buttons.add(new TurnPageButton(handler, x0 + handler.getWidth() - 50 * Window.SCALE, y0 + handler.getHeight() / 2, 1));
		pageW = (int) (470 * Window.SCALE);
		pageH = (int) (680 * Window.SCALE);
		pages = new BufferedImage[32];
		for(int i = 0; i < pages.length; i++) {
			pages[i] = ImageManager.getImage("/rules/" + String.valueOf(i + 1) + ".png");
			buttons.add(new PageButton(handler, 0, 0, i));
		}
		currPage = 0;
	}

	@Override
	public void tick() {
		for(int i = 0; i < 2; i++) {
			if(currPage == 0 && i == 0) {
				buttons.get(i).setActive(false);
				continue;
			} else if(currPage == 31 && i == 1) {
				buttons.get(i).setActive(false);
				continue;
			}
			buttons.get(i).setActive(true);
			buttons.get(i).tick();
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(bg, x0, y0, handler.getWidth(), handler.getHeight(), null);
		if(currPage == 0 || currPage == 31) {
			g.drawImage(pages[currPage], (int) (x0 + handler.getWidth() / 2 - pageW / 2), (int) (y0 + handler.getHeight() / 2 - pageH / 2),
					pageW, pageH, null);
		} else {			
			g.drawImage(pages[currPage], (int) (x0 + handler.getWidth() / 2 - pageW), (int) (y0 + handler.getHeight() / 2 - pageH / 2),
					pageW, pageH, null);
			g.drawImage(pages[currPage + 1], (int) (x0 + handler.getWidth() / 2), (int) (y0 + handler.getHeight() / 2 - pageH / 2),
					pageW, pageH, null);
		}
		for(int i = 0; i < 2; i++) {
			if(currPage == 0 && i == 0) {
				continue;
			} else if(currPage == 31 && i == 1) {
				continue;
			}
			buttons.get(i).render(g);
		}
	}
	
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	
	public void incCurrPage() {
		System.out.println(currPage);
		if(currPage == 0) {
			currPage++;
		} else {			
			currPage += 2;
		}
	}

	public void decCurrPage() {
		System.out.println(currPage);
		if(currPage == 1) {
			currPage--;
		} else {
			currPage -= 2;
		}
	}
	
}
