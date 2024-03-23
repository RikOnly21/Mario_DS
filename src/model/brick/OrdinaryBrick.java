package model.brick;

import manager.GameEngine;
import manager.MapManager;
import model.GameObject;
import model.Map;
import model.hero.Mario;

import model.prize.Prize;
import view.Animation;
import view.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrdinaryBrick extends Brick {

	private Animation animation;
	private boolean breaking;
	private int frames;

	public OrdinaryBrick(double x, double y, BufferedImage style) {
		super(x, y, style);
		setBreakable(true);
		setEmpty(true);

		setAnimation();
		breaking = false;
		frames = animation.getLeftFrames().length;
	}

	private void setAnimation() {
		ImageLoader imageLoader = new ImageLoader();
		BufferedImage[] leftFrames = imageLoader.getBrickFrames();

		animation = new Animation(leftFrames, leftFrames);
	}

	@Override
	public void breakBrick(GameEngine engine, Mario mario) {
		MapManager manager = engine.getMapManager();

		if (!mario.isSuper())
			return;

		breaking = true;
		manager.addRevealedBrick(this);

		double newX = getX() - 27, newY = getY() - 27;
		setLocation(newX, newY);

		return;
	}

	@Override
	public void breakBrick2(GameEngine engine, Mario mario2) {
		MapManager manager = engine.getMapManager();

		if (!mario2.isSuper())
			return;

		breaking = true;
		manager.addRevealedBrick(this);

		double newX = getX() - 27, newY = getY() - 27;
		setLocation(newX, newY);

		return;
	}

	public int getFrames() {
		return frames;
	}

	public void animate() {
		if (breaking) {
			setStyle(animation.animate(3, true));
			frames--;
		}
	}
	
}
