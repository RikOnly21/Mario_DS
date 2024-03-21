package model.brick;

import java.awt.image.BufferedImage;

import model.prize.Prize;

public interface IBirckFactory {

    public Brick createBrick(double x, double y, BufferedImage style,Prize prize);
    
    

}
