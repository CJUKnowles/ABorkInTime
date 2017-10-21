package com.turruc.game.entities;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.game.GameManager;

public class LOSBullet extends GameObject {

	private float speed = 400; // 400
	private int size = 8; // width/height of bullet
	double xVelocity;
	double yVelocity;
	public boolean LOS = true;
	private float lastClearPosX = 0;
	private float lastClearPosY = 0;


	public LOSBullet(int targetX, int targetY, int tileX, int tileY, float offX, float offY, GameContainer gc, float dt, int range, boolean kill, boolean ignoreWalls) {
		this.tag = EntityType.LOSBullet;
		posX = tileX * GameManager.TS + offX;
		posY = tileY * GameManager.TS + offY;

		double angle = Math.atan2(targetX - posX, targetY - posY);
		this.xVelocity = speed * Math.cos(angle);
		this.yVelocity = speed * Math.sin(angle);

		while (true) {
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

			if(ignoreWalls) {
				if (range == 0) {
					LOS = false;
					return;
				}
			} else {
			
			if (GameManager.gm.getCollisionNum(tileX, tileY) == 1 || range == 0) {
				if(kill) {
					this.dead = true;					
				}
				LOS = false;
				return;
			}
			}
			if (GameManager.gm.getCollisionNum(tileX, tileY) == 0 && !kill) {
				System.out.println("I am the biggest meme");
				this.lastClearPosX = this.posX;
				this.lastClearPosY = this.posY;

			}
			
			// if(posX == targetX && posY == targetY) {
			if (Math.abs(posX - targetX) <= 10 && Math.abs(posY - targetY) <= 10) {
				if(kill) {
					this.dead = true;					
				}
				LOS = true;
				return;
			}

			posX = tileX * GameManager.TS + offX;
			posY = tileY * GameManager.TS + offY;
			range--;
		}
	}

	@Override
	public void update(GameContainer gc, float dt) {
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		// r.drawFillRect((int) posX, (int) posY, size, size, 0xffff0000);
	}

	public float getLastClearPosX() {
		return lastClearPosX;
	}

	public float getLastClearPosY() {
		return lastClearPosY;
	}

	public void dispose() {
		
	}

}
