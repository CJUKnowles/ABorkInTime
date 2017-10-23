package com.turruc.game.entities;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.game.GameManager;

public abstract class GameObject {
	protected EntityType tag;
	protected float posX, posY;
	protected int width, height;
	protected boolean dead = false;
	public static int slowMotion = 8;
	
	public abstract void update(GameContainer gc, float dt);

	public abstract void render(GameContainer gc, Renderer r);

	public boolean checkContact(float posX, float posY, float posX2, float posY2) {
		if (Math.abs((posX + GameManager.TS / 2) - (posX2 + GameManager.TS / 2)) < GameManager.TS && Math.abs((posY + GameManager.TS / 2) - (posY2 + GameManager.TS / 2)) < GameManager.TS) {
			return true;
		}
		return false;
	}
	
	public boolean checkContactLarge(float posX, float posY, float posX2, float posY2) {
		if (Math.abs((posX + GameManager.TS / 2) - (posX2 + GameManager.TS)) < GameManager.TS && Math.abs((posY + GameManager.TS / 2) - (posY2 + GameManager.TS)) < GameManager.TS * 2) {
			return true;
		}
		return false;
	}
	
	public EntityType getTag() {
		return tag;
	}

	public void setTag(EntityType tag) {
		this.tag = tag;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public abstract void dispose();
}
