package com.turruc.engine;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.turruc.engine.gfx.Font;
import com.turruc.engine.gfx.Image;
import com.turruc.engine.gfx.ImageRequest;
import com.turruc.engine.gfx.ImageTile;
import com.turruc.engine.gfx.Light;
import com.turruc.engine.gfx.LightRequest;

public class Renderer {

	private Font font = Font.STANDARD;
	private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();
	private ArrayList<LightRequest> lightRequest = new ArrayList<LightRequest>();

	private int pW, pH; // pixelWidth, pixelHeight
	private int[] p; // pixel
	private int[] zb; // z buffer
	private int[] lm; // light map
	private int[] lb; // light block

	private int ambientColor = 0xff232323;
	private int zDepth = 0;
	private boolean processing = false;
	private float camX, camY;

	public Renderer(GameContainer gc) {
		pW = gc.getWidth();
		pH = gc.getHeight();
		p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
		zb = new int[p.length];
		lm = new int[p.length];
		lb = new int[p.length];
	}

	public void clear() {
		for (int i = 0; i < p.length; i++) {
			p[i] = 0;
			zb[i] = 0;
			lm[i] = ambientColor;
			lb[i] = 0;
		}
	}

	public void process() {
		processing = true;

		Collections.sort(imageRequest, new Comparator<ImageRequest>() {
			@Override
			public int compare(ImageRequest i0, ImageRequest i1) {

				if (i0.zDepth < i1.zDepth) {
					return -1;
				}

				if (i0.zDepth > i1.zDepth) {
					return 1;
				}

				return 0;
			}

		});

		for (int i = 0; i < imageRequest.size(); i++) {
			ImageRequest ir = imageRequest.get(i);
			setzDepth(ir.zDepth);
			drawImage(ir.image, ir.offX, ir.offY);
		}

		// Draw lighting
		for (int i = 0; i < lightRequest.size(); i++) {
			LightRequest l = lightRequest.get(i);
			this.drawLightRequest(l.light, l.locX, l.locY);
		}

		for (int i = 0; i < p.length; i++) {
			float r = ((lm[i] >> 16) & 0xff) / 255f;
			float g = ((lm[i] >> 8) & 0xff) / 255f;
			float b = (lm[i] & 0xff) / 255f;

			p[i] = ((int) (((p[i] >> 16) & 0xff) * r) << 16 | (int) (((p[i] >> 8) & 0xff) * g) << 8 | (int) ((p[i] & 0xff) * b));
		}

		imageRequest.clear();
		lightRequest.clear();
		processing = false;
	}

	public void setPixel(int x, int y, int value) {
		int alpha = ((value >> 24) & 0xff);

		if (x < 0 || x >= pW || y < 0 || y >= pH || alpha == 0) {
			return;
		}

		int index = x + y * pW;

		if (zb[index] > zDepth) {
			return;
		}

		zb[index] = zDepth;

		if (alpha == 255) {
			p[index] = value;
		} else {
			int pixelColor = p[index];

			int newRed = ((pixelColor >> 16) & 0xff) - (int) ((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
			int newGreen = ((pixelColor >> 8) & 0xff) - (int) ((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
			int newBlue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));
			p[index] = newRed << 16 | newGreen << 8 | newBlue;
		}
	}

	public void setLightMap(int x, int y, int value) {
		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}

		if (zb[x + y * pW] > zDepth) {
			return;
		}

		int baseColor = lm[x + y * pW];

		int maxRed = Math.max((baseColor >> 16) & 0xff, (value >> 16) & 0xff);
		int maxGreen = Math.max((baseColor >> 8) & 0xff, (value >> 8) & 0xff);
		int maxBlue = Math.max(baseColor & 0xff, value & 0xff);
		lm[x + y * pW] = (maxRed << 16 | maxGreen << 8 | maxBlue);
	}

	public void setLightBlock(int x, int y, int value) {
		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}

		lb[x + y * pW] = value;
	}

	public void drawText(String text, int offX, int offY, int color) {
		offX -= camX;
		offY -= camY;
		int offset = 0;

		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i);

			for (int y = 0; y < font.getFontImage().getH(); y++) {
				for (int x = 0; x < font.getWidths()[unicode]; x++) {
					if (font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xffffffff) {
						setPixel(x + offX + offset, y + offY, color);
					}
				}
			}

