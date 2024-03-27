package manager;

import model.EndFlag;
import model.Map;
import model.brick.Brick;
import model.brick.OrdinaryBrick;
import model.enemy.Enemy;
import model.hero.Fireball;
import model.hero.Mario;
import model.prize.BoostItem;
import model.prize.Coin;
import model.prize.Prize;
import view.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class MapFacade {
    private MapCreator mapCreator;
    
    private Map map;

    public MapFacade(double remainingTime, BufferedImage backgroundImage) {
        map = new Map(remainingTime, backgroundImage);
    }

    public Mario getMario(String whichMario) {
        return map.getMario(whichMario);
    }

    public void setMario(Mario mario, String whichMario) {
        map.setMario(mario, whichMario);
    }

    public ArrayList<Enemy> getEnemies() {
        return map.getEnemies();
    }

    public ArrayList<Fireball> getFireballs() {
        return map.getFireballs();
    }

    public ArrayList<Prize> getRevealedPrizes() {
        return map.getRevealedPrizes();
    }

    public ArrayList<Brick> getAllBricks() {
        return map.getAllBricks();
    }

    public void addBrick(Brick brick) {
        map.addBrick(brick);
    }

    public void addGroundBrick(Brick brick) {
        map.addGroundBrick(brick);
    }

    public void addEnemy(Enemy enemy) {
        map.addEnemy(enemy);
    }

    public void drawMap(Graphics2D g2) {
        map.drawMap(g2);
    }

    public void updateLocations() {
        map.updateLocations();
    }

    public double getBottomBorder() {
        return map.getBottomBorder();
    }

    public void addRevealedPrize(Prize prize) {
        map.addRevealedPrize(prize);
    }

    public void addFireball(Fireball fireball, String whichMario) {
        map.addFireball(fireball, whichMario);
    }

    public void setEndPoint(EndFlag endPoint) {
        map.setEndPoint(endPoint);
    }

    public EndFlag getEndPoint() {
        return map.getEndPoint();
    }

    public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        map.addRevealedBrick(ordinaryBrick);
    }

    public void removeFireball(Fireball object) {
        map.removeFireball(object);
    }

    public void removeEnemy(Enemy enemy) {
        map.removeEnemy(enemy);
    }

    public void removePrize(Prize object) {
        map.removePrize(object);
    }

    public void updateTime(double passed) {
        map.updateTime(passed);
    }

    public boolean isTimeOver() {
        return map.isTimeOver();
    }

    public double getRemainingTime() {
        return map.getRemainingTime();
    }

    public String getPath() {
        return map.getPath();
    }

    public void setPath(String path) {
        map.setPath(path);
    }

    public MapFacade createMap(String mapPath, double timeLimit) {
        return mapCreator.createMap(mapPath, timeLimit);
    }
    
    
    
}