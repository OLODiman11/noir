package com.olodiman11.noir.states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.HvsS;
import com.olodiman11.noir.gamemodes.KvsI;
import com.olodiman11.noir.gamemodes.Mode;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.gamemodes.SpyTag;
import com.olodiman11.noir.gamemodes.TvsC;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.CardsManager;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.Notification;
import com.olodiman11.noir.objects.QuoteBox;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.PassTurnButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.log.Log;

public class GameState extends State{
	
	// Layout
	private int boardX, boardY, boardW, boardH;
	private int previewEvX, previewEvY, previewX, previewY, previewW, previewH;
	private int handX, handY, handW, handH;
	private int othHandX, othHandY, othHandW;
	private int labelPosLeftX, labelPosLeftY, labelPosRightX, labelPosRightY;
	private int chooseTextY;
	
	// Board Textures
	private BufferedImage[] frames, background;
	private BufferedImage cap, blackout, choice;
	
	// Role Textures
	private BufferedImage roleTexture, actionsTextTexture;
	
	// Card Manager
	private CardsManager cm;
	
	// Notification
	private Notification nt;
	private String choiceLine;

	// Log
	private Log log;
	
	// Game Modes
	private gameModes mode;
	private ArrayList<Mode> modes;
	public static enum gameModes{
		KvsI("Бандит против Инспектора", 0),
		HvsS("Убийца против Сыщика", 1),
		SpyTag("Шпионские игры", 2),
		TvsC("Вор-виртуоз против Начальника полиции", 3),
		MvsFBI("ФБР против Мафии", 4),
		Heist("Ограбление века", 5);
		
		private String message;
		private int index;
		private gameModes(String message, int index) {
			this.message = message;
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String getMessage() {
			return message;
		}	
	}
	
	// Actions
	private Actions currAction;
	public static enum Actions{
		Init, Idle, Shift, FastShift, Collapse, Disarm, FBIAccuse, Rob, SwapTo,
		// Killer
		Murder, Disguise,
		// Inspector
		Accuse, Exonerate,
		// Hitman
		Kill, Evade,
		// Sleuth
		Investigate, Defend,
		// Spy
		Capture, Canvas,
		// Thief
		Steal, Change, StealWholeBoard,
		// Chief
		Charge, Deputize,
		// Thug
		MKill, MDisguise,
		// Psycho
		Threat, Swap,
		// Bomber
		Bomb, Detonate,
		// Sniper
		Snipe, Setup,
		// Undercover
		FBIDisguise, Autopsy,
		// Detective
		FBICanvas,
		// Suit
		Protect, Marker,
		// Profiler
		Profile,
		// Cleaner
		Disable,
		// Decoy
		Vanish,
		// Insider
		InsideJob,
		// Hacker
		Hack,
		// Silencer
		Silence,
		// Mimic
		Duplicate,
		// Infiltraitor
		AdSwap,
		// Sneak
		StealthyShift,
		// MasterSafecracker
		Safebreaking,
		// SecurityChief
		OfSwap, Catch, Surveillance;
		
	}
	
	// Tokens
	public static enum Tokens{
		TREASURE(0, "сокровища"), THREAT(1, "угрозы"), PROTECTION(2, "защиты"), BOMB(3, "бомбы"), STEAL(4, "кражи");
		
		private int index;
		private String name;
		Tokens(int index, String name) {
			this.index = index;
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		
		public String getName() {
			return name;
		}
	}
	
	// Number of Players
	private int numPlayers;
	
	// Lists
	private ArrayList<Card> refresh;
	private ArrayList<QuoteBox> quotes;
	private LinkedHashMap<String, ArrayList<Player>> teams;
	
	// Others
	private long fadingTime;

