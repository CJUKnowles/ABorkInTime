package com.turruc.game.entities;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.Image;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.game.GameManager;

public class FloatEnemy extends GameObject {
	private ImageTile floatEnemy = new ImageTile("/player.png", 32, 32);
	private Image floatHead = new Image("/floatHead.png");
	private int padding, paddingTop;

	private int direction = 1;
	private float anim = 0;
	private int tileX, tileY;
	private float offX, offY;

	private float normalSpeed = 100;
	private float slowSpeed = normalSpeed / slowMotion;
	private float speed = normalSpeed;

	private float normalFallSpeed = 25;
	private float slowFallSpeed = normalFallSpeed / slowMotion;
	private float fallSpeed = normalFallSpeed;
	private float jump = -7; // must be negative
	private boolean ground = false;
	private boolean groundLast = false;

	private int maxHealth = 100;
	private int health = maxHealth;

	private int normalAnimationSpeed = 10;
	private int slowAnimationSpeed = normalAnimationSpeed / slowMotion;
	private int animationSpeed = normalAnimationSpeed;

	private double lastTimeLavaDamage;
	private int lavaDamageCooldown = 1;
	private int lavaDamage = 10;

	private boolean againstWall = false;

	private double lastTimeDamage;
	private int damageCooldown = 1;
	private int damage = 25;

	private boolean attacking = false;
	private float attackAnim = 4;

	private SoundClip ugh;
	private SoundClip boof;

	private Player player;

	private int manaReward = 20;
	private int range = 60;

	private double angle = 0;
	private double angle2 = 0;

	double xVelocity;
	double yVelocity;

	public FloatEnemy(int posX, int posY) {
		this.tag = EntityType.floatEnemy;
		this.tileX = posX;
		this.tileY = posY;
		this.offX = 0;
		this.offY = 0;
		this.posX = posX * GameManager.TS;
		this.posY = posY * GameManager.TS;
		this.width = 32;
		this.height = 32;

		this.padding = 0;
		this.paddingTop = 0;

		player = GameManager.gm.getPlayer();

		if (ugh == null) ugh = new SoundClip("/audio/ugh.wav");
		if (boof == null) boof = new SoundClip("/audio/boof.wav");

	}

