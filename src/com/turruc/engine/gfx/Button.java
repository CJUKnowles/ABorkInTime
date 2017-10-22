package com.turruc.engine.gfx;

import java.awt.Rectangle;

import com.turruc.engine.Renderer;

@SuppressWarnings("serial")
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
		r.drawFillRect((int)this.getX(), (int)this.getY(), (int)this.getWidth(), (int)this.getHeight(), color);
		r.drawText(text, this.x + (this.width/2) - r.getTextLength(text)/2, this.y + this.height/2 - 5, 0xffffffff);
	}
	
	public void drawOutline(Renderer r) {
		r.drawRect((int)this.getX(), (int)this.getY(), (int)this.getWidth(), (int)this.getHeight(), color);
	}
	
	public void drawThickOutline(Renderer r) {
		r.drawRect((int)this.getX() -1, (int)this.getY()-1, (int)this.getWidth() + 2, (int)this.getHeight() + 2, color);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	
	
}
