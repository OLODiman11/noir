package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.Comment;
import com.olodiman11.noir.objects.Warning;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.Object;

public class StateManager {
	
	private Warning warning;
	private Comment comment;
	
	// Handler
	private Handler handler;
	
	// Стадии игры
	private ArrayList<State> states;
	private int currState;
	public static final int MENU = 0,
							GAME = 1,
							GAMEMENU = 2,
							WAITING = 3,
							LOADING = 4,
							WIN = 5,
							RULES = 6;
	
	public StateManager(Handler handler) {
		
		this.handler = handler;
		
		states = new ArrayList<State>();
		states.add(new MainMenuState(handler));
		states.add(new GameState(handler));
		states.add(new MenuState(handler));
		states.add(new WaitingState(handler));
		states.add(new LoadingState(handler));
		states.add(new WinState(handler));
		states.add(new RulesState(handler));
		setState(MENU);
		
	}
	
	// Tick and Render
	
	public void tick() {
		
		states.get(currState).tick();
		
		if(comment != null) {
			comment.tick();
		}
		
		if(warning != null) {			
			warning.tick();
		}
		
	}
	
	public void render(Graphics g) {
		
		states.get(currState).render(g);
		
		if(comment != null) {
			comment.render(g);
		}
		
		if(warning != null) {			
			warning.render(g);
		}
		
	}
	
	public void createWarning(String text) {
		for(Button b: states.get(currState).getButtons()) {
			b.setActive(false);
		}
		warning = new Warning(handler, text);
	}
	
	public void removeWarning() {
		for(Button b: states.get(currState).getButtons()) {
			b.setActive(true);
		}
		warning = null;
	}
	
	public void createComment(String text, Object obj, boolean vert) {
		comment = new Comment(handler, text, obj, vert);
	}
	
	public void createComment(String text, Object obj, boolean vert, int objWidth, int objHeight) {
		comment = new Comment(handler, text, obj, vert, objWidth, objHeight);
	}
	
	// Getters and Setters
	
	public void setState(int state) {
		currState = state;
	}

	public int getCurrState() {
		return currState;
	}

	public ArrayList<State> getStates() {
		return states;
	}
	
	public void setStates(ArrayList<State> states) {
		this.states = states;
	}

	public Warning getWarning() {
		return warning;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	public Comment getComment() {
		return comment;
	}

}
