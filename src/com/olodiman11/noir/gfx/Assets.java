package com.olodiman11.noir.gfx;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.states.GameState.Tokens;

public class Assets {
	
	// Cards
	private final BufferedImage[] cards, dead, evidence;
	private final BufferedImage faceDown, highlight, shade, shadow, deceased;
	
	// Buttons
	private final BufferedImage[] cancelButton, cancel, collapse, up, down, left, right, cols, rows;
	
	// Roles n Actions
	private final HashMap<String, BufferedImage[][]> roles;
	
	// Options
	private final BufferedImage[] check, numRollLeftAr, numRollRightAr;
	private final BufferedImage numRoll;
	
	// Nameplates
	private final HashMap<String, BufferedImage> nameplates, smallNameplates;
	
	// RoleIcons
	private final HashMap<String, HashMap<String, BufferedImage>> roleIcons; //killerIcon, inspectorIcon, hitmanIcon, sleuthIcon, spyIcon, masterThiefIcon, chiefOfPoliceIcon;
	
	// Action Icons
	private final BufferedImage[] collapseCheck, capture, canvas, disable, silence, accuse, steal, kill, murder,
	autopsy, threat, bomb, protectionAdd, protectionRemove, swap, swapFrom, swapTo, detonate;
	
	// Tokens
	private final BufferedImage[] tokens;
	private final HashMap<String, BufferedImage> markers;
	private final BufferedImage tknHighlight;
	
	// Modes
	private final BufferedImage[] modes, modeChoice;
	
	// Cursors
	private final BufferedImage[] cursors;
	
	// Others
	private final BufferedImage quote, textField, inputField, cursor, selected, blackout, warning, choice, target, squareHighlight, info;
			
	public Assets() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();	
		double scale;
		
		if((double) screenSize.getWidth() / (double) screenSize.getHeight() >= 16D/9D) {
			scale = (double) screenSize.getHeight() / 768D;
		} else {
			scale = (double) screenSize.getWidth() / 1366D;
		}
		
		// Cards
		cards = new BufferedImage[50];
		dead = new BufferedImage[50];
		evidence = new BufferedImage[50];
		for(int i = 0; i < cards.length; i++) {
			cards[i] = ImageManager.getImage("/cards/alive/" + String.valueOf(i + 1) + ".png");
			dead[i] = ImageManager.getImage("/cards/dead/" + String.valueOf(i + 1) + ".png");
			evidence[i] = ImageManager.getImage("/cards/evidence/" + String.valueOf(i + 1) + ".png");
		}
		faceDown = ImageManager.getImage("/cards/evidenceFaceDown.png");
		highlight = ImageManager.getImage("/cards/highlight.png");
		shade = ImageManager.getImage("/cards/shade.png");
		shadow = ImageManager.getImage("/cards/shadow.png");
		deceased = ImageManager.getImage("/cards/evDeceased.png");
		
		// Buttons
		cancel = new BufferedImage[4];
		cancel[0] = ImageManager.getImage("/buttons/others/cancel.png");
		cancel[1] = ImageManager.getImage("/buttons/others/selected/cancel.png");
		cancel[2] = ImageManager.getImage("/buttons/others/pressed/cancel.png");
		cancel[3] = ImageManager.getImage("/buttons/others/disabled/cancel.png");
		for(BufferedImage i: cancel) {
			i = ImageManager.scaleImage(i, (int) (65 * scale), (int) (70 * scale));
		}
		
		collapse = new BufferedImage[4];
		collapse[0] = ImageManager.getImage("/buttons/others/collapse.png");
		collapse[1] = ImageManager.getImage("/buttons/others/selected/collapse.png");
		collapse[2] = ImageManager.getImage("/buttons/others/pressed/collapse.png");
		collapse[3] = ImageManager.getImage("/buttons/others/disabled/collapse.png");
		for(BufferedImage i: collapse) {
			i = ImageManager.scaleImage(i, (int) (65 * scale), (int) (70 * scale));
		}
		
