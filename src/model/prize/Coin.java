package model.prize;

import manager.GameEngine;
import model.GameObject;
import model.hero.Mario;


import java.awt.*;
import java.awt.image.BufferedImage;

public class Coin extends GameObject{

	private int point;
	private boolean revealed, acquired = false;
	private int revealBoundary;

	public Coin(double x, double y, BufferedImage style, int point) {
		super(x, y, style);
		this.point = point;
		revealed = false;
		setDimension(30, 42);
		revealBoundary = (int) getY() - getDimension().height;
	}

	public int getPoint() {
		return point;
	}

	public void reveal() {
		revealed = true;
	}

	public void onTouch(Mario mario, GameEngine engine) {
		if (!acquired) {
			acquired = true;
			mario.acquirePoints(point);
			mario.acquireCoin();
			engine.playCoin();
		}
	}

	public void onTouch2(Mario mario2, GameEngine engine) {
		if (!acquired) {
			acquired = true;
			mario2.acquirePoints(point);
			mario2.acquireCoin();
			engine.playCoin();
		}
	}

	@Override
	public void updateLocation() {
		if (revealed) {
			setY(getY() - 5);
		}
	}

	@Override
	public void draw(Graphics g) {
		if (revealed) {
			g.drawImage(getStyle(), (int) getX(), (int) getY(), null);
		}
	}

	public int getRevealBoundary() {
		return revealBoundary;
	}

}
