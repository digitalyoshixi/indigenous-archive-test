package indigenousarchive;

import processing.core.PApplet;
import processing.core.PImage;

public class Button extends Rect{
    
    private PImage picture;
    private Rect bg;

    public Button(PApplet p, int x, int y, int width, int height, int color, PImage picture){
        super(p,x,y,width,height,color);
        this.picture = picture;
        bg = new Rect(p,x-5,y-5,width+10,height+10,0xFF000000);
    }
    public Button(PApplet p, int x, int y, int width, int height, int color){
        this(p,x,y,width,height,color,null);
    }
    @Override
    public void draw(){
        // draw the background
        bg.draw();
        // draw the rectangle
        super.draw();
        // draw the image
        super.getApp().image(picture,super.getX(),super.getY(),super.getWidth(),super.getHeight());
    }

    public boolean checkIntersection(int x, int y){
        if (super.getX() <= x && super.getX()+super.getWidth() >= x &&
            super.getY() <= y && super.getY()+super.getHeight() >= y){
          return true;
          }
        else{
          return false;
        }
      }
}
