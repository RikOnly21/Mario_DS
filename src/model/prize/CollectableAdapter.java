package model.prize;

import java.awt.Graphics;
import java.awt.Rectangle;
import manager.GameEngine;
import model.GameObject;
import model.hero.Mario;
//adapter design pattern
public class CollectableAdapter implements Prize{
    
    private GameObject gameObj;
    public CollectableAdapter(GameObject gameObj) {
        this.gameObj = gameObj;
    }

    public GameObject getGameObj() {
        return gameObj;
    }

    @Override
    public int getPoint() {
        if(gameObj instanceof Coin) {
            return ((Coin)gameObj).getPoint();
        } else if(gameObj instanceof BoostItem) {
            return ((BoostItem)gameObj).getPoint();
        }
        return -1;
    }

    @Override
    public void reveal() {
        if(gameObj instanceof Coin) {
            ((Coin)gameObj).reveal();
        } else if(gameObj instanceof BoostItem) {
            ((BoostItem)gameObj).reveal();
        }
    }

    @Override
    public Rectangle getBounds() {
        if(gameObj instanceof Coin) {
            return ((Coin)gameObj).getBounds();
        } else if(gameObj instanceof BoostItem) {
            return ((BoostItem)gameObj).getBounds();
        }
        return null;
    }

    @Override
    public void onTouch(Mario mario, GameEngine engine) {
        if(gameObj instanceof Coin) {
            ((Coin)gameObj).onTouch(mario, engine);
        } else if(gameObj instanceof BoostItem) {
            ((BoostItem)gameObj).onTouch(mario, engine);
        }
    }

    @Override
    public void onTouch2(Mario mario2, GameEngine engine) {
        if(gameObj instanceof Coin) {
            ((Coin)gameObj).onTouch2(mario2, engine);
        } else if(gameObj instanceof BoostItem) {
            ((BoostItem)gameObj).onTouch2(mario2, engine);
        }
    }

    @Override
    public void draw(Graphics g) {
        if(gameObj instanceof Coin) {
            ((Coin)gameObj).draw(g);
        } else if(gameObj instanceof BoostItem) {
            ((BoostItem)gameObj).draw(g);
        }
    }
    
    
}
