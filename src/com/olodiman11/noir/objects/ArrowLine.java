package com.olodiman11.noir.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.states.GameState.Actions;

public class ArrowLine {

	private Handler handler;
	private float x1, y1, x2, y2;
	private int[][] triangleStart, triangleEnd;
	private Card home, target;
	private boolean display;
	private int triangleSize;
	private Color color;
	private float lineW;
	
	public ArrowLine(Handler handler, Card home) {
		this.handler = handler;
		this.home = home;
		triangleStart = new int[2][3];
		triangleEnd = new int[2][3];
		triangleSize = (int) (6 * Window.SCALE);
		lineW = (float) (3 * Window.SCALE);
		color = Color.decode("#F0F0F0");
		prepareLine();
	}
	
	private float getRadians(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float atan;
		if((dx < 0  && dy >= 0) || (dx < 0 && dy < 0)) {
			atan = (float) (Math.atan(dy/dx) + Math.PI);
		} else {
			atan = (float) Math.atan(dy/dx);
		}
		return atan;
	}
	
	private float getMagnitude(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	private void setTriangles(float rad) {
		float phase = 60f * (float) Math.PI / 360f;
		float ang1 = rad - phase;
		float ang2 = rad + phase;
		
		triangleStart[0][0] = (int) x1;
		triangleStart[0][1] = Math.round(x1 + triangleSize * (float) Math.cos(ang1));
		triangleStart[0][2] = Math.round(x1 + triangleSize *(float) Math.cos(ang2));
		triangleStart[1][0] = (int) y1;
		triangleStart[1][1] = Math.round(y1 + triangleSize * (float) Math.sin(ang1));
		triangleStart[1][2] = Math.round(y1 + triangleSize * (float) Math.sin(ang2));
		
		triangleEnd[0][0] = (int) x2;
		triangleEnd[0][1] = Math.round(x2 - triangleSize * (float) Math.cos(ang1));
		triangleEnd[0][2] = Math.round(x2 - triangleSize * (float) Math.cos(ang2));
		triangleEnd[1][0] = (int) y2;
		triangleEnd[1][1] = Math.round(y2 - triangleSize * (float) Math.sin(ang1));
		triangleEnd[1][2] = Math.round(y2 - triangleSize * (float) Math.sin(ang2));
		
	}
	
	public void prepareLine() {
		float radius = home.getWidth()/2f;
		float centerX = (float) home.getX() + home.getWidth() / 2f;
		float centerY = (float) home.getY() + home.getHeight() / 2f;
		x2 = (float) handler.getMm().getX();
		y2 = (float) handler.getMm().getY();
		if(getMagnitude(centerX, centerY, x2, y2) <= radius) {
			display = false;
		} else {
			display = true;
			float rad;
			if(target != null) {
				float targetX = (float) target.getX() + target.getWidth() / 2;
				float targetY = (float) target.getY() + target.getHeight() / 2;
				rad = getRadians(centerX, centerY, targetX, targetY);
				x1 = centerX + radius * (float) Math.cos((double) rad);
				y1 = centerY + radius * (float) Math.sin((double) rad);
				x2 = targetX - radius * (float) Math.cos((double) rad);
				y2 = targetY - radius * (float) Math.sin((double) rad);
			} else {				
				rad = getRadians(centerX, centerY, x2, y2);
				x1 = centerX + radius * (float) Math.cos((double) rad);
				y1 = centerY + radius * (float) Math.sin((double) rad);
			}
			setTriangles(rad);
		}
	}
	
	public void checkTargetOnRelease() {
		if(target == null) {
			handler.getGameState().getCm().deselectAll();
			handler.getGameState().getCm().deactivateAll();
			if(handler.getGameState().getCurrMode() instanceof Heist) {				
				handler.getGameState().setCurrAction(Actions.OfSwap);
				for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
					handler.getGameState().getCm().getCard(ev.getIndex()).setActive(true);
				}
			}
			else {
				handler.getGameState().setCurrAction(Actions.Swap);
				for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
					for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
						handler.getGameState().getMap()[row][col].setActive(true);
					}
				}
			}
		}
	}
	
	public void render(Graphics2D g2d) {
		if(display) {
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(lineW, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			g2d.fillPolygon(triangleStart[0], triangleStart[1], 3);
			g2d.fillPolygon(triangleEnd[0], triangleEnd[1], 3);
		}
	}
	
	public void setTarget(Card target) {
		if(target != home) {			
			this.target = target;
		}
	}
	
	public Card getTarget() {
		return target;
	}
	
}
