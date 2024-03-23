package model.enemy;

import java.awt.image.BufferedImage;

public class ConcreteCreateKoopaTroopa implements IEnemyFactory{
    public Enemy createEnemy(double x,double y,BufferedImage style){
       
              return new KoopaTroopa(x,y,style);
         
       
        

  }
}
