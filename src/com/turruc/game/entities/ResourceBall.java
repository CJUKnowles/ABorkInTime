package com.turruc.game.entities;

import com.turruc.engine.GameContainer;
import com.turruc.engine.Renderer;
import com.turruc.engine.audio.SoundClip;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.game.GameManager;

public class ResourceBall extends GameObject {

	private int type; // 0 health, 1 mana
	private int amount;
	private Player player;
	private float anim = 0;
	private int normalAnimationSpeed = 10;
	private int slowAnimationSpeed = normalAnimationSpeed / slowMotion;
	private int animationSpeed = normalAnimationSpeed;
	private ImageTile image = new ImageTile("/resourceBall.png", 32, 32);

	private int tileX;
	private int tileY;
	private SoundClip woosh;

	public ResourceBall(GameManager gm, int tileX, int tileY, int type, int amount) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.posX = tileX * GameManager.TS;
		this.posY = tileY * GameManager.TS;
		this.type = type;
		this.amount = amount;
		this.player = gm.getPlayer();

		this.tag = EntityType.resourceBall;
		if(woosh == null)
		woosh = new SoundClip("/audio/woosh.wav");
	}

	@Override
	public void update(GameContainer gc, float dt) {
		
		
		if (GameManager.gm.getPlayer().isSlow()) {
			animationSpeed = slowAnimationSpeed;
		} else {
			animationSpeed = normalAnimationSpeed;
		}
		
		anim += dt * animationSpeed;
		if (anim > 4) {
			anim = 0;
		}
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImageTile(image, (int) tileX * GameManager.TS, (int) tileY * GameManager.TS, (int) anim, type);
	}

	public void setDead(boolean dead) {
		this.dead = dead;
		if (type == 0) {
			player.setHealth(player.getHealth() + amount);
		} else if (type == 1) {
			player.setMana(player.getMana() + amount);
		}
		woosh.play();
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
		image.dispose();
		image = null;
		woosh.close();
		woosh = null;
	}
}