			offset += font.getWidths()[unicode];
		}
	}

	public void drawImage(Image image, int offX, int offY) {
		offX -= camX;
		offY -= camY;
		if (image.isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
			return;
		}

		// Don't render code
		if (offX < -image.getW()) {
			return;
		}

		if (offY < -image.getH()) {
			return;
		}

		if (offX >= pW) {
			return;
		}

		int newX = 0;
		int newY = 0;
		int newWidth = image.getW();
		int newHeight = image.getH();

		if (offY >= pH) {
			return;
		}

		// clipping code
		if (offX < 0) {
			newX -= offX;
		}

		if (offY < 0) {
			newY -= offY;
		}

		if (newWidth + offX >= pW) {
			newWidth -= newWidth + offX - pW;
		}

		if (newHeight + offY >= pH) {
			newHeight -= newHeight + offY - pH;
		}

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + offX, y + offY, image.getP()[x + y * image.getW()]);
				setLightBlock(x + offX, y + offY, image.getLightBlock());
			}
		}
	}

	public void drawImage(BufferedImage image, int offX, int offY) {
		offX -= camX;
		offY -= camY;

		int newX = 0;
		int newY = 0;
		int newWidth = image.getWidth();
		int newHeight = image.getWidth();

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + offX, y + offY, image.getRGB(x, y));
			}
		}
	}

	public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {
		offX -= camX;
		offY -= camY;
		if (image.isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
			return;
		}

		// Don't render code
		if (offX < -image.getTileW()) {
			return;
		}

		if (offY < -image.getTileH()) {
			return;
		}

		if (offX >= pW) {
			return;
		}

		int newX = 0;
		int newY = 0;
		int newWidth = image.getTileW();
		int newHeight = image.getTileH();

		if (offY >= pH) {
			return;
		}

		// clipping code
		if (offX < 0) {
			newX -= offX;
		}

		if (offY < 0) {
			newY -= offY;
		}

		if (newWidth + offX >= pW) {
			newWidth -= newWidth + offX - pW;
		}

		if (newHeight + offY >= pH) {
			newHeight -= newHeight + offY - pH;
		}

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + offX, y + offY, image.getP()[(x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getW()]);
				setLightBlock(x + offX, y + offY, image.getLightBlock());
			}
		}
	}
	
	public void drawImageTile(BufferedImage image, int offX, int offY, int tileX, int tileY) {
		offX -= camX;
		offY -= camY;

		// Don't render code
		if (offX < -image.getTileWidth()) {
			return;
		}

		if (offY < -image.getTileHeight()) {
			return;
		}

		if (offX >= pW) {
			return;
		}

		int newX = 0;
		int newY = 0;
		int newWidth = image.getTileWidth();
		int newHeight = image.getTileHeight();

		if (offY >= pH) {
			return;
		}

		// clipping code
		if (offX < 0) {
			newX -= offX;
		}

		if (offY < 0) {
			newY -= offY;
		}

		if (newWidth + offX >= pW) {
			newWidth -= newWidth + offX - pW;
		}

		if (newHeight + offY >= pH) {
			newHeight -= newHeight + offY - pH;
		}

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + offX, y + offY, image.getRGB(x, y));
			}
		}
	}

	public void drawRect(int offX, int offY, int width, int height, int color) {
		offX -= camX;
		offY -= camY;
		for (int y = 0; y <= height; y++) {
			setPixel(offX, y + offY, color);
			setPixel(offX + width, y + offY, color);
		}

		for (int x = 0; x <= width; x++) {
			setPixel(x + offX, offY, color);
			setPixel(x + offX, offY + height, color);
		}
	}

	public void drawFillRect(int offX, int offY, int width, int height, int color) {
		offX -= camX;
		offY -= camY;

		// don't render code
		if (offX < -width) return;
		if (offY < -height) return;
		if (offX >= pW) return;
		if (offY >= pH) return;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setPixel(x + offX, y + offY, color);
			}
		}

	}

	public void drawLight(Light l, int offX, int offY) {
		lightRequest.add(new LightRequest(l, offX, offY));
	}

	private void drawLightRequest(Light l, int offX, int offY) {
		offX -= camX;
		offY -= camY;
		for (int i = 0; i <= l.getDiameter(); i++) {
			drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);
		}
	}

	// lol yeah dont touch this James
	private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offX, int offY) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1; // if x0 < x1, set sc to 1, otherwise set to -1
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while (true) {

			int screenX = x0 - l.getRadius() + offX;
			int screenY = y0 - l.getRadius() + offY;

			if (screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH) {
				return;
			}

			int lightColor = l.getLightValue(x0, y0);

			if (lightColor == 0) {
				return;
			}

			if (lb[screenX + screenY * pW] == Light.FULL) {
				return;
			}

			setLightMap(screenX, screenY, lightColor);

			if (x0 == x1 && y0 == y1) {
				break;
			}

			e2 = 2 * err;

			if (e2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}

			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public int getzDepth() {
		return zDepth;
	}

	public void setzDepth(int zDepth) {
		this.zDepth = zDepth;
	}

	public int getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(int ambientColor) {
		this.ambientColor = ambientColor;
	}

	public float getCamX() {
		return camX;
	}

	public void setCamX(float camX) {
		this.camX = camX;
	}

	public float getCamY() {
		return camY;
	}

	public void setCamY(float camY) {
		this.camY = camY;
	}

	public BufferedImage transformImage(BufferedImage image, int rotation) {
		int scale = 1;
		int scaledWidth = (int) (scale * image.getWidth());
		int scaledHeight = (int) (scale * image.getHeight());

		AffineTransform transform;
		if (rotation % 180 == 0) {
			transform = AffineTransform.getRotateInstance(Math.toRadians(rotation), scaledWidth / 2, scaledHeight / 2);
			transform.scale(scale, scale);
		} else {
			transform = AffineTransform.getTranslateInstance((scaledHeight - scaledWidth) / 2, (scaledWidth - scaledHeight) / 2);
			transform.rotate(Math.toRadians(rotation), scaledWidth / 2, scaledHeight / 2);
			transform.scale(scale, scale);
		}
		AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return operation.filter(image, null);
	}
}
