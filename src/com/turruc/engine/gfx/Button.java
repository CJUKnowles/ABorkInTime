package com.turruc.engine.gfx;

import java.awt.Rectangle;

import com.turruc.engine.Renderer;

public class Button extends Rectangle {
	
	private String text;
	private int color;
	
	public Button(int x, int y, int width, int height, int color, String text) {
		super(x, y, width, height);
		this.text = text;
		this.color = color;
	}
	
	public boolean mouseIsOver(int mouseX, int mouseY) {
		return(mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
	}
	
	public void drawButton(Renderer r) {
		r.drawFillRect((int)this.getX(), (int)this.getY(), (int)this.getWidth(), (int)this.getHeight(), 0xff040404);
		r.drawText(text, this.x + (this.width/2) - r.getTextLength(text)/2, this.y, color);
	}
	
	
}
