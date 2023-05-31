package com.olodiman11.noir.objects.actions;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Launcher;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet07Collapse;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class CollapseButton extends Button{

	private boolean colsRefreshable, rowsRefreshable, playersChose, dirChose, cardSelected = false;
	private boolean playerChoosing = false;
	private boolean displayed = true;
	private int[] numCols, numRows;
	private CollapseButton refCols, refRows;
	private int[] indexes;
	private Packet07Collapse packet;
	
	public CollapseButton(Handler handler) {
		super(handler, 0, 0);
		x = x0 + 1067 * Window.SCALE;
		y = y0 + 113 * Window.SCALE;
		texture = handler.getAssets().getCollapse()[0];
		selectedImg = handler.getAssets().getCollapse()[1];
		pressedImg = handler.getAssets().getCollapse()[2];
		disabledImg = handler.getAssets().getCollapse()[3];
		
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		active = false;
	}
	
	public CollapseButton(Handler handler, double x, double y) {
		this(handler);
		this.x = x;
		this.y = y;
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		if(refCols != null) {
			refCols.onMousePressed(m);
		}
		if(refRows != null) {
			refRows.onMousePressed(m);
		}
		super.onMousePressed(m);
	}
	
	@Override
	public void onMouseReleased(MouseEvent m) {
		if(refCols != null) {
			refCols.onMouseReleased(m);
		}
		if(refRows != null) {
			refRows.onMouseReleased(m);
		}
		super.onMouseReleased(m);
	}
	
	@Override
	public void processButtonPress() {
		System.out.println("dirChose: " + String.valueOf(dirChose));
		System.out.println("playersChose: " + String.valueOf(playersChose));
		System.out.println("colsRefreshable: " + String.valueOf(colsRefreshable));
		System.out.println("rowsRefreshable: " + String.valueOf(rowsRefreshable));
		handler.getGameState().setCurrAction(Actions.Collapse);
		active = false;
		if(dirChose) {
			active = false;
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CancelButton) {
					((CancelButton) b).setActive(false);
					break;
				}
			}
			dirChose = false;
			refRows = new CollapseButton(handler, x, y);
			refRows.setTexture(handler.getAssets().getRows()[0]);
			refRows.setSelectedImg(handler.getAssets().getRows()[1]);
			refRows.setPressedImg(handler.getAssets().getRows()[2]);
			refRows.setActive(true);
			refRows.setPlayersChose(playersChose);
			refRows.setRowsRefreshable(rowsRefreshable);
			refRows.setNumCols(numCols);
			refRows.setNumRows(numRows);
			refCols = new CollapseButton(handler, x + 183 * Window.SCALE, y);
			refCols.setTexture(handler.getAssets().getCols()[0]);
			refCols.setSelectedImg(handler.getAssets().getCols()[1]);
			refCols.setPressedImg(handler.getAssets().getCols()[2]);
			refCols.setActive(true);
			refCols.setPlayersChose(playersChose);
			refCols.setColsRefreshable(colsRefreshable);
			refCols.setNumCols(numCols);
			refCols.setNumRows(numRows);
		} else if(playersChose) {
			selectCards();
			letPlayerChoose();
			if(!playerChoosing) {
				packet.writeData(handler.getSocketClient());
			}
		} else if(colsRefreshable) {
			selectCards();
			refreshCols();
			packet.writeData(handler.getSocketClient());
		} else if(rowsRefreshable) {
			System.out.println("Refreshing");
			selectCards();
			refreshRows();
			packet.writeData(handler.getSocketClient());
		}
		
	}
	
	public void checkCollapseAbility() {
		System.out.println("Refreshable Checked!");
		if(refCols != null && refRows != null) {
			if(!refCols.isActive() || !refRows.isActive()) {
				refCols = null;
				refRows = null;
			}
		}
		dirChose = false;
		numCols = new int[handler.getGameState().getNumRows()];
		numRows = new int[handler.getGameState().getNumCols()];
		
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				if(handler.getGameState().getMap()[row][col].isDead()) {
					numCols[row] += 1;
					numRows[col] += 1;
				}
			}
		}
		
		colsRefreshable = true;
		boolean colsPlayersChose = false;
		for(Integer i: numCols) {
			if(i == 0) {
				colsRefreshable = false;
				colsPlayersChose = false;
				break;
			}
			if(i > 1) {
				colsPlayersChose = true;
			}
		}
		
		rowsRefreshable = true;
		boolean rowsPlayersChose = false;
		for(Integer i: numRows) {
			if(i == 0) {
				rowsRefreshable = false;
				rowsPlayersChose = false;
				break;
			}
			if(i > 1) {
				rowsPlayersChose = true;
			}
		}
		
		if(colsPlayersChose || rowsPlayersChose) {
			playersChose = true;
		} else {
			playersChose = false;
		}
		
		if(colsRefreshable && rowsRefreshable) {
			dirChose = true;
		}
		
		if(colsRefreshable || rowsRefreshable) {
			active = true;
		} else {
			active = false;
		}
		
	}
	
	public void selectCards() {
		
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setRefCols(null);
				((CollapseButton) b).setRefRows(null);
				break;
			}
		}
		Card[][] map = handler.getGameState().getMap();
		if(colsRefreshable) {
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CollapseButton) {
					((CollapseButton) b).setColsRefreshable(true);
					((CollapseButton) b).setRowsRefreshable(false);
					break;
				}
			}
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				if(numCols[row] > 1) {
					continue;
				}
				for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
					if(map[row][col].isDead()) {
						handler.getGameState().getMap()[row][col].setSelected(true);
					}
				}
			}
		}
		if(rowsRefreshable) {
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CollapseButton) {
					((CollapseButton) b).setColsRefreshable(false);
					((CollapseButton) b).setRowsRefreshable(true);
					break;
				}
			}
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				if(numRows[col] > 1) {
					continue;
				}
				for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
					if(map[row][col].isDead()) {
						handler.getGameState().getMap()[row][col].setSelected(true);
					}
				}
			}
		}
	}
	
	public void letPlayerChoose() {
		Card[][] map = handler.getGameState().getMap();
		if(colsRefreshable) {
			playerChoosing = false;
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				cardSelected = false;
				for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
					if(map[row][col].isSelected()) {
						cardSelected = true;
						for(int i = 0; i < handler.getGameState().getNumCols(); i++) {
							handler.getGameState().getMap()[row][i].setActive(false);
						}
						break;
					}
				}
				if(!cardSelected) {
					playerChoosing = true;
					for(int i = 0; i < handler.getGameState().getNumCols(); i++) {
						if(map[row][i].isDead()) {
							handler.getGameState().getMap()[row][i].setActive(true);
						}
					}
					break;
				}
			}
			if(!playerChoosing) {
				refreshCols();
			}
		}
		
		if(rowsRefreshable) {
			playerChoosing = false;
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				cardSelected = false;
				for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
					if(map[row][col].isSelected()) {
						cardSelected = true;
						for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
							handler.getGameState().getMap()[i][col].setActive(false);
						}
						break;
					}
				}
				if(!cardSelected) {
					playerChoosing = true;
					for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
						if(map[i][col].isDead()) {
							handler.getGameState().getMap()[i][col].setActive(true);
						}
					}
					break;
				}
			}
			if(!playerChoosing) {
				refreshRows();
			}
		}
	}
	
	public void refreshCols() {
		if(playerChoosing) {
			return;
		}
		
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setRefCols(null);
				((CollapseButton) b).setRefRows(null);
				break;
			}
		}
		Card[][] map = handler.getGameState().getMap();
		for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
			for(int j = 0; j < handler.getGameState().getNumCols(); j++) {
				if(map[i][j].isSelected()) {
					handler.getGameState().getCm().addDyingOut(map[i][j]);
				}
			}	
		}
		handler.getGameState().getCm().setFinX(handler.getGameState().getCm().getFinX()
				+ (handler.getGameState().getCm().getCardW() + handler.getGameState().getCm().getSpace()) / 2);
		int prevNumCols = handler.getGameState().getNumCols();
		indexes = new int[handler.getGameState().getNumRows()];
		handler.getGameState().setNumCols(prevNumCols - 1);
		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
			for(int col = 0; col < prevNumCols; col++) {
				if(map[row][col].isSelected()) {
					indexes[row] = col;
					if(col + 1 >= prevNumCols) {
						break;
					}
					for(int i = col + 1; i < prevNumCols; i++) {
						handler.getGameState().getMap()[row][i - 1] = map[row][i];
						handler.getGameState().getMap()[row][i - 1].setCol(i - 1);
						//handler.getGameState().getMap()[row][i - 1].scale();
					}
					break;
				}
				handler.getGameState().getMap()[row][col] = map[row][col];
				handler.getGameState().getMap()[row][col].setCol(col);
				handler.getGameState().getMap()[row][col].setRow(row);
				//handler.getGameState().getMap()[row][col].scale();
			}
		}
		
		System.out.println(handler.getGameState().getCurrAction());
		handler.getGameState().getCm().scaleCards();
		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				handler.getGameState().getMap()[row][col].resize();
			}
		}
		System.out.println("");
		for(Evidence ev: handler.getGameState().getCm().getEvDeck()) {
			ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
		}
		for(Player p: handler.getConnectedPlayers()) {
			p.getCard().scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
			for(Evidence ev: p.getHand()) {				
				ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
			}
		}
		for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
			ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
		}
		
		packet = new Packet07Collapse(handler.getPlayer().getUsername(), rowsRefreshable, indexes);
		deselectAll();
		deactivateAll();
