package com.turruc.game.screens;

import java.io.File;

import com.turruc.engine.AbstractGame;
import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.Button;
import com.turruc.engine.gfx.Image;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.game.GameManager;
import com.turruc.game.GameState;
import com.turruc.game.Level;

public class LevelSelect extends AbstractGame {
	private static GameContainer gc;
	private Image levelSelect;
	Button[] buttons;
	static Level[] levels;

	Button back;
	Button okay;

	Level levelToLoad;

	private SoundClip click;

	int selected = -1;

	public LevelSelect() {
		levelSelect = new Image("/levelSelect.png");
		levels = new Level[12];
		buttons = new Button[12];
		click = new SoundClip("/audio/click.wav");
		click.setVolume(6);
		for (int i = 0; i < 12; i++) {
			buttons[i] = new Button((i % 4) * 200 + 50, ((i / 4) * 90) + 130, 800 / 8, 480 / 8, 0xff000000, "Empty");
		}

		back = new Button(89, 410, 202, 61, 0xff000000, "");
		okay = new Button(508, 410, 202, 61, 0xff000000, "");

		// TODO: load levels from a file into levels array
		for (int i = 1; i <= 12; i++) {
			String path = "/levels/level" + i;

			boolean correct = checkFiles(path);
			if(correct) {
				levels[i - 1] = new Level(new Image(path + "/levelImage.png"), new Image(path + "/background.png"), new Image(path + "/midground.png"), new ImageTile(path + "/tileset.png", 32, 32), new Image(path + "/levelPreview.png"));
			}

		}
	}

	private boolean checkFiles(String path) {
		File file = new File("");
		
		path = file.getAbsolutePath() + File.separator + "resources" + path;
		File folder = new File(path);
		if(!folder.exists()) {
			System.err.println("Cannot find folder at " + path);
			return false;
		}

		File levelImage = new File(path + "/levelImage.png");
		if(!levelImage.exists()) {
			System.err.println("Cannot find levelImage.png");
			return false;
		}

		File background = new File(path + "/background.png");
		if(!background.exists()) {
			System.err.println("Cannot find background.png");
			return false;
		}

		File midground = new File(path + "/midground.png");
		if(!midground.exists()) {
			System.err.println("Cannot find midground.png");
			return false;
		}

		File levelPreview = new File(path + "/levelPreview.png");
		if(!levelPreview.exists()) {
			System.err.println("Cannot find levelPreview.png");
			return false;
		}
		return true;
	}

	@Override
	public void init(GameContainer gc) {
		gc.getRenderer().setAmbientColor(-1);
	}

	@Override
	public void update(GameContainer gc, float dt) {
		if (gc.getInput().isButtonDown(1)) {
			if (back.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();
				gc.gameState = GameState.MENU;
			} else if (okay.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();
				// TODO: Set the selected level as the new level
				if(levelToLoad!=null) {
					levelToLoad.loadLevel();
					GameManager.gm.level = levelToLoad;
					gc.gameState = GameState.GAME;					
				}
			}
			for (int i = 0; i < 12; i++) {
				if (buttons[i].mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
					click.play();
					if (levels[i] != null) {

						levelToLoad = levels[i];
						selected = i;
					}
				}
			}

		}
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImage(levelSelect, 0, 0);

		for (int i = 0; i < 12; i++) {
			try {
				if(selected == i) {
					buttons[i].setColor(0xffffffff);
					buttons[i].drawThickOutline(r);
					buttons[i].setColor(0xff000000);
				}
				r.drawImage(levels[i].getPreview(), (int) buttons[i].getX(), (int) buttons[i].getY());
				buttons[i].drawOutline(r);
			} catch (Exception e) {
				buttons[i].drawButton(r);
			}
		}
	}

	public static GameContainer getGc() {
		return gc;
	}

}