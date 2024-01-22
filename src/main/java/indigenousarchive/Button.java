package indigenousarchive;

import processing.core.PApplet;
import processing.core.PImage;

/**
	 * Button class to allow for clicking on and to have images on
	 * @author	 David Chen
	 * @since 2024-1-21
	 * @version 1.0
*/
public class Button extends Rect{
    // attributes
    private PImage picture;
    private Rect bg;

    /**
     * Constructor for the button
     * @param p PApplet canvas
     * @param x The x coordinate (topleft)
     * @param y The y coordinate (topleft)
     * @param width The width of the button
     * @param height The height of the button
     * @param color The color of the button background
     * @param picture The hopefully transparent png of a button image
     */
    public Button(PApplet p, int x, int y, int width, int height, int color, PImage picture){
        super(p,x,y,width,height,color); // let parent initialize these attributes
        this.picture = picture; // set pucutre
        bg = new Rect(p,x-5,y-5,width+10,height+10,0xFF000000); // make a background rectangle for beauty
    }
    /**
     * Overloaded constructor for Button without an image
     * @param p PApplet canvas
     * @param x The x coordinate (topleft)
     * @param y The y coordinate (topleft)
     * @param width The width of the button
     * @param height The height of the button
     * @param color The color of the button background
    */
    public Button(PApplet p, int x, int y, int width, int height, int color){
        this(p,x,y,width,height,color,null);
    }
    
    /**
     * Draws the button on the screen. Overrides the rectangle draw and adds a background and image 
     */
    @Override
    public void draw(){
        // draw the background
        bg.draw();
        // draw the rectangle
        super.draw();
        // draw the image
        super.getApp().image(picture,super.getX(),super.getY(),super.getWidth(),super.getHeight());
    }

    /**
     * Checks if given x,y coordinate intersects with the button's hitbox.
     * @param x The x coordinate of a coordinate to check
     * @param y The y coorindate of a coord to check
     * @return boolean of if there is an inersection
     */
    public boolean checkIntersection(int x, int y){
      // check if the point is within the boundaries of this button;s rectangle
        if (super.getX() <= x && super.getX()+super.getWidth() >= x &&
            super.getY() <= y && super.getY()+super.getHeight() >= y){
          return true;
          }
        else{
          return false;
        }
      }
}