//		checkRefreshable();
		handler.getPlayer().setDone(true);
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		
		handler.getGameState().getCurrMode().endTurn();
		System.out.println(handler.getGameState().getCurrAction());
	}

	public void refreshRows() {
		if(playerChoosing) {
			return;
		}
		
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setRefCols(null);
				((CollapseButton) b).setRefRows(null);
				break;
			}
		}
		Card[][] map = handler.getGameState().getMap();
		for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
			for(int j = 0; j < handler.getGameState().getNumCols(); j++) {
				if(map[i][j].isSelected()) {
					handler.getGameState().getCm().addDyingOut(map[i][j]);
				}
			}	
		}
		handler.getGameState().getCm().setFinY(handler.getGameState().getCm().getFinY()
				+ (handler.getGameState().getCm().getCardH() + handler.getGameState().getCm().getSpace()) / 2);
		int prevNumRows = handler.getGameState().getNumRows();
		indexes = new int[handler.getGameState().getNumCols()];
		handler.getGameState().setNumRows(prevNumRows - 1);
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < prevNumRows; row++) {
				if(map[row][col].isSelected()) {
					indexes[col] = row;
					if(row + 1 == prevNumRows) {
						break;
					}
					for(int i = row + 1; i < prevNumRows; i++) {
						handler.getGameState().getMap()[i - 1][col] = map[i][col];
						handler.getGameState().getMap()[i - 1][col].setRow(i - 1);
						//handler.getGameState().getMap()[i - 1][col].scale();
					}
					break;
				}
				handler.getGameState().getMap()[row][col] = map[row][col];
				handler.getGameState().getMap()[row][col].setCol(col);
				handler.getGameState().getMap()[row][col].setRow(row);
				//handler.getGameState().getMap()[row][col].scale();
			}
		}
		
		System.out.println(handler.getGameState().getCurrAction());
		handler.getGameState().getCm().scaleCards();
		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				handler.getGameState().getMap()[row][col].resize();
			}
		}
		for(Evidence ev: handler.getGameState().getCm().getEvDeck()) {
			ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
		}
		for(Player p: handler.getConnectedPlayers()) {
			p.getCard().scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
			for(Evidence ev: p.getHand()) {				
				ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
			}
		}
		for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
			ev.scale(handler.getGameState().getCm().getCardW(), handler.getGameState().getCm().getCardH());
		}
		
		packet = new Packet07Collapse(handler.getPlayer().getUsername(), rowsRefreshable, indexes);
		deselectAll();
		deactivateAll();
		handler.getGameState().setCurrAction(Actions.Idle);
