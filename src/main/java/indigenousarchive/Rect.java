package indigenousarchive;

import processing.core.PApplet;

/**
	 * Rectangle class to represent rectangles of varying sizes
	 * @author David Chen
	 * @since 2024-1-21
	 * @version 1.0
*/
public class Rect {
    private PApplet app;
    private int x;
    private int y;
    private int width;
    private int height;
    private int color;

    /**
     * Constructor for rectangle
     * @param p PApplet canvas to draw rectangle on
     * @param x The x coordinate (topleft)
     * @param y The y coordinate (topleft)
     * @param width The width of rectangle
     * @param height the height of rectangle
     * @param color The color the rectangle will dawn
     */
    public Rect(PApplet p, int x, int y,int width, int height, int color){
        this.app = p;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color; // example like 0xFF8899AA 
    }

    /**
     * Overloaded constructor of rectangle that sets default color to 0xFFFFFFFF
     * @param p PApplet canvas to draw rectangle on
     * @param x The x coordinate (topleft)
     * @param y The y coordinate (topleft)
     * @param width The width of rectangle
     * @param height the height of rectangle
     * @param color The color the rectangle will dawn
     */
    public Rect(PApplet p, int x, int y,int width, int height){
        this(p,x,y,width,height,0xFFFFFFFF); // alpha, red, blue. green
    }   

    // accessors
    /**
     * Returns the PApplet app
     * @return PApplet app
     */
    public PApplet getApp(){
        return this.app;
    }
    /**
     * returns the x value
     * @return integer x (topleft)
     */
    public int getX(){
        return this.x;
    }
    /**
     * returns y value
     * @return integer y (topleft)
     */
    public int getY(){
        return this.y;
    }
    /**
     * returns rectangles widtth
     * @return integer width
     */
    public int getWidth(){
        return this.width;
    }
    /**
     * returns the rectangles height
     * @return integer height
     */
    public int getHeight(){
        return this.height;
    }
    /**
     * returns the hex color of the rectangle
     * @return integer hex color
     */
    public int getColor(){
        return this.color;
    }
    
    
    // mutators
    /**
     * Sets the retangles color to given color
     * @param color The new color of the rectangle
     */
    public void setColor(int color){
        this.color = color;
    }

    /**
     * Draw method to put rectangle on the PApplet canvas with its ceratin color
     */
    public void draw(){
        app.fill(this.color);
        app.rect(x, y, width,height);
    }
    
}
