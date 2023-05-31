package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.Button;

public abstract class State {

	// Координаты начала полотна
	protected int x0, y0;
	
	// Список кнопок
	protected ArrayList<Button> buttons;
	
	// Фон
	protected BufferedImage bg;
	
	// Handler
	protected Handler handler;
	
	public State(Handler handler) {
		
		this.handler = handler;
		
		x0 = handler.getFrameWidth() / 2 - handler.getWidth() / 2;
		y0 = handler.getFrameHeight() / 2 - handler.getHeight() / 2;
		
		buttons = new ArrayList<Button>();
	}
	
	// Tick and Render
	
	public abstract void tick();
	
	public abstract void render(Graphics g);
	
	// Getters and Setters

	public ArrayList<Button> getButtons() {
		return buttons;
	}
	
}
