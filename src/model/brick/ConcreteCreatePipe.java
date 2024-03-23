package model.brick;

import java.awt.image.BufferedImage;

import model.prize.Prize;

public class ConcreteCreatePipe implements IBirckFactory {
public Brick createBrick(double x, double y, BufferedImage style,Prize prize) {  
                return new Pipe(x,y,style);  
    }
}
