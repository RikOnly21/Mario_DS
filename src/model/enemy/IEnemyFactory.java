package model.enemy;

import java.awt.image.BufferedImage;

public interface IEnemyFactory {
    public Enemy createEnemy(double x,double y,BufferedImage style);
}
