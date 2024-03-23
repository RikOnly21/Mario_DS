package model.brick;

import java.awt.image.BufferedImage;

import model.prize.Prize;

public class ConcreteCreateOrdinaryBirck implements IBirckFactory{
public Brick createBrick(double x, double y, BufferedImage style,Prize prize) {  
                return new OrdinaryBrick(x,y,style);  
    }
}
