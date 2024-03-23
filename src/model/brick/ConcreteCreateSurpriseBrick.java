package model.brick;

import java.awt.image.BufferedImage;

import model.prize.Prize;

public class ConcreteCreateSurpriseBrick implements IBirckFactory{
public Brick createBrick(double x, double y, BufferedImage style,Prize prize) {  
                return new SurpriseBrick(x,y,style,prize);  
    }
}
