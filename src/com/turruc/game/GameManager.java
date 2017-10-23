package com.turruc.game;

import java.awt.Color;
import java.util.ArrayList;

import com.sun.glass.events.KeyEvent;
import com.turruc.engine.AbstractGame;
import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.gfx.Image;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.game.entities.EntityType;
import com.turruc.game.entities.GameObject;
import com.turruc.game.entities.Player;

public class GameManager extends AbstractGame {
	private static GameContainer gc;

	public static GameManager gm;

	public static final int TS = 32; // tilesize for hitboxes

	private static ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private Camera camera;

	public int[] collision;
	public int levelW, levelH;
	private Image platform;
	private Image ladder;
	private ImageTile lava;
	private Player player;

	public Level level;

	private int normalAnimationSpeed = 7;
	private int slowAnimationSpeed = normalAnimationSpeed / GameObject.slowMotion;
	private int animationSpeed = normalAnimationSpeed;

	public static boolean IN_LEVEL_EDITOR = false;
	private float anim = 0;

	public GameManager() {
		if (gm == null) {
			gm = this;
		} else {
			throw new IllegalStateException("Tried to create a new instance of GameManager");
		}
		player = new Player(8, 8);
		getObjects().add(player);
		level = new Level(new Image("/levels/levelExample/levelExample.png"), new Image("/levels/levelExample/backgroundExample.png"), new Image("/levels/levelExample/midgroundExample.png"), new ImageTile("/levels/levelExample/tilesetExample.png", 32, 32));
		level.loadLevel();
		camera = new Camera(EntityType.player);
		platform = new Image("/platform.png");
		lava = new ImageTile("/lava.png", 32, 32);
		ladder = new Image("/ladder.png");
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			IN_LEVEL_EDITOR = args[0].equalsIgnoreCase("true");
		} else {
			IN_LEVEL_EDITOR = false;
		}

		
		gc = new GameContainer(new GameManager());
		if(IN_LEVEL_EDITOR) gc.gameState = GameState.GAME;
		gc.start();
	}

	@Override
	public void init(GameContainer gc) {
		gc.getRenderer().setAmbientColor(-1);
	}

	@Override
	public void update(GameContainer gc, float dt) {

		if (IN_LEVEL_EDITOR && gc.getInput().isKeyDown(KeyEvent.VK_F5)) {
			level.updateLevel(this);
		}
		
		for (int i = 0; i < getObjects().size(); i++) {
			getObjects().get(i).update(gc, dt);
			if (getObjects().get(i).isDead()) {
				getObjects().remove(i);
				i--;
			}
		}
		camera.update(gc, this, dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		camera.render(r);

		if (getPlayer().isSlow()) {
			animationSpeed = slowAnimationSpeed;
		} else {
			animationSpeed = normalAnimationSpeed;
		}

		anim += gc.getDt() * animationSpeed;
		anim %= 4;

		// Start of drawing map
		for (int i = 0; i < (levelW * 32) / level.getBackground().getW() * 2; i++) { // add 1 to i < x if drawing one
																						// too few backgrounds
			r.drawImage(level.getBackground(), (int) (i * level.getBackground().getW() + camera.getOffX() * camera.getBackgroundSpeed()), 0);
		}

		for (int i = 0; i < (levelW * 32) / level.getBackground().getW() * 2; i++) { // add 1 to i < x if drawing one
																						// too few backgrounds
			r.drawImage(level.getMidground(), (int) (i * level.getMidground().getW() + camera.getOffX() * camera.getMidgroundSpeed()), 0);
		}

		// r.drawFillRect(0, 0, level.getH() * 32, level.getW() * 32, 0xff00ffff);

		for (int y = 0; y < levelH; y++) {
			for (int x = 0; x < levelW; x++) {
				// drawing normal tileset (dirt, cave, etc)
				if (collision[x + y * levelW] == 1) {
					if (y != 0 && y != levelH - 1 && x != 0 && x != levelW) {
						if (!getContact(x, y - 1) && getContact(x - 1, y) && getCollision(x, y + 1) && getContact(x + 1, y)) {
							// dirt.getTileImage(0, 0).setLightBlock(Light.FULL);
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 0, 0); // up
						} else if (getContact(x, y - 1) && !getContact(x - 1, y) && getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 1, 0); // left
						} else if (getContact(x, y - 1) && getContact(x - 1, y) && !getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 2, 0); // down
						} else if (getContact(x, y - 1) && getContact(x - 1, y) && getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 0); // right
						} else if (!getContact(x, y - 1) && !getContact(x - 1, y) && getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 0, 1); // up, left
						} else if (!getContact(x, y - 1) && getContact(x - 1, y) && getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 1, 1); // up, right
						} else if (getContact(x, y - 1) && getContact(x - 1, y) && !getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 2, 1); // down, right
						} else if (getContact(x, y - 1) && !getContact(x - 1, y) && !getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 1); // down, left
						} else if (!getContact(x, y - 1) && getContact(x - 1, y) && !getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 0, 2); // up, down
						} else if (getContact(x, y - 1) && !getContact(x - 1, y) && getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 1, 2); // left, right
						} else if (!getContact(x, y - 1) && !getContact(x - 1, y) && getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 2, 2); // up, left, right
						} else if (!getContact(x, y - 1) && getContact(x - 1, y) && !getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 2); // up, down, right
						} else if (getContact(x, y - 1) && !getContact(x - 1, y) && !getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 0, 3); // left, right, down
						} else if (!getContact(x, y - 1) && !getContact(x - 1, y) && !getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 1, 3); // up, left, down
						} else if (!getContact(x, y - 1) && !getContact(x - 1, y) && !getCollision(x, y + 1) && !getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 2, 3); // up, left, down, right
						} else if (getContact(x, y - 1) && getContact(x - 1, y) && getCollision(x, y + 1) && getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 3); // none
						}

						// top row
					} else if (y == 0) {
						if (!getCollision(x, y + 1)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 2, 0); // down
						} else {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 3); // none
						}

						// left column
					} else if (x == 0) {
						if (!getContact(x + 1, y)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 0); // right
						} else {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 3); // none
						}
						// bottom row
					} else if (y == levelH - 1) {
						if (!getContact(x, y - 1)) {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 0, 0); // up
						} else {
							r.drawImageTile(level.getTileset(), x * TS, y * TS, 3, 3); // none
						}
					}
				}
				// end of drawing normal tileset

				// Drawing lava
				if (collision[x + y * levelW] == 3) {
					if (collision[x + (y - 1) * levelW] == 3) {
						r.drawImageTile(lava, x * TS, y * TS, (int) anim, 1); // none
					} else {
						r.drawImageTile(lava, x * TS, y * TS, (int) anim, 0); // up

					}

				}
				// end of drawing lava

				// Drawing platforms
				if (collision[x + y * levelW] == 4) {
					r.drawImage(platform, x * TS, y * TS);

				}
				// end of drawing platforms

				// Drawing ladders
				if (collision[x + y * levelW] == 5) {
					r.drawImage(ladder, x * TS, y * TS);

				}
				// end of drawing ladders

				// end of drawing map
			}
		}

		for (GameObject obj : getObjects()) {
			obj.render(gc, r);

		}

		r.drawText("Left Click: Shoot", (int) camera.getOffX(), 32, Color.WHITE.getRGB());
		r.drawText("Right Click: Melee", (int) camera.getOffX(), 48, Color.WHITE.getRGB());
		r.drawText("Shift: Teleport", (int) camera.getOffX(), 64, Color.WHITE.getRGB());
		r.drawText("Ctrl: Slow Motion", (int) camera.getOffX(), 80, Color.WHITE.getRGB());

	}

	public void addObject(GameObject object) {
		getObjects().add(object);
	}

	public GameObject getObject(EntityType tag) {
		for (int i = 0; i < getObjects().size(); i++) {
			if (getObjects().get(i).getTag().equals(tag)) {
				return getObjects().get(i);
			}
		}
		return null;
	}

	public boolean getContact(int x, int y) {
		return x < 0 || x >= levelW || y < 0 || y >= levelH || collision[x + y * levelW] == 1 || collision[x + y * levelW] == 2 || collision[x + y * levelW] == 3;
	}

	public boolean getCollision(int x, int y) {
		return x < 0 || x >= levelW || y < 0 || y >= levelH || collision[x + y * levelW] == 1 || collision[x + y * levelW] == 2;
	}

	public int getCollisionNum(int x, int y) {
		return collision[x + y * levelW];
	}

	public Camera getCamera() {
		return camera;
	}

	public int getLevelW() {
		return levelW;
	}

	public int getLevelH() {
		return levelH;
	}

	public Player getPlayer() {
		return player;
	}

	public static ArrayList<GameObject> getObjects() {
		return objects;
	}

	public int[] getCollision() {
		return collision;
	}

	public static GameContainer getGc() {
		return gc;
	}

}