	public GameState(Handler handler) {
		
		super(handler);
		
		fadingTime = 30L * 1000L / 60L + 100L;
		
		setLayout();
		
		nt = new Notification(handler);
		
		refresh = new ArrayList<Card>();
		quotes = new ArrayList<QuoteBox>();
		teams = new LinkedHashMap<String, ArrayList<Player>>();
		
		background = new BufferedImage[] {ImageManager.getImage("/states/game/background1.png"),
				ImageManager.getImage("/states/game/background2.png")};
		
		frames = new BufferedImage[] {ImageManager.getImage("/states/game/frames1.png"),
				ImageManager.getImage("/states/game/frames2.png")};
		
		cap = ImageManager.getImage("/states/game/cap.png");		
		
		blackout = handler.getAssets().getBlackout();
		
		choice = handler.getAssets().getChoice();
		
		mode = gameModes.KvsI;
		
		modes = new ArrayList<Mode>();
		modes.add(new KvsI(handler));
		modes.add(new HvsS(handler));
		modes.add(new SpyTag(handler));
		modes.add(new TvsC(handler));
		modes.add(new MvsFBI(handler));
		modes.add(new Heist(handler));
		
		currAction = Actions.Idle;
		
	}
	
	public void setLayout() {
		
		boardX = (int) ((350 + 15) * Window.SCALE);
		boardY = (int) ((100 + 15) * Window.SCALE);
		boardW = (int) ((666 - 30) * Window.SCALE);
		boardH = (int) ((618 - 30) * Window.SCALE);
		
		previewX = (int) (1046 * Window.SCALE);
		previewY = (int) (100 * Window.SCALE);
		previewEvX = (int) (30 * Window.SCALE);
		previewEvY = (int) (100 * Window.SCALE);
		previewW = (int) (290 * Window.SCALE);
		previewH = (int) (411 * Window.SCALE);
		
		handX = (int) (1054 * Window.SCALE);
		handY = (int) (553 * Window.SCALE);
		handW = (int) (276 * Window.SCALE);
		handH = (int) (156 * Window.SCALE);
		
		othHandX = (int) (10 * Window.SCALE);
		othHandY = (int) (65 * Window.SCALE);
		othHandW = (int) (294 * Window.SCALE);
		
		labelPosLeftX = (int) (x0 + 20 * Window.SCALE);
		labelPosLeftY = (int) (y0 + 760 * Window.SCALE);
		labelPosRightX = (int) (x0 + 1346 * Window.SCALE);
		labelPosRightY = (int) (y0 + 760 * Window.SCALE);
		
		chooseTextY = (int) (x0 + 50 * Window.SCALE);
	}
	
	public void createRoleTextures() {
		if(handler.getPlayer().getRole() == null) {
			roleTexture = null;
			actionsTextTexture = null;
		} else {			
			roleTexture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[0][0];
			actionsTextTexture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[0][1];
		}
	}
	
	public void setupBoard() {
		setCurrAction(Actions.Init);
		if(mode.equals(gameModes.Heist)) {
			((Heist) modes.get(mode.getIndex())).createRolesDeck();
		}
		log = new Log(handler);
		handler.getLoadingState().setPercent(10);
		modes.get(mode.getIndex()).setTeams();
		handler.getLoadingState().setPercent(20);
		modes.get(mode.getIndex()).setRoles();
		handler.getLoadingState().setPercent(30);
		createRoleTextures();
		handler.getLoadingState().setPercent(40);
		modes.get(mode.getIndex()).createButtons();
		handler.getLoadingState().setPercent(50);
		modes.get(mode.getIndex()).setupBoard();
		handler.getLoadingState().setPercent(60);
		modes.get(mode.getIndex()).setWinCondition();
		handler.getLoadingState().setPercent(70);
		cm.scaleCards();
		handler.getLoadingState().setPercent(80);
		cm.layoutCards();
		handler.getLoadingState().setPercent(90);
		cm.createEvDeck();
		if(mode.equals(gameModes.TvsC)) {
			((TvsC) getCurrMode()).addMoney();
		} else if(mode.equals(gameModes.Heist)) {
			((Heist) getCurrMode()).createSurGrid();
		}
		handler.getLoadingState().setPercent(100);
	}
	
