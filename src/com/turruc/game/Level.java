package com.turruc.game;

import java.awt.Color;
import java.util.ArrayList;

import com.turruc.engine.gfx.Image;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.game.entities.FloatEnemy;
import com.turruc.game.entities.GameObject;
import com.turruc.game.entities.LargeEnemy;
import com.turruc.game.entities.MeleeEnemy;
import com.turruc.game.entities.Player;
import com.turruc.game.entities.ResourceBall;
import com.turruc.game.entities.Turret;

public class Level {
	Image levelImage, background, midground;
	Image preview;
	ImageTile tileset;

	public Level(Image levelImage, Image background, Image midground, ImageTile tileset) {
		this.levelImage = levelImage;
		this.background = background;
		this.midground = midground;
		this.tileset = tileset;
	}

	public Level(Image levelImage, Image background, Image midground, ImageTile tileset, Image preview) {
		this.levelImage = levelImage;
		this.background = background;
		this.midground = midground;
		this.tileset = tileset;
		this.preview = preview;
	}

	public void updateLevel(GameManager gm) {

		GameManager.gm.levelW = levelImage.getW();
		GameManager.gm.levelH = levelImage.getH();
		GameManager.gm.collision = new int[GameManager.gm.levelW * GameManager.gm.levelH];

		for (int y = 0; y < levelImage.getH(); y++) {
			for (int x = 0; x < levelImage.getW(); x++) {

				if (y == 0 || y == levelImage.getH() - 1 || x == 0 || x == levelImage.getW() - 1) {
					GameManager.gm.collision[x + y * levelImage.getW()] = 1;
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xffff00ff) {
					GameManager.gm.collision[x + y * levelImage.getW()] = -100;// player
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.BLACK.getRGB()) {// black
					GameManager.gm.collision[x + y * levelImage.getW()] = 1; // collision block
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.WHITE.getRGB()) {// white
					GameManager.gm.collision[x + y * levelImage.getW()] = 0;// air
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.GREEN.getRGB()) {// green
					GameManager.gm.collision[x + y * levelImage.getW()] = 2;// turret
				} else if ((levelImage.getP()[x + y * levelImage.getW()] | 0xff000000) == Color.RED.getRGB()) {// red //
					// |
					// 0xff000000
					// removes
					// alpha
					GameManager.gm.collision[x + y * levelImage.getW()] = -1;// health ball
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.BLUE.getRGB()) {// blue
					GameManager.gm.collision[x + y * levelImage.getW()] = -2;// mana ball
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.YELLOW.getRGB()) {// yellow
					GameManager.gm.collision[x + y * levelImage.getW()] = 3;// lava
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff963200) {// Brown
					GameManager.gm.collision[x + y * levelImage.getW()] = 4;// platform
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff6400ff) {// Purple
					GameManager.gm.collision[x + y * levelImage.getW()] = 5;// ladder
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff00ffff) {// teal
					 GameManager.getObjects().add(new MeleeEnemy(x, y)); //meleeEnemy
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff6464ff) {// Purplish blue
					 GameManager.getObjects().add(new FloatEnemy(x, y)); //floatEnemy
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff646400) {// teal
					 GameManager.getObjects().add(new LargeEnemy(x, y)); //largeEnemy
				}

			}
		}
	}

