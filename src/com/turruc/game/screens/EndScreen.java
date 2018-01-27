package com.turruc.game.screens;

import com.turruc.engine.AbstractGame;
import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.Button;
import com.turruc.engine.gfx.Image;
import com.turruc.game.GameManager;
import com.turruc.game.GameState;

public class EndScreen extends AbstractGame {
	private static GameContainer gc;

	Button okay;
	private SoundClip click;
	public static Process proc;

	private Image winScreen = new Image("/winScreen.png");
	private Image loseScreen = new Image("/loseScreen.png");

	public static boolean win = false;

	public EndScreen() {
		okay = new Button(287, 194, 240, 95, 0xffffffff, "Play");
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
			if (okay.mouseIsOver(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
				click.play();
				gc.gameState = GameState.MENU;
				GameManager.startTime = System.currentTimeMillis();
				GameManager.goalTime = GameManager.startTime + GameManager.levelTime;
			}

		}

	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		if (win) {
			r.drawImage(winScreen, 0, 0);
		} else {
			r.drawImage(loseScreen, 0, 0);
		}
	}

	public static GameContainer getGc() {
		return gc;
	}

}