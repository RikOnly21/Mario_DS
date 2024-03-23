package model.brick;

import java.awt.image.BufferedImage;

import model.prize.Prize;

public class ConcreteCreateGroundBrick implements IBirckFactory {
 public Brick createBrick(double x, double y, BufferedImage style,Prize prize) {  
                return new GroundBrick(x,y,style);  
    }
}