		String text;
		text = "upArrow";
		up = new BufferedImage[5];
		up[0] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		up[1] = ImageManager.getImage("/buttons/arrows/selected/" + text + ".png");
		up[2] = ImageManager.getImage("/buttons/arrows/pressed/" + text + ".png");
		up[3] = ImageManager.getImage("/buttons/arrows/disabled/" + text + ".png");
		up[4] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		
		text = "downArrow";
		down = new BufferedImage[5];
		down[0] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		down[1] = ImageManager.getImage("/buttons/arrows/selected/" + text + ".png");
		down[2] = ImageManager.getImage("/buttons/arrows/pressed/" + text + ".png");
		down[3] = ImageManager.getImage("/buttons/arrows/disabled/" + text + ".png");
		down[4] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		
		text = "leftArrow";
		left = new BufferedImage[5];
		left[0] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		left[1] = ImageManager.getImage("/buttons/arrows/selected/" + text + ".png");
		left[2] = ImageManager.getImage("/buttons/arrows/pressed/" + text + ".png");
		left[3] = ImageManager.getImage("/buttons/arrows/disabled/" + text + ".png");
		left[4] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		
		text = "rightArrow";
		right = new BufferedImage[5];
		right[0] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		right[1] = ImageManager.getImage("/buttons/arrows/selected/" + text + ".png");
		right[2] = ImageManager.getImage("/buttons/arrows/pressed/" + text + ".png");
		right[3] = ImageManager.getImage("/buttons/arrows/disabled/" + text + ".png");
		right[4] = ImageManager.getImage("/buttons/arrows/" + text + ".png");
		
		for(int i = 0; i < up.length; i++) {
			up[i] = ImageManager.scaleImage(up[i], (int) (54 * scale), (int) (56 * scale));
			down[i] = ImageManager.scaleImage(down[i], (int) (54 * scale), (int) (56 * scale));
			left[i] = ImageManager.scaleImage(left[i], (int) (54 * scale), (int) (56 * scale));
			right[i] = ImageManager.scaleImage(right[i], (int) (54 * scale), (int) (56 * scale));
		}
		
		text = "columns";
		cols = new BufferedImage[3];
		cols[0] = ImageManager.getImage("/buttons/others/" + text + ".png");
		cols[1] = ImageManager.getImage("/buttons/others/selected/" + text + ".png");
		cols[2] = ImageManager.getImage("/buttons/others/pressed/" + text + ".png");
		for(BufferedImage i: cols) {
			i = ImageManager.scaleImage(i, (int) (65 * scale), (int) (70 * scale));
		}
		
		text = "rows";
		rows = new BufferedImage[3];
		rows[0] = ImageManager.getImage("/buttons/others/" + text + ".png");
		rows[1] = ImageManager.getImage("/buttons/others/selected/" + text + ".png");
		rows[2] = ImageManager.getImage("/buttons/others/pressed/" + text + ".png");
		for(BufferedImage i: rows) {
			i = ImageManager.scaleImage(i, (int) (65 * scale), (int) (70 * scale));
		}
		
		// Roles n Actions
		roles = new HashMap<String, BufferedImage[][]>();
		int width = (int) (290 * scale);
		int height = (int) (411 * scale);
		
		// Killer
		BufferedImage[][] bi  = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/killer.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/killer/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/killer/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/killer/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/killer/murder.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/killer/murderSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/killer/murderDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/killer/disguise.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/killer/disguiseSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/killer/disguiseDis.png");
		roles.put("killer", bi);
		
		// Inspector
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/inspector.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/inspector/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/inspector/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/inspector/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/inspector/accuse.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/inspector/accuseSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/inspector/accuseDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/inspector/exonerate.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/inspector/exonerateSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/inspector/exonerateDis.png");
		roles.put("inspector", bi);
		
		// Hitman
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/hitman.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/hitman/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/hitman/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/hitman/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/hitman/kill.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/hitman/killSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/hitman/killDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/hitman/evade.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/hitman/evadeSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/hitman/evadeDis.png");
		roles.put("hitman", bi);
		
		// Sleuth
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/sleuth.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/sleuth/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/sleuth/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/sleuth/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/sleuth/investigate.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/sleuth/investigateSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/sleuth/investigateDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/sleuth/defend.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/sleuth/defendSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/sleuth/defendDis.png");
		roles.put("sleuth", bi);
		