	public void loadLevel() {
		GameManager.gm.levelW = levelImage.getW();
		GameManager.gm.levelH = levelImage.getH();
		GameManager.gm.collision = new int[GameManager.gm.levelW * GameManager.gm.levelH];

		// Clearing the old objects array
		ArrayList<GameObject> toBeRemoved = new ArrayList<GameObject>();
		for (GameObject obj : GameManager.getObjects()) {
			if (obj instanceof Player) {
				continue;
			}
			obj.dispose();
			toBeRemoved.add(obj);
		}

		for (GameObject obj : toBeRemoved) {
			GameManager.getObjects().remove(obj);
		}
		toBeRemoved.clear();
		// End clearing the old objects array

		for (int y = 0; y < levelImage.getH(); y++) {
			for (int x = 0; x < levelImage.getW(); x++) {

				if (y == 0 || y == levelImage.getH() - 1 || x == 0 || x == levelImage.getW() - 1) {
					GameManager.gm.collision[x + y * levelImage.getW()] = 1;
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xffff00ff) {
					GameManager.gm.collision[x + y * levelImage.getW()] = -100;// player
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.BLACK.getRGB()) {// black
					GameManager.gm.collision[x + y * levelImage.getW()] = 1; // collision block
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.WHITE.getRGB()) {// white
					GameManager.gm.collision[x + y * levelImage.getW()] = 0;// air
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.GREEN.getRGB()) {// green
					GameManager.gm.collision[x + y * levelImage.getW()] = 2;// turret
				} else if ((levelImage.getP()[x + y * levelImage.getW()] | 0xff000000) == Color.RED.getRGB()) {// red | 0xff000000 removes alpha
					GameManager.gm.collision[x + y * levelImage.getW()] = -1;// health ball
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.BLUE.getRGB()) {// blue
					GameManager.gm.collision[x + y * levelImage.getW()] = -2;// mana ball
				} else if (levelImage.getP()[x + y * levelImage.getW()] == Color.YELLOW.getRGB()) {// yellow
					GameManager.gm.collision[x + y * levelImage.getW()] = 3;// lava
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff963200) {// Brown
					GameManager.gm.collision[x + y * levelImage.getW()] = 4;// platform
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff6400ff) {// Purple
					GameManager.gm.collision[x + y * levelImage.getW()] = 5;// ladder
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff00ffff) {// teal
					GameManager.getObjects().add(new MeleeEnemy(x, y)); //meleeEnemy
				} else if (levelImage.getP()[x + y * levelImage.getW()] == 0xff6464ff) {// teal
					GameManager.getObjects().add(new FloatEnemy(x, y)); //floatEnemy
				} else if  (levelImage.getP()[x + y * levelImage.getW()] == 0xff646400) {
					GameManager.getObjects().add(new LargeEnemy(x, y - 1)); //largeEnemy//-1 to offset the spawn position so he doesn't get stuck in the floor
					System.out.println("meme");
				}
			}
		}

		for (int y = 0; y < GameManager.gm.levelH; y++) {
			for (int x = 0; x < GameManager.gm.levelW; x++) {
				if (GameManager.gm.collision[x + y * GameManager.gm.levelW] == -100) { // player
					GameManager.gm.getPlayer().moveTo(x * GameManager.TS, y * GameManager.TS);
					GameManager.gm.getCamera().setOffX(GameManager.gm.getPlayer().getPosX());
					GameManager.gm.getCamera().setOffY(GameManager.gm.getPlayer().getPosY());
				}

				if (GameManager.gm.collision[x + y * GameManager.gm.levelW] == 2) { // turret
					GameManager.getObjects().add(new Turret(x, y));
				}

				if (GameManager.gm.collision[x + y * GameManager.gm.levelW] == -1) { // healthBall
					GameManager.getObjects().add(new ResourceBall(GameManager.gm, x, y, 0, 101 + (levelImage.getP()[x + y * GameManager.gm.levelW] >> 24)));
				}

				if (GameManager.gm.collision[x + y * GameManager.gm.levelW] == -2) { // manaBall
					GameManager.getObjects().add(new ResourceBall(GameManager.gm, x, y, 1, 101 + (levelImage.getP()[x + y * GameManager.gm.levelW] >> 24)));
				}

			}
		}
	}

	public Image getLevelImage() {
		return levelImage;
	}

	public void setLevelImage(Image levelImage) {
		this.levelImage = levelImage;
	}

	public Image getBackground() {
		return background;
	}

	public void setBackground(Image background) {
		this.background = background;
	}

	public Image getMidground() {
		return midground;
	}

	public void setMidground(Image midground) {
		this.midground = midground;
	}

	public Image getPreview() {
		return preview;
	}

	public void setPreview(Image preview) {
		this.preview = preview;
	}

	public ImageTile getTileset() {
		return tileset;
	}

	public void setTileset(ImageTile tileset) {
		this.tileset = tileset;
	}
}
