package model.prize;

import manager.GameEngine;
import manager.MapManager;
import model.hero.Mario;


import java.awt.*;
import model.UIDesign;

public interface Prize extends UIDesign{

	int getPoint();

	void reveal();

	Rectangle getBounds();

	void onTouch(Mario mario, GameEngine engine);

	void onTouch2(Mario mario2, GameEngine engine);

}