		// Spy
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/spy.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/spy/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/spy/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/spy/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/spy/capture.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/spy/captureSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/spy/captureDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/spy/canvas.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/spy/canvasSel.png");
		roles.put("spy", bi);
		
		// Master Thief
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/masterThief.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/masterThief/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/masterThief/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/masterThief/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/masterThief/steal.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/masterThief/stealSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/masterThief/stealDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/masterThief/change.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/masterThief/changeSel.png");
		roles.put("masterThief", bi);
		
		// Chief of Police
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/chiefOfPolice.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/chiefOfPolice/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/chiefOfPolice/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/chiefOfPolice/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/chiefOfPolice/charge.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/chiefOfPolice/chargeSel.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/chiefOfPolice/deputize.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/chiefOfPolice/deputizeSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/chiefOfPolice/deputizeDis.png");
		roles.put("chiefOfPolice", bi);
		
		// Thug
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/thug.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/thug/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/thug/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/thug/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/thug/kill.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/thug/killSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/thug/killDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/thug/disguise.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/thug/disguiseSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/thug/disguiseDis.png");
		roles.put("thug", bi);
		
		// Psycho
		bi = new BufferedImage[3][2];
		bi[0][0] = ImageManager.getImage("/roles/psycho.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/psycho/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/psycho/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/psycho/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/psycho/swap.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/psycho/swapSel.png");
		roles.put("psycho", bi);
		
		// Bomber
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/bomber.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/bomber/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/bomber/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/bomber/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/bomber/bomb.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/bomber/bombSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/bomber/bombDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/bomber/detonate.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/bomber/detonateSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/bomber/detonateDis.png");
		roles.put("bomber", bi);
		
		// Sniper
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/sniper.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/sniper/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/sniper/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/sniper/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/sniper/setup.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/sniper/setupSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/sniper/setupDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/sniper/snipe.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/sniper/snipeSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/sniper/snipeDis.png");
		roles.put("sniper", bi);
		
		// Undercover
		bi = new BufferedImage[6][3];
		bi[0][0] = ImageManager.getImage("/roles/undercover.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/undercover/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/undercover/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/undercover/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/undercover/accuse.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/undercover/accuseSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/undercover/accuseDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/undercover/disguise.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/undercover/disguiseSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/undercover/disguiseDis.png");
		bi[4][0] = ImageManager.getImage("/roles/actions/undercover/autopsy.png");
		bi[4][1] = ImageManager.getImage("/roles/actions/undercover/autopsySel.png");
		bi[4][2] = ImageManager.getImage("/roles/actions/undercover/autopsyDis.png");
		bi[5][0] = ImageManager.getImage("/roles/actions/undercover/disarm.png");
		bi[5][1] = ImageManager.getImage("/roles/actions/undercover/disarmSel.png");
		bi[5][2] = ImageManager.getImage("/roles/actions/undercover/disarmDis.png");
		roles.put("undercover", bi);
		
		// Detective
		bi = new BufferedImage[5][3];
		bi[0][0] = ImageManager.getImage("/roles/detective.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/detective/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/detective/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/detective/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/detective/farAccuse.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/detective/farAccuseSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/detective/farAccuseDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/detective/canvas.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/detective/canvasSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/detective/canvasDis.png");
		bi[4][0] = ImageManager.getImage("/roles/actions/detective/disarm.png");
		bi[4][1] = ImageManager.getImage("/roles/actions/detective/disarmSel.png");
		bi[4][2] = ImageManager.getImage("/roles/actions/detective/disarmDis.png");
		roles.put("detective", bi);
		
		// Suit
		bi = new BufferedImage[5][3];
		bi[0][0] = ImageManager.getImage("/roles/suit.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/suit/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/suit/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/suit/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/suit/accuse.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/suit/accuseSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/suit/accuseDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/suit/protect.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/suit/protectSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/suit/protectDis.png");
		bi[4][0] = ImageManager.getImage("/roles/actions/suit/disarm.png");
		bi[4][1] = ImageManager.getImage("/roles/actions/suit/disarmSel.png");
		bi[4][2] = ImageManager.getImage("/roles/actions/suit/disarmDis.png");
		roles.put("suit", bi);
		
