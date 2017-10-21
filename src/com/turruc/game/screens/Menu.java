package com.turruc.game.screens;

import javax.sound.sampled.Clip;

import com.turruc.engine.AbstractGame;
import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.Button;
import com.turruc.engine.gfx.Image;
import com.turruc.game.GameState;

public class Menu extends AbstractGame {
	private static GameContainer gc;

	Button play;
	Button quit;
	Button levelSelect;
	Button levelEditor;
	private SoundClip click;

	private Image menu = new Image("/menu.png");

	public Menu() {
		play = new Button(90, 140, 240, 95, 0xffffffff, "Play");
		quit = new Button(90, 265, 240, 95, 0xffffffff, "Quit");
		levelSelect = new Button(480, 265, 240, 95, 0xffffffff, "level select");
		levelEditor = new Button(480, 140, 240, 95, 0xffffffff, "level editor");
		click = new SoundClip("/audio/click.wav");
		click.setVolume(6);
	}

	@Override
	public void init(GameContainer gc) {
		gc.getRenderer().setAmbientColor(-1);
	}

	@Override
	public void update(GameContainer gc, float dt) {
		if (gc.getInput().isButtonDown(1)) {
			if (play.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();
				gc.gameState = GameState.GAME;
			} else if (quit.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();

				System.exit(0);
			} else if (levelSelect.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();

				gc.gameState = GameState.LEVELS;
			} else if (levelEditor.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();

				// TODO: Implement levelEditor
			}
		}

	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImage(menu, 0, 0);
	}

	public static GameContainer getGc() {
		return gc;
	}

}