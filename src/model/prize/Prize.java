package model.prize;

import manager.GameEngine;
import manager.MapManager;
import model.hero.Mario;
import model.hero.Mario2;

import java.awt.*;

public interface Prize {

	int getPoint();

	void reveal();

	Rectangle getBounds();

	void onTouch(Mario mario, GameEngine engine);

	void onTouch2(Mario2 mario2, GameEngine engine);

}