		// Profiler
		bi = new BufferedImage[5][3];
		bi[0][0] = ImageManager.getImage("/roles/profiler.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/profiler/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/profiler/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/profiler/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/profiler/accuse.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/profiler/accuseSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/profiler/accuseDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/profiler/profile.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/profiler/profileSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/profiler/profileDis.png");
		bi[4][0] = ImageManager.getImage("/roles/actions/profiler/disarm.png");
		bi[4][1] = ImageManager.getImage("/roles/actions/profiler/disarmSel.png");
		bi[4][2] = ImageManager.getImage("/roles/actions/profiler/disarmDis.png");
		roles.put("profiler", bi);
		
		// Safecracker
		bi = new BufferedImage[3][3];
		bi[0][0] = ImageManager.getImage("/roles/safecracker.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/safecracker/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/safecracker/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/safecracker/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/safecracker/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/safecracker/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/safecracker/robDis.png");
		roles.put("safecracker", bi);
		
		// Runner
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/runner.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/runner/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/runner/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/runner/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/runner/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/runner/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/runner/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/runner/fastShift.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/runner/fastShiftSel.png");
		roles.put("runner", bi);
		
		// Cleaner
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/cleaner.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/cleaner/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/cleaner/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/cleaner/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/cleaner/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/cleaner/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/cleaner/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/cleaner/disable.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/cleaner/disableSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/cleaner/disableDis.png");
		roles.put("cleaner", bi);
		
		// Decoy
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/decoy.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/decoy/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/decoy/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/decoy/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/decoy/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/decoy/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/decoy/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/decoy/vanish.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/decoy/vanishSel.png");
		roles.put("decoy", bi);
		
		// Insider
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/insider.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/insider/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/insider/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/insider/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/insider/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/insider/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/insider/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/insider/insideJob.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/insider/insideJobSel.png");
		roles.put("insider", bi);
		
		// Hacker
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/hacker.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/hacker/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/hacker/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/hacker/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/hacker/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/hacker/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/hacker/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/hacker/hack.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/hacker/hackSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/hacker/hackDis.png");
		roles.put("hacker", bi);
		
		// Silencer
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/silencer.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/silencer/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/silencer/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/silencer/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/silencer/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/silencer/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/silencer/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/silencer/silence.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/silencer/silenceSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/silencer/silenceDis.png");
		roles.put("silencer", bi);
		
		// Mimic
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/mimic.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/mimic/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/mimic/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/mimic/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/mimic/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/mimic/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/mimic/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/mimic/duplicate.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/mimic/duplicateSel.png");
		roles.put("mimic", bi);
		
		// Infiltrator
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/infiltrator.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/infiltrator/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/infiltrator/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/infiltrator/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/infiltrator/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/infiltrator/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/infiltrator/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/infiltrator/adSwap.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/infiltrator/adSwapSel.png");
		roles.put("infiltrator", bi);
		
		// Sneak
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/sneak.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/sneak/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/sneak/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/sneak/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/sneak/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/sneak/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/sneak/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/sneak/stealthyShift.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/sneak/stealthyShiftSel.png");
		roles.put("sneak", bi);
		
		// MasterSafecracker
		bi = new BufferedImage[4][3];
		bi[0][0] = ImageManager.getImage("/roles/masterSafecracker.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/masterSafecracker/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/masterSafecracker/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/masterSafecracker/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/masterSafecracker/rob.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/masterSafecracker/robSel.png");
		bi[2][2] = ImageManager.getImage("/roles/actions/masterSafecracker/robDis.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/masterSafecracker/safebreaking.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/masterSafecracker/safebreakingSel.png");
		bi[3][2] = ImageManager.getImage("/roles/actions/masterSafecracker/safebreakingDis.png");
		roles.put("masterSafecracker", bi);
		