	public void setupBoard(int[][] mapInd) {
		setCurrAction(Actions.Init);
		log = new Log(handler);
		handler.getLoadingState().setPercent(20);
//		modes.get(mode.getIndex()).setRoles();
		handler.getLoadingState().setPercent(30);
		createRoleTextures();
		handler.getLoadingState().setPercent(40);
		modes.get(mode.getIndex()).createButtons();
		handler.getLoadingState().setPercent(55);
		modes.get(mode.getIndex()).setupBoard();
		handler.getLoadingState().setPercent(70);
		modes.get(mode.getIndex()).setWinCondition();
		handler.getLoadingState().setPercent(80);
		cm.scaleCards();
		handler.getLoadingState().setPercent(90);
		cm.layoutCards(mapInd);
		if(mode.equals(gameModes.TvsC)) {
			((TvsC) getCurrMode()).addMoney();
		} else if(mode.equals(gameModes.Heist)) {
			((Heist) getCurrMode()).createSurGrid();
		}
		handler.getLoadingState().setPercent(100);
	}
	
	public void move(int dir, int col, int row) {
		Card tempCard = null;
		Card[][] map = handler.getGameState().getMap();
		switch(dir) {
			case(8):
				for(int itRow = 0; itRow < getNumRows(); itRow++) {
					if(itRow == 0) {
						tempCard = map[getNumRows() - 1][col];
						map[getNumRows() - 1][col] = map[itRow][col];
						map[getNumRows() - 1][col].setColnRow(col, itRow - 1);
					} else if(itRow == getNumRows() - 1) {
						map[itRow - 1][col] = tempCard;
						map[itRow - 1][col].setColnRow(col, itRow - 1);
					} else {
						map[itRow - 1][col] = map[itRow][col];
						map[itRow - 1][col].setColnRow(col, itRow - 1);
					}
				}
			break;
			case(6):
				for(int itCol = getNumCols() - 1; itCol >= 0; itCol--) {
					if(itCol == getNumCols() - 1) {
						tempCard = map[row][0];
						map[row][0] = map[row][itCol];
						map[row][0].setColnRow(itCol + 1, row);
					} else if(itCol == 0) {
						map[row][itCol + 1] = tempCard;
						map[row][itCol + 1].setColnRow(itCol + 1, row);
					} else {
						map[row][itCol + 1] = map[row][itCol];
						map[row][itCol + 1].setColnRow(itCol + 1, row);
					}
				}
			break;
			case(2):
				for(int itRow = getNumRows() - 1; itRow >= 0; itRow--) {
					if(itRow == getNumRows() - 1) {
						tempCard = map[0][col];
						map[0][col] = map[itRow][col];
						map[0][col].setColnRow(col, itRow + 1);
					} else if(itRow == 0) {
						map[itRow + 1][col] = tempCard;
						map[itRow + 1][col].setColnRow(col, itRow + 1);
					} else {
						map[itRow + 1][col] = map[itRow][col];
						map[itRow + 1][col].setColnRow(col, itRow + 1);
					}
				}
			break;
			case(4):
				for(int itCol = 0; itCol < getNumCols(); itCol++) {
					if(itCol == 0) {
						tempCard = map[row][getNumCols() - 1];
						map[row][getNumCols() - 1] = map[row][itCol];
						map[row][getNumCols() - 1].setColnRow(itCol - 1, row);
					} else if(itCol == getNumCols() - 1) {
						map[row][itCol - 1] = tempCard;
						map[row][itCol - 1].setColnRow(itCol - 1, row);
					} else {
						map[row][itCol - 1] = map[row][itCol];
						map[row][itCol - 1].setColnRow(itCol - 1, row);
					}
				}
			break;
		}
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).checkCollapseAbility();
			}
		}
	}

	@Override
	public void tick() {
		
		try {
			for(Button b: buttons) {
				b.tick();
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
		}
		
		getCurrMode().tick();
		
		try {			
			for(Evidence ev: handler.getPlayer().getHand()) {
				ev.tick();
			}
		} catch(ConcurrentModificationException e) {}
		
		for(Player p: handler.getConnectedPlayers()) {
			p.tick();
		}
		
		cm.tick();
		
		if(log != null) {			
			log.tick();
		}
		
		try {
			for(QuoteBox q: quotes) {
				q.tick();
			}
		} catch(ConcurrentModificationException e) {}
		
		nt.tick();
		
	}

	@Override
	public void render(Graphics g) {
		
		if(mode.equals(gameModes.MvsFBI) || mode.equals(gameModes.Heist) || mode.equals(gameModes.SpyTag)) {
			g.drawImage(background[1], x0, y0, handler.getWidth(), handler.getHeight(), null);
		} else {			
			g.drawImage(background[0], x0, y0, handler.getWidth(), handler.getHeight(), null);
		}
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for(int i = 0; i < getNumRows(); i++) {
			Text.drawCenteredLine(String.valueOf(i + 1), x0 + boardX - 5 * Window.SCALE,
					cm.getFinY() + (cm.getCardH() + cm.getSpace()) * i + cm.getCardH() / 2,
					g2d, (float) (14 * Window.SCALE), Color.decode("#414244"));
		}
		for(int i = 0; i < getNumCols(); i++) {
			Text.drawCenteredLine(String.valueOf(i + 1),
					cm.getFinX() + (cm.getCardW() + cm.getSpace()) * i + cm.getCardW() / 2,
					y0 + boardY - 10 * Window.SCALE,
					g2d, (float) (14 * Window.SCALE), Color.decode("#414244"));
		}
		
		if(roleTexture != null) {			
			g.drawImage(roleTexture, x0 + previewX, y0 + previewY, previewW, previewH, null);
		}
		
		if(handler.getPlayer().getCard() != null) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, handler.getPlayer().getCard().getOpacity()));
			g2d.drawImage(handler.getPlayer().getCard().getLargeEvidenceTexture(), x0 + previewEvX, y0 + previewEvY, null);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
		
		if(mode.equals(gameModes.MvsFBI) || mode.equals(gameModes.Heist) || mode.equals(gameModes.SpyTag)) {			
			g.drawImage(frames[1], x0, y0, handler.getWidth(), handler.getHeight(), null);
		} else {
			g.drawImage(frames[0], x0, y0, handler.getWidth(), handler.getHeight(), null);			
		}
		
		if(mode.equals(gameModes.Heist)) {
			for(Vault[] vault: ((Heist) modes.get(mode.getIndex())).getRobed()) {
				for(Vault v: vault) {
					v.render(g);
				}
			}
		}
		
		cm.render(g);
		
		if(log != null) {			
			log.render(g);
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			p.render(g);
		}
		
		getCurrMode().render(g);
		
		if(actionsTextTexture != null) {			
			g.drawImage(actionsTextTexture, x0 + previewX, y0 + previewY, previewW, previewH, null);
		}
		
		try {			
			for(Evidence ev: handler.getPlayer().getHand()) {
				ev.render(g);
			}
			if(!cm.getTemps().isEmpty()) {
				for(Evidence c: cm.getTemps()) {
					c.render(g);
				}
			}
		} catch(ConcurrentModificationException e) {}
		
		for(Player p: handler.getConnectedPlayers()) {			
			if(!p.equals(handler.getPlayer())) {
				for(Evidence ev: p.getHand()) {
					ev.render(g);
				}	
			}
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getNamePlate() != null) {
				p.getNamePlate().render(g);
			}
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getNamePlate() != null) {
				if(p.getNamePlate().isIconHovering() && !p.equals(handler.getPlayer())) {
					g.drawImage(handler.getAssets().getRoles().get(p.getRole().getText())[0][0],
							(int) handler.getMm().getX(), (int) handler.getMm().getY(), null);
					break;
				}
			}
		}
		
		if(mode.equals(gameModes.MvsFBI)) {
			if(((MvsFBI) getCurrMode()).isReaction()) {
				double x = x0 + handler.getWidth() / 2 - ((MvsFBI) getCurrMode()).getReactionTxr().getWidth() / 2;
				double y = y0 + 70 * Window.SCALE;
				int w = (int) (((MvsFBI) getCurrMode()).getReactionTxr().getWidth() * Window.SCALE);
				int h = (int) (((MvsFBI) getCurrMode()).getReactionTxr().getHeight() * Window.SCALE);
				g.drawImage(((MvsFBI) getCurrMode()).getReactionTxr(), (int) x, (int) y, w, h, null);
				g2d.setColor(Color.decode("#232528"));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.fillRect((int) (x + 27 * Window.SCALE), (int) (y + 5 * Window.SCALE),
						(int) (670 * Window.SCALE), (int) (33 * Window.SCALE));
				Text.drawCenteredText(((MvsFBI) getCurrMode()).getReactionText(), x + w / 2, y + h / 2,
						g, Text.DEF_SIZE, Color.decode("#5E636B"), Color.decode("#383B3F"));
			}
		}
		
		g.drawImage(cap, x0, y0, handler.getWidth(), handler.getHeight(), null);
		
		Text.drawCenteredText("Карт в колоде: " + String.valueOf(cm.getEvDeck().size()),
				handler.getWidth() / 2, y0 + 743 * Window.SCALE, g, (float) (23 * Window.SCALE),
				Color.decode("#5D5E63"), Color.decode("#333438"));
		
		try {	
			for(Button b: buttons) {
				b.render(g);
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
		}
		
		try {
			for(QuoteBox q: quotes) {
				q.render(g);
			}
		} catch(ConcurrentModificationException e) {}
		
		if(handler.getSm().getCurrState() != StateManager.GAME) {
			g.drawImage(blackout, x0, y0, handler.getWidth(), handler.getHeight(), null);
		}
		
		nt.render(g);
		
		if(!cm.getChoicePoll().isEmpty()) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cm.getChoicePoll().get(0).getOpacity()));
			if(choiceLine != null)
			g2d.drawImage(choice, x0, y0, handler.getWidth(), handler.getHeight(), null);
			Text.drawCenteredLine(choiceLine, handler.getFrameWidth() / 2, chooseTextY, g2d, 34f, Color.decode("#CCCCCC"));
			if(cm.getChoicePoll().size() < 4) {					
				for(Evidence c: cm.getChoicePoll()) {
					c.render(g);
				}
			} else {
				for(Evidence c: cm.getChoicePoll()) {
					if(cm.getChoicePoll().indexOf(c) % 2 == 0) {							
						c.render(g);
					}
				}
				for(Evidence c: cm.getChoicePoll()) {
					if(cm.getChoicePoll().indexOf(c) % 2 != 0) {							
						c.render(g);
					}
				}
			}
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
		
	}
	
	// Getters and Setters
	
	@Override
	public ArrayList<Button> getButtons() {
		return buttons;
	}
	
	public void setNumCols(int numCols) {
		getCurrMode().setNumCols(numCols);
		cm.setMap(new Card[getNumRows()][numCols]);
	}
	
	public void setNumRows(int numRows) {
		getCurrMode().setNumRows(numRows);
		cm.setMap(new Card[numRows][getNumCols()]);
	}
	
	public Mode getCurrMode() {
		return modes.get(mode.getIndex());
	}

	public Card[][] getMap() {
		return cm.getMap();
	}

	public gameModes getMode() {
		return mode;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public int getNumCols() {
		return getCurrMode().getNumCols();
	}
	
	public int getNumRows() {
		return getCurrMode().getNumRows();
	}
	
	public void setMode(gameModes mode) {
		this.mode = mode;
	}

	public ArrayList<Card> getRefresh() {
		return refresh;
	}

	public void setRefresh(ArrayList<Card> refresh) {
		this.refresh = refresh;
	}

	public Actions getCurrAction() {
		return currAction;
	}

	public void setCurrAction(Actions currAction) {
		
		switch(currAction) {
		case Init:
			choiceLine = "Выберите свою тайную личность";
			break;
		case FBICanvas:
		case Profile:
		case Defend:
		case Exonerate:
			choiceLine = "Выберите невиновного";
			break;
		default:
			choiceLine = "Выберите новую личность";
			break;
		}
		
		if(currAction != Actions.Idle) {
			getCurrMode().deactivateButtons();
			if(!(this.currAction == Actions.Init
			&& currAction == Actions.Murder)
			&& currAction != Actions.Disguise
			&& currAction != Actions.Exonerate
			&& currAction != Actions.Defend
			&& currAction != Actions.Evade
			&& currAction != Actions.Change
			&& currAction != Actions.Threat
			&& currAction != Actions.Marker
			&& currAction != Actions.Protect
			&& currAction != Actions.Deputize
			&& currAction != Actions.StealWholeBoard
			&& currAction != Actions.SwapTo
			&& !(currAction == Actions.Detonate && !handler.getPlayer().isYourTurn())) {				
				for(Button b: getCurrMode().getButtons()) {
					if(b instanceof CancelButton) {
						b.setActive(true);
					}
				}
			}
		} else {
			switch(this.currAction) {
			case Shift:
				for(Button b: getCurrMode().getButtons()) {
					if(b instanceof ShiftButton) {
						((ShiftButton) b).clearArrows();
					}
				}
				break;
			default:
				cm.deactivateAll();
				cm.deselectAll();
				break;
			}
		}
		
		for(Button b: getCurrMode().getButtons()) {
			if(b instanceof PassTurnButton) {
				((PassTurnButton) b).setActive(true);
			}
		}
		
		this.currAction = currAction;
		
	}

	public Notification getNt() {
		return nt;
	}

	public int getBoardW() {
		return boardW;
	}

	public int getBoardH() {
		return boardH;
	}

	public int getPreviewW() {
		return previewW;
	}

	public int getPreviewH() {
		return previewH;
	}

	public int getBoardX() {
		return boardX;
	}

	public int getBoardY() {
		return boardY;
	}

	public CardsManager getCm() {
		return cm;
	}

	public int getPreviewEvX() {
		return previewEvX;
	}

	public int getPreviewEvY() {
		return previewEvY;
	}

	public int getPreviewX() {
		return previewX;
	}

	public int getPreviewY() {
		return previewY;
	}

	public int getHandX() {
		return handX;
	}

	public int getHandY() {
		return handY;
	}

	public int getHandW() {
		return handW;
	}

	public int getHandH() {
		return handH;
	}

	public ArrayList<QuoteBox> getQuotes() {
		return quotes;
	}

	public int getOthHandX() {
		return othHandX;
	}

	public int getOthHandY() {
		return othHandY;
	}

	public int getOthHandW() {
		return othHandW;
	}
	
	public void setCm(CardsManager cm) {
		this.cm = cm;
	}

	public long getFadingTime() {
		return fadingTime;
	}

	public LinkedHashMap<String, ArrayList<Player>> getTeams() {
		return teams;
	}

	public Log getLog() {
		return log;
	}

	public int getLabelPosLeftX() {
		return labelPosLeftX;
	}

	public int getLabelPosLeftY() {
		return labelPosLeftY;
	}

	public int getLabelPosRightX() {
		return labelPosRightX;
	}

	public int getLabelPosRightY() {
		return labelPosRightY;
	}
	
	public void setChoiceLine(String choiceLine) {
		this.choiceLine = choiceLine;
	}
	
}
