package com.turruc.game.entities;

import java.util.Random;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.Image;
import com.turruc.game.GameManager;

public class Turret extends GameObject {
	private Image turret = new Image("/turret.png");
	private Image turretHead = new Image("/turretHead.png");

	private float targetX;
	private float targetY;

	private int tileX, tileY;
	private float offX, offY;
	private int normalFireRate = 70;
	private int slowFireRate = normalFireRate * slowMotion;
	private int fireRate = 70; // Smaller = faster firerate //70
	private int timeUntilNextShot = fireRate;
//	private GameManager gm;
	private double angle = 0;
	private double angle2;
	private double targetAngle;
	private int range = 60; // 5 per tile roughly
	private double normalTurnSpeed = 5;
	private double slowTurnSpeed = normalTurnSpeed / slowMotion;
	private double turnSpeed = normalTurnSpeed;

	private boolean canShoot = false;
	private int accuracy = 10; //the closer to zero, the more on target the turret has to be before it fires
	
	private Player player;

	private int manaReward = 20;
	
	private SoundClip pew;
	private SoundClip boof;
	
	public Turret(int posX, int posY) {
		Random random = new Random();
		normalFireRate += random.nextInt(20) - 10;
		this.tag = EntityType.turret;
		this.tileX = posX;
		this.tileY = posY;
		this.offX = 0;
		this.offY = 0;
		this.posX = posX * GameManager.TS;
		this.posY = posY * GameManager.TS;
		this.width = 32;
		this.height = 32;
		this.player = GameManager.gm.getPlayer();
		
		pew = new SoundClip("/audio/pew.wav");
		boof = new SoundClip("/audio/boof.wav");
	}

	@Override
	public void update(GameContainer gc, float dt) {

		if (GameManager.gm.getPlayer().isSlow()) {
			fireRate = slowFireRate;
			turnSpeed = slowTurnSpeed;
		} else {
			fireRate = normalFireRate;
			turnSpeed = normalTurnSpeed;
		}

		targetX = GameManager.gm.getPlayer().getPosX();
		targetY = GameManager.gm.getPlayer().getPosY();

		if (GameManager.gm.getCollisionNum(tileX, tileY + 1) == 1) { // below
			angle2 = 0;
		} else if (GameManager.gm.getCollisionNum(tileX + 1, tileY) == 1) { // right
			angle2 = 270;
		} else if (GameManager.gm.getCollisionNum(tileX - 1, tileY) == 1) { // left
			angle2 = 90;
		} else if (GameManager.gm.getCollisionNum(tileX, tileY - 1) == 1) { // above
			angle2 = 180;
		}

		// update target position
		if (checkLOS(this.posX, this.posY, targetX, targetY)) {
			targetAngle = Math.toDegrees(Math.atan2(targetY - posY, targetX - posX));
		}
		// end update target position

		// Turn head towards player
		angle %= (Math.PI * 2);
		double distanceLeft = Math.abs(targetAngle - Math.toDegrees(angle));
		double distanceRight = Math.abs(targetAngle - Math.toDegrees(angle + .01));

		if (distanceLeft >= 180) {
			distanceLeft = 180 - (distanceLeft % 180);
		}

		if (distanceRight >= 180) {
			distanceRight = 180 - (distanceRight % 180);
		}

		canShoot = false;
		if (distanceLeft <= distanceRight && distanceLeft > turnSpeed) {
			angle -= dt * turnSpeed;
		} else if (distanceLeft > distanceRight && distanceRight > turnSpeed) {
			angle += dt * turnSpeed;
		}
		
		if(Math.abs(Math.toDegrees(angle) - targetAngle) < accuracy) {
			canShoot = true;
		}

		if (timeUntilNextShot != 0 & checkLOS(this.posX, this.posY, targetX, targetY)) {
			timeUntilNextShot--;
		}

		if (timeUntilNextShot == 0 && canShoot) {
			if (checkLOS(this.posX, this.posY, targetX, targetY)) {
				timeUntilNextShot = fireRate;
				GameManager.gm.addObject(new EnemyBullet((int) (targetX), (int) (targetY), tileX, tileY, offX, offY));
				pew.play();
			}
		}
	}

	public boolean checkLOS(float x0, float y0, float x1, float y1) {
		LOSBullet check = new LOSBullet((int) targetX, (int) targetY, tileX, tileY, offX, offY, GameManager.getGc(), GameManager.getGc().getDt(), range, true, false);
		return check.LOS;
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImage(r.transformImage(turret.getBufferedImage(), (int) angle2), (int) posX, (int) posY);
		r.drawImage(r.transformImage(turretHead.getBufferedImage(), (int) Math.toDegrees(angle)), (int) posX, (int) posY);
	}

	public void setDead(boolean dead) {
		GameManager.gm.getCollision()[(((int) this.tileY) * GameManager.gm.getLevelW()) + (int) this.tileX] = 0;
		this.dead = dead;
		player.setMana(player.getMana() + manaReward);
		boof.play();
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
		turret.dispose();
		turret = null;
		turretHead.dispose();
		turretHead = null;
	}
	
}