//		checkRefreshable();
		handler.getPlayer().setDone(true);
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		
		handler.getGameState().getCurrMode().endTurn();
		System.out.println(handler.getGameState().getCurrAction());
	}
	
	public void deselectAll() {
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				handler.getGameState().getMap()[row][col].setSelected(false);
			}
		}
	}
	
	public void deactivateAll() {
		for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				handler.getGameState().getMap()[row][col].setActive(false);
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if(refCols != null) {
			refCols.tick();
		}
		if(refRows != null) {
			refRows.tick();
		}
	}
	
	@Override
	public void render(Graphics g) {
		if(displayed) {			
			if(active) {
				super.render(g);
			} else {
				g.drawImage(disabledImg, (int) x, (int) y, width, height, null);
			}
		}
		if(refCols != null) {
			refCols.render(g);
		}
		if(refRows != null) {
			refRows.render(g);
		}
	}

	public void setColsRefreshable(boolean colsRefreshable) {
		this.colsRefreshable = colsRefreshable;
	}

	public void setRowsRefreshable(boolean rowsRefreshable) {
		this.rowsRefreshable = rowsRefreshable;
	}

	public void setPlayersChose(boolean playersChose) {
		this.playersChose = playersChose;
	}

	public void setRefCols(CollapseButton refCols) {
		this.refCols = refCols;
	}

	public void setRefRows(CollapseButton refRows) {
		this.refRows = refRows;
	}

	public void setNumCols(int[] numCols) {
		this.numCols = numCols;
	}

	public void setNumRows(int[] numRows) {
		this.numRows = numRows;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
	
}
