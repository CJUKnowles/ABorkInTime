package com.turruc.game.entities;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.gfx.Image;
import com.turruc.game.GameManager;

public class EnemyBullet extends GameObject {
	private Image bullet = new Image("/bullet.png");

	private int tileX, tileY;
	private float offX, offY;

	private float normalSpeed = 400; // 400
	private float slowSpeed = normalSpeed / (float) slowMotion;
	private float speed = 400;
	private int size = 8; // width/height of bullet
	double xVelocity;
	double yVelocity;

	private double angle2 = 0;
	private double angle = 0;

	public EnemyBullet(int targetX, int targetY, int tileX, int tileY, float offX, float offY) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.offX = offX + GameManager.TS / 2 - size / 2;
		this.offY = offY + GameManager.TS / 2 - size / 2;
		this.tag = EntityType.enemyBullet;
		posX = tileX * GameManager.TS + offX;
		posY = tileY * GameManager.TS + offY;

		angle2 = Math.atan2(targetY - posY, targetX - posX);
		angle = Math.atan2(targetX - posX, targetY - posY);

	}

	@Override
	public void update(GameContainer gc, float dt) {
		if (GameManager.gm.getPlayer().isSlow()) {

			speed = slowSpeed;
		} else {
			speed = normalSpeed;
		}

		
		
		this.xVelocity = speed * Math.cos(angle);
		this.yVelocity = speed * Math.sin(angle);

		offY += xVelocity * dt;
		offX += yVelocity * dt;

		// Final Position
		if (offY > GameManager.TS / (GameManager.TS / size)) {
			tileY++;
			offY -= GameManager.TS;
		}

		if (offY < -GameManager.TS / (GameManager.TS / size)) {
			tileY--;
			offY += GameManager.TS;
		}

		if (offX > GameManager.TS / (GameManager.TS / size)) {
			tileX++;
			offX -= GameManager.TS;
		}

		if (offX < -GameManager.TS / (GameManager.TS / size)) {
			tileX--;
			offX += GameManager.TS;
		}

		if (GameManager.gm.getCollisionNum(tileX, tileY) == 1) {
			this.dead = true;
		}

		for (int i = 0; i < GameManager.getObjects().size(); i++) {
			if (GameManager.getObjects().get(i).getTag().equals(EntityType.player)) {
				if (Math.abs(posX - GameManager.getObjects().get(i).getPosX() - 16) <= 16 && Math.abs(posY - GameManager.getObjects().get(i).getPosY() - 16) <= 16) {
					this.dead = true;
					((Player) GameManager.getObjects().get(i)).hit(20);
					break;
				}
			}
		}

		posX = tileX * GameManager.TS + offX;
		posY = tileY * GameManager.TS + offY;
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImage(r.transformImage(bullet.getBufferedImage(), (int) Math.toDegrees(angle2)), (int) posX, (int) posY);
	}

	@Override
	public void dispose() {
		bullet.dispose();
		bullet = null;
	}

}
