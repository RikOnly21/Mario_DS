package model.enemy;

import java.awt.image.BufferedImage;

public class ConcreteCreateGoomba implements IEnemyFactory{
    public Enemy createEnemy(double x,double y,BufferedImage style){
       
              return new Goomba(x,y,style);
        

  }

}