		// SecurityChief
		bi = new BufferedImage[5][3];
		bi[0][0] = ImageManager.getImage("/roles/securityChief.png");
		bi[0][0] = ImageManager.scaleImage(bi[0][0], width, height);
		bi[0][1] = ImageManager.getImage("/roles/actions/securityChief/text.png");
		bi[0][1] = ImageManager.scaleImage(bi[0][1], width, height);
		bi[1][0] = ImageManager.getImage("/roles/actions/securityChief/shift.png");
		bi[1][1] = ImageManager.getImage("/roles/actions/securityChief/shiftSel.png");
		bi[2][0] = ImageManager.getImage("/roles/actions/securityChief/ofSwap.png");
		bi[2][1] = ImageManager.getImage("/roles/actions/securityChief/ofSwapSel.png");
		bi[3][0] = ImageManager.getImage("/roles/actions/securityChief/surveillance.png");
		bi[3][1] = ImageManager.getImage("/roles/actions/securityChief/surveillanceSel.png");
		bi[4][0] = ImageManager.getImage("/roles/actions/securityChief/catch.png");
		bi[4][1] = ImageManager.getImage("/roles/actions/securityChief/catchSel.png");
		roles.put("securityChief", bi);
		
		// Options
		check = new BufferedImage[3];
		check[0] = ImageManager.getImage("/states/mainmenu/check.png");
		check[1] = ImageManager.getImage("/states/mainmenu/checkSel.png");
		check[2] = ImageManager.getImage("/states/mainmenu/checkPress.png");
		
		numRoll = ImageManager.getImage("/numRoll/numRoll.png");
		
		numRollLeftAr = new BufferedImage[3];
		numRollLeftAr[0] = ImageManager.getImage("/numRoll/numRollLeftAr.png");
		numRollLeftAr[1] = ImageManager.getImage("/numRoll/numRollLeftArSel.png");
		numRollLeftAr[2] = ImageManager.getImage("/numRoll/numRollLeftArPress.png");
		
		numRollRightAr = new BufferedImage[3];
		numRollRightAr[0] = ImageManager.getImage("/numRoll/numRollRightAr.png");     
		numRollRightAr[1] = ImageManager.getImage("/numRoll/numRollRightArSel.png");  
		numRollRightAr[2] = ImageManager.getImage("/numRoll/numRollRightArPress.png");
		
		String[] colors = new String[] {"red", "orange", "yellow", "green", "blue", "navy", "purple", "brown", "silver"};
		// Nameplates
		nameplates = new HashMap<String, BufferedImage>();
		for(String s: colors) {
			nameplates.put(s, ImageManager.getImage("/player/nameplates/big/" + s + ".png"));
		}
		smallNameplates = new HashMap<String, BufferedImage>();
		for(String s: colors) {
			smallNameplates.put(s, ImageManager.getImage("/player/nameplates/small/" + s + ".png"));
		}
		
		// Markers
		markers = new HashMap<String, BufferedImage>();
		for(String s: colors) {			
			markers.put(s, ImageManager.getImage("/markers/" + s + ".png"));
		}
		
		// RoleIcons
		roleIcons = new HashMap<String, HashMap<String, BufferedImage>>();
		for(Roles r: Roles.values()) {			
			HashMap<String, BufferedImage> temp = new HashMap<String, BufferedImage>();
			for(int i = 0; i < colors.length; i++) {
				temp.put(colors[i], ImageManager.scaleImage(ImageManager.getImage(
						"/player/icons/" + r.getText() + "/" + colors[i] + ".png"), (int) (60 * scale), (int) (60 * scale)));
			}
			roleIcons.put(r.getText(), temp);
		}
		
		cancelButton = new BufferedImage[3];
		cancelButton[0] = ImageManager.getImage("/buttons/cancel/cancel.png");
		cancelButton[1] = ImageManager.getImage("/buttons/cancel/cancelSel.png");
		cancelButton[2] = ImageManager.getImage("/buttons/cancel/cancelPress.png");
		cancelButton[0] = ImageManager.scaleImage(cancelButton[0], (int) (50 * scale), (int) (50 * scale));
		cancelButton[1] = ImageManager.scaleImage(cancelButton[1], (int) (50 * scale), (int) (50 * scale));
		cancelButton[2] = ImageManager.scaleImage(cancelButton[2], (int) (50 * scale), (int) (50 * scale));
		
