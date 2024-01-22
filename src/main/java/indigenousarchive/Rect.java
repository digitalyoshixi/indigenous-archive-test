package indigenousarchive;

import processing.core.PApplet;

public class Rect {
    public PApplet app;
    private int x;
    private int y;
    private int width;
    private int height;
    private int color;

    public Rect(PApplet p, int x, int y,int width, int height, int color){
        this.app = p;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    // overloaded
    public Rect(PApplet p, int x, int y,int width, int height){
        this(p,x,y,width,height,0xFFFFFFFF); // alpha, red, blue. green
    }   

    // accessors
    public PApplet getApp(){
        return this.app;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getWidth(){
        return this.width;
    }
    public int getHeight(){
        return this.height;
    }
    public int getColor(){
        return this.color;
    }
    
    
    // mutators
    public void setColor(int color){
        this.color = color;
    }

    public void draw(){
        app.fill(this.color);
        app.rect(x, y, width,height);
    }
    
}