	@Override
	public void update(GameContainer gc, float dt) {
		if (health <= 0) {
			boof.play();
			setDead(true);
			health = maxHealth;
			this.posX = 64;
			this.posX = 64;
			this.offX = 0;
			this.offX = 0;
			this.tileX = 2;
			this.tileY = 2;
		}
		if (new LOSBullet((int) player.getPosX(), (int) player.getPosY(), tileX, tileY, offX, offY, GameManager.getGc(), GameManager.getGc().getDt(), range, true, true).LOS) {
			angle2 = Math.atan2(GameManager.gm.getPlayer().getPosY() - posY, GameManager.gm.getPlayer().getPosX() - posX);
			angle = Math.atan2(GameManager.gm.getPlayer().getPosX() - posX, GameManager.gm.getPlayer().getPosY() - posY);

			this.xVelocity = speed * Math.cos(angle);
			this.yVelocity = speed * Math.sin(angle);

			offX += yVelocity * dt; // --------------------------

			if (new LOSBullet((int) player.getPosX(), (int) player.getPosY(), tileX, tileY, offX, offY, GameManager.getGc(), GameManager.getGc().getDt(), range, true, true).LOS) {
				// slow motion
				if (GameManager.gm.getPlayer().isSlow()) {
					speed = slowSpeed;
					fallSpeed = slowFallSpeed;
					animationSpeed = slowAnimationSpeed;
				} else {
					speed = normalSpeed;
					fallSpeed = normalFallSpeed;
					animationSpeed = normalAnimationSpeed;
				}

				againstWall = true;

				// lava damage
				if (GameManager.gm.getCollisionNum((int) tileX, (int) tileY) == 3 && (System.nanoTime() / 1000000000.0) - lastTimeLavaDamage > lavaDamageCooldown) {
					lastTimeLavaDamage = System.nanoTime() / 1000000000.0;
					hit(lavaDamage);
				}
				// end lava damage

				// Lava Slow
				if (GameManager.gm.getCollisionNum((int) tileX, (int) tileY) == 3 && !GameManager.gm.getPlayer().isSlow()) {
					speed = slowSpeed * 3;
					fallSpeed = slowFallSpeed * 3;
					animationSpeed = slowAnimationSpeed * 3;
				} else if (GameManager.gm.getCollisionNum((int) tileX, (int) tileY) == 3 && GameManager.gm.getPlayer().isSlow()) {
					speed = (slowSpeed * 3) / slowMotion;
					fallSpeed = (slowFallSpeed * 3) / slowMotion;
					animationSpeed = (slowAnimationSpeed * 3) / slowMotion;
				} else if (!GameManager.gm.getPlayer().isSlow()) {
					speed = normalSpeed;
					fallSpeed = normalFallSpeed;
					animationSpeed = normalAnimationSpeed;
				}
				// end lava slow

				// health check
				if (health > maxHealth) {
					health = maxHealth;
				} else if (health < 0) {
					health = 0;
				}
				// end of health check

				if (attacking) {
					attackAnim += dt * animationSpeed;
					if (attackAnim > 7) {
						attacking = false;
						attackAnim = 4;
					}
				}

				// Move Towards player

				// End Move Towards Player
				// Beginning Left and right
				if (GameManager.gm.getPlayer().getPosX() > this.posX && Math.abs(GameManager.gm.getPlayer().getPosX() - this.posX) > GameManager.TS / 2) {
					if (GameManager.gm.getCollision(tileX + 1, tileY) || GameManager.gm.getCollision(tileX + 1, tileY + (int) Math.signum((int) offY))) {
						offX += dt * speed;
						if (offX > padding) {
							tileX += offX / GameManager.TS;
							offX = padding;
						}
					} else {
						direction = 0;
						againstWall = false;
					}
				}

				if (GameManager.gm.getPlayer().getPosX() < this.posX && Math.abs(GameManager.gm.getPlayer().getPosX() - this.posX) > GameManager.TS / 2) {
					if (GameManager.gm.getCollision(tileX - 1, tileY) || GameManager.gm.getCollision(tileX - 1, tileY + (int) Math.signum((int) offY))) {
						offX -= dt * speed;
						if (offX < -padding) {
							tileX += offX / GameManager.TS + 1;
							offX = -padding;
						}

					} else {
						direction = 1;
						againstWall = false;
					}
				}
				if (Math.abs(GameManager.gm.getPlayer().getPosX() - this.posX) < GameManager.TS / 2) {
					againstWall = false;
				}
				// End left and right
				offY += xVelocity * dt;
				// Beginning Jump and Gravity

				if ((GameManager.gm.getCollision(tileX, tileY - 1) || GameManager.gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY - 1)) && offY < -paddingTop) {
					offY = -paddingTop;
				}

				if ((GameManager.gm.getCollision(tileX, tileY + 1) || GameManager.gm.getCollisionNum(tileX, tileY + 1) == 4 || GameManager.gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY + 1)) && offY > 0) {
					offY = 0;
					ground = true;
				}

				// Falling through platforms

				// End of falling through platforms
				// End Jump and Gravity

				// Final Position
				if (offY > GameManager.TS / 2) {
					tileY++;
					offY -= GameManager.TS;
				}

				if (offY < -GameManager.TS / 2) {
					tileY--;
					offY += GameManager.TS;
				}

				if (offX > GameManager.TS / 2) {
					tileX++;
					offX -= GameManager.TS;
				}

				if (offX < -GameManager.TS / 2) {
					tileX--;
					offX += GameManager.TS;
				}

				posX = tileX * GameManager.TS + offX;
				posY = tileY * GameManager.TS + offY;
				// end of final position

				// Animation
				if (direction == 0) {
					direction = 0;
					anim += dt * animationSpeed;
					if (anim > 4) {
						anim = 0;
					}

				} else if (direction == 1) {
					direction = 1;
					anim += dt * animationSpeed;
					if (anim > 4) {
						anim = 0;
					}
				} else {
					anim = 0;
				}

				if (!ground) {
					anim = 1;
				}

				if (ground && !groundLast) {
					anim = 2;
				}

				// End of Animation

				groundLast = ground;
				// attacking
				if (checkContact(this.posX, this.posY, GameManager.gm.getPlayer().getPosX(), GameManager.gm.getPlayer().getPosY()) && (System.nanoTime() / 1000000000.0) - lastTimeDamage > damageCooldown) {
					GameManager.gm.getPlayer().hit(damage);
					lastTimeDamage = System.nanoTime() / 1000000000.0;
					attacking = true;
				}
			}

		}
		// End of attacking
	}

	public void hit(int damage) {
		health -= damage;
		if (health < 0) {
			health = 0;
		}
		ugh.play();
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		if (attacking) {
			// r.drawImageTile(floatEnemy, (int) posX, (int) posY, (int) attackAnim,
			// direction);
		} else {
			// r.drawImageTile(floatEnemy, (int) posX, (int) posY, (int) anim, direction);
		}

		r.drawImage(r.transformImage(floatHead.getBufferedImage(), (int) Math.toDegrees(-angle)), (int) posX, (int) posY);

		// health
		r.drawFillRect((int) posX, (int) posY - 9, this.width, 5, 0xbb000000);
		r.drawFillRect((int) posX + 1, (int) posY + 1 - 9, (int) ((float) (this.width - 2) * ((float) health / (float) maxHealth)), 3, 0xbbff0000);
		// end of health
	}

	public void moveTo(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
		this.tileX = (int) (posX / GameManager.TS);
		this.tileY = (int) (posY / GameManager.TS);
		this.offX = (int) (posX % GameManager.TS);
		this.offY = (int) (posY % GameManager.TS);
	}

	public void setDead(boolean dead) {
		this.dead = dead;
		player.setMana(player.getMana() + manaReward);
		ugh.play();
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public float getTileX() {
		return tileX;
	}

	public float getTileY() {
		return tileY;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		floatEnemy.dispose();
		floatEnemy = null;
		ugh.close();
		ugh = null;
		boof.close();
		boof = null;
	}

}