		// Action Icons
		collapseCheck = new BufferedImage[4];
		collapseCheck[0] = ImageManager.getImage("/buttons/others/check.png");
		collapseCheck[1] = ImageManager.getImage("/buttons/others/checkPending.png");
		collapseCheck[2] = ImageManager.getImage("/buttons/others/selected/check.png");
		collapseCheck[3] = ImageManager.getImage("/buttons/others/pressed/check.png");
		
		text = "murder";
		murder = new BufferedImage[3];
		murder[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		murder[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		murder[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "kill";
		kill = new BufferedImage[3];
		kill[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		kill[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		kill[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "capture";
		capture = new BufferedImage[3];
		capture[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		capture[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		capture[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "canvas";
		canvas = new BufferedImage[3];
		canvas[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		canvas[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		canvas[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "capture";
		autopsy = new BufferedImage[3];
		autopsy[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		autopsy[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		autopsy[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "disable";
		disable = new BufferedImage[3];
		disable[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		disable[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		disable[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "capture";
		silence = new BufferedImage[3];
		silence[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		silence[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		silence[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "accuse";
		accuse = new BufferedImage[3];
		accuse[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		accuse[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		accuse[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "threat";
		threat = new BufferedImage[3];
		threat[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		threat[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		threat[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "bomb";
		bomb = new BufferedImage[3];
		bomb[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		bomb[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		bomb[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "swap";
		swap = new BufferedImage[3];
		swap[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		swap[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		swap[2] = swap[0];
		
		swapFrom = new BufferedImage[3];
		swapFrom[0] = swap[0];
		swapFrom[1] = swap[1];
		swapFrom[2] = swap[1];
		
		text = "swapTo";
		swapTo = new BufferedImage[3];
		swapTo[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		swapTo[1] = swap[1];
		swapTo[2] = swap[1];
		
		text = "capture";
		detonate = new BufferedImage[3];
		detonate[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		detonate[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		detonate[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "capture";
		steal = new BufferedImage[3];
		steal[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		steal[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		steal[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "protectionAdd";
		protectionAdd = new BufferedImage[3];
		protectionAdd[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		protectionAdd[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		protectionAdd[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
		
		text = "protectionRemove";
		protectionRemove = new BufferedImage[3];
		protectionRemove[0] = ImageManager.getImage("/buttons/actions/" + text + ".png");
		protectionRemove[1] = ImageManager.getImage("/buttons/actions/selected/" + text + ".png");
		protectionRemove[2] = ImageManager.getImage("/buttons/actions/pressed/" + text + ".png");
			
		// Tokens
		tokens = new BufferedImage[4];
		tokens[Tokens.TREASURE.getIndex()] = ImageManager.getImage("/tokens/treasure.png");
		tokens[Tokens.THREAT.getIndex()] = ImageManager.getImage("/tokens/threat.png");
		tokens[Tokens.PROTECTION.getIndex()] = ImageManager.getImage("/tokens/protection.png");
		tokens[Tokens.BOMB.getIndex()] = ImageManager.getImage("/tokens/bomb.png");
		tknHighlight = ImageManager.getImage("/tokens/highlight.png");
		
		// Modes
		String[] modes = new String[] {"kvsi", "hvss", "spytag", "tvsc", "mvsfbi", "heist"};
		this.modes = new BufferedImage[6];
		for(int i = 0; i < modes.length; i++) {			
			this.modes[i] = ImageManager.scaleImage(ImageManager.getImage("/modes/" + modes[i] + ".png"), width, height);
		}
			
		modeChoice = new BufferedImage[6];
		for(int i = 0; i < modeChoice.length; i++) {
			modeChoice[i] = ImageManager.getImage("/modes/choice/" + modes[i] + ".png");
			modeChoice[i] = ImageManager.scaleImage(modeChoice[i],
					(int) (modeChoice[i].getWidth() * scale), (int) (modeChoice[i].getHeight() * scale));
		}
		
		// Cursors
		cursors = new BufferedImage[4];
		cursors[0] = ImageManager.getImage("/cursors/def.png");
		cursors[1] = ImageManager.getImage("/cursors/hand.png");
		cursors[2] = ImageManager.getImage("/cursors/wait.png");
		cursors[3] = ImageManager.getImage("/cursors/text.png");
		
		// Others
		quote = ImageManager.scaleImage(ImageManager.getImage("/player/others/quote.png"), (int) (10 * scale), (int) (30 * scale));
		textField = ImageManager.getImage("/textfield/texture.png");
		inputField = ImageManager.getImage("/textfield/input.png");
		cursor = ImageManager.getImage("/textfield/cursor.png");
		selected = ImageManager.scaleImage(ImageManager.getImage("/cards/selected.png"), width, height);
		blackout = ImageManager.getImage("/states/game/blackout.png");
		warning = ImageManager.getImage("/states/mainmenu/warning.png");
		choice = ImageManager.getImage("/states/game/choice.png");
		target = ImageManager.getImage("/cards/target.png");
		squareHighlight = ImageManager.getImage("/modes/squareHighlight.png");
		info = ImageManager.getImage("/misc/info.png");
		
	}

	public BufferedImage[] getCards() {
		return cards;
	}

	public BufferedImage[] getDead() {
		return dead;
	}

	public BufferedImage[] getEvidence() {
		return evidence;
	}

	public BufferedImage getFaceDown() {
		return faceDown;
	}

	public BufferedImage getHighlight() {
		return highlight;
	}

	public BufferedImage getShade() {
		return shade;
	}

	public BufferedImage getShadow() {
		return shadow;
	}

	public BufferedImage[] getCancel() {
		return cancel;
	}

	public BufferedImage[] getCollapse() {
		return collapse;
	}

	public BufferedImage[] getUp() {
		return up;
	}

	public BufferedImage[] getDown() {
		return down;
	}

	public BufferedImage[] getLeft() {
		return left;
	}

	public BufferedImage[] getRight() {
		return right;
	}

	public BufferedImage[] getCols() {
		return cols;
	}

	public BufferedImage[] getRows() {
		return rows;
	}

	public BufferedImage[] getCheck() {
		return check;
	}

	public BufferedImage[] getCollapseCheck() {
		return collapseCheck;
	}

	public BufferedImage[] getMurder() {
		return murder;
	}

	public BufferedImage[] getKill() {
		return kill;
	}

	public BufferedImage[] getCapture() {
		return capture;
	}

	public BufferedImage[] getCanvas() {
		return canvas;
	}

	public BufferedImage getTarget() {
		return target;
	}

	public HashMap<String, BufferedImage[][]> getRoles() {
		return roles;
	}

	public HashMap<String, BufferedImage> getNameplates() {
		return nameplates;
	}

	public HashMap<String, HashMap<String, BufferedImage>> getRoleIcons() {
		return roleIcons;
	}

	public BufferedImage getQuote() {
		return quote;
	}

	public BufferedImage[] getTokens() {
		return tokens;
	}
	
	public HashMap<String, BufferedImage> getSmallNameplates() {
		return smallNameplates;
	}

	public BufferedImage getTextField() {
		return textField;
	}

	public BufferedImage getInputField() {
		return inputField;
	}

	public BufferedImage getCursor() {
		return cursor;
	}

	public HashMap<String, BufferedImage> getMarkers() {
		return markers;
	}

	public BufferedImage getSelected() {
		return selected;
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

	public BufferedImage[] getCancelButton() {
		return cancelButton;
	}

	public BufferedImage getBlackout() {
		return blackout;
	}

	public BufferedImage getWarning() {
		return warning;
	}

	public BufferedImage[] getModes() {
		return modes;
	}

	public BufferedImage getChoice() {
		return choice;
	}

	public BufferedImage[] getModeChoice() {
		return modeChoice;
	}

	public BufferedImage getSquareHighlight() {
		return squareHighlight;
	}

	public BufferedImage[] getCursors() {
		return cursors;
	}

	public BufferedImage getNumRoll() {
		return numRoll;
	}

	public BufferedImage[] getNumRollLeftAr() {
		return numRollLeftAr;
	}

	public BufferedImage[] getNumRollRightAr() {
		return numRollRightAr;
	}

	public BufferedImage getTknHighlight() {
		return tknHighlight;
	}

	public BufferedImage getDeceased() {
		return deceased;
	}

	public BufferedImage getInfo() {
		return info;
	}
	
}
