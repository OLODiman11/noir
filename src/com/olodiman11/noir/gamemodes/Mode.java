package com.olodiman11.noir.gamemodes;

import java.awt.Graphics;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.buttons.Button;

public abstract class Mode {
	
	public static enum Roles{
		Killer("killer", "Бандит"), Inspector("inspector", "Инспектор"),
		Hitman("hitman", "Убийца"), Sleuth("sleuth", "Сыщик"),
		Spy("spy", "Шпион"),
		MasterThief("masterThief", "Вор-виртуоз"), ChiefOfPolice("chiefOfPolice", "Начальник полиции"),
		Undercover("undercover", "Агент под прикрытием"), Detective("detective", "Детектив"),
		Suit("suit", "Оперативник"), Profiler("profiler", "Криминалист"),
		Thug("thug", "Бандит"), Psycho("psycho", "Маньяк"),
		Bomber("bomber", "Подрывник"), Sniper("sniper", "Снайпер"),
		SecurityChief("securityChief", "Начальник безопасности"),
		Safecracker("safecracker", "Медвежатник"), Runner("runner", "Бегун"),
		Cleaner("cleaner", "Чистильщик"), Decoy("decoy", "Подставное лицо"),
		Insider("insider", "Инсайдер"), Hacker("hacker", "Хакер"),
		Silencer("silencer", "Устранитель"), Mimic("mimic", "Подражатель"),
		Infiltrator("infiltrator", "Диверсант"), Sneak("sneak", "Стукач"),
		MasterSafecracker("masterSafecracker", "Медвежатник-виртуоз");
		
		private String text, name;
		private Roles(String text, String name) {
			this.text = text;
			this.name = name;
		}
		
		public String getText() {
			return text;
		}
		
		public String getName() {
			return name;
		}
	}
	
	protected int[] turnsOrder;
	protected Handler handler;
	protected int numCols, numRows;
	protected int numPlayers;
	protected ArrayList<Button> buttons;
	protected int x0, y0;
	protected int[] roles;
	
	public Mode(Handler handler) {
		this.handler = handler;
		x0 = handler.getFrameWidth() / 2 - handler.getWidth() / 2;
		y0 = handler.getFrameHeight() / 2 - handler.getHeight() / 2;
		buttons = new ArrayList<Button>();
	}
	
	public abstract void setWinCondition();
	
	public abstract void checkWin();
	
	public abstract void setupBoard();
	
	public abstract void dealCards();
	
	public abstract void setTurnsOrder();
	
	public abstract void setTeams();
	
	public abstract void setRoles();
	
	public abstract void createButtons();
	
	public abstract void startTurn();
	
	public abstract void endTurn();
	
	public abstract void reset();
	
	public void activateButtons() {
		for(Button b: buttons) {
			if(b instanceof CancelButton) {
				continue;
			}
			if(b instanceof CollapseButton) {
				((CollapseButton) b).checkCollapseAbility();
				continue;
			}
			b.setActive(true);
		}
	}
	
	public void deactivateButtons() {
		for(Button b: buttons) {
			b.setActive(false);
		}
	}
	
	public void tick() {
		for(Button b: buttons) {
			b.tick();
		}
	}

	public void render(Graphics g) {
		for(Button b: buttons) {
			b.render(g);
		}
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public ArrayList<Button> getButtons() {
		return buttons;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}


	
}
