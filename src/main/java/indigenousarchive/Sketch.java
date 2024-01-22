package indigenousarchive;

import java.util.ArrayList;
import java.util.List;


import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.util.concurrent.ThreadLocalRandom;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.awt.datatransfer.DataFlavor;

import org.apache.commons.validator.routines.UrlValidator;
import java.util.Scanner;

/**
 * THE BIG ONE. the main method that makes UI and incorporates functionality between UI and crawling
 * @author David Chen
 * @since 2024-1-21
 * @verion 1.0
 */
public class Sketch extends PApplet {
    
    // attributes
    private String menu = "Home"; // Home, Gallery
    private PFont font; // Arial, 16 point, anti-aliasing on
    private UrlValidator urlvalidate = new UrlValidator(); // check if a given url is valid
    private Rect menubg = new Rect(this,20,20,960,560); // a smaller background for each menu
    private int cores = Runtime.getRuntime().availableProcessors(); // number of CPU cores
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // users system clipboard
    
    // home attributes
    private String currenturl = "http://gnu.org"; // example url that will be replaced 
    private int statustimer = 0; // when it hits 300, then you can swap.
    private String urlstatus = "Archive A URL!"; // text to be written to screen depending on the current status of a sent request
        // attributes for drawing
        private Rect searchbarouter = new Rect(this,35,300,930,50,0xFF000000); // search bar rectangle
        private Rect searchbarinner = new Rect(this,40,305,920,40); // search bar inner
        private PImage logo; // logo that will be gotten later
        private Button homebutton; // button to changemey to home
        private Button gallerybutton; // button to change menu to gallery
    // gallery attributes
    private List<List<Object>> gallerylist = new ArrayList<List<Object>>(); // the list of all gallery items
    private int galleryindex = 0; // current gallery item
    private Button oldcopy; // button to copy old url
    private Button newcopy; // button to copy archived url
    String currentold = ""; // the text of the old url
    String currentnew = ""; // the text of the archived url

    
    /**
     * Basic setting of screen size to 1000, 600
     */
    public void settings() {
        size(1000, 600);

    }

    /**
     * Setup loads all images and sets background and font 
     */
    public void setup() {
        background(152, 190, 100);
        // load font
        font = createFont("Arial",16,true);
        textFont(font,30);
        // load images
        logo = loadImage("images/logo.png");
        PImage home = loadImage("images/home.png");
        PImage gallery = loadImage("images/gallery.png");
        PImage oldimage = loadImage("images/old.png");
        PImage newimage  = loadImage("images/newarchive.png");
        // create buttons
        homebutton = new Button(this,400,500,50,50,0xFFFFFFFF,home);
        homebutton.setColor(0xFFFFBB22);
        gallerybutton = new Button(this,480,500,50,50,0xFFFFFFFF,gallery);
        oldcopy = new Button(this,600,500,50,50,0xFFFFFFFF,oldimage);
        newcopy = new Button(this,680,500,50,50,0xFFFFFFFF,newimage);
    }

    /**
     * Draws every item on screen depednding on current menu if its Home or Gallery.
     */
    public void draw() {
        // fires every frame
        clear(); // well, it doesn't really need to anyhow

        // different graphics depending on each menu        
        switch (menu){
            case "Home":
                drawmenu();
                
                break;
            case "Gallery":
                drawgallery();
                break;
        }
        drawbuttons(); // draw the switching buttons always

    }

    /**
     * just draw the buttons to switch around
     */
    public void drawbuttons(){
        homebutton.draw();
        gallerybutton.draw();
    }
    /**
     * draw the entiremenu.
     */
    public void drawmenu(){
        // set outer background
        background(240,120,100);
        menubg.draw(); // draw inner background
        image(logo,60,40,900,250); // draw logo
        // draw searchbar
        searchbarouter.draw();  
        searchbarinner.draw(); 
        // draw text of current URL
        fill(0);
        textSize(30);
        text(currenturl,40,330);
        // archive status text.
        if (statustimer >= 300){ // only show the last status message for 300 frames (~5 seconds)
            urlstatus = "Archive A URL!";
            statustimer = 0;
        }
        if (!(urlstatus.equals("Archive A URL!"))){ // a differnt status message
            statustimer++; // keep increment until 300
        }
        // draw url status
        text(urlstatus, 360,380);
        // draw tasks info
        text((Thread.activeCount()-3)/2 + " sites are currently being archived...",250,440);
        text("Your PC should not archive more than: "+String.valueOf(cores-3)+" websites concurrently!",100,470);

    }



    // if key is pressed down
    List<Integer> currkeys = new ArrayList<Integer>(); // list for multiple keys at once
    /**
     * bunch of methods to activate once a key is pressed
     */
    public void keyPressed(){
        switch (menu){ // actions depend on menu
            case "Home":
                
                currkeys.add(keyCode);
                if (keyCode == 10){ // enter
                    // check if url provided was valid or not
                    if (urlvalidate.isValid(currenturl)){
                        urlstatus = "Archiving: "+currenturl;
                        // start the archive thread
                        ArchiveThread newthread = new ArchiveThread(currenturl);
                        newthread.start(); // start the new thread
                    }
                    else {
                        urlstatus = "Invalid URL";
                    }
                    
                }
                else if (keyCode == 8){ // backspace
                    if (currenturl.length()>0){
                        currenturl = currenturl.substring(0,currenturl.length()-1); // delete one charcter
                    }
                }
                else if (currkeys.contains(17) && currkeys.contains(86)){ // CTRL+V
                    // paste the currentuser's clipboard into currenturl
                    try {
                        String data = (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor); 
                        
                        currenturl+=String.valueOf(data); // currenturl += clipboard
                    }
                    catch (Exception e) {

                    }
                }
                else if (!(keyCode == 17 || keyCode == 16)){ // not CTRL or Shift
                    currenturl += key; // character represenation of keyCode
                }
                break;
            case "Gallery":
                // right button
                if (keyCode == 39){
                    if (galleryindex < gallerylist.size()-1){ // not exceeding gallery size
                        galleryindex++; // go to next image
                    }
                }
                // left button
                else if(keyCode == 37){ 
                    if (galleryindex > 0){ // not smaller than gallery size
                        galleryindex--; // go to last image
                    }
                }
                
                break;
        }
        

    }

    /**
     * when a key is releasd to remove some keys from current key list
     */
    public void keyReleased(){
        if (currkeys.contains(keyCode)){ // check if current key is inside current keys pressed
            currkeys.remove(currkeys.indexOf(keyCode)); // remove that key from the list
        }
    }
    /**
     * draw the gallery 
     */
    public void drawgallery(){
        background(240,120,100); // draw bg
        menubg.draw(); // draw smaller bg
        String oldurl = "none"; // backup plan if we dont have any gallery images
        String newurl = "none"; // backup value if no images
        
        try {
            // draw the current gallery image
            PImage image = (PImage)gallerylist.get(galleryindex).get(0);
            image(image,300,100,300,300);
            // get image information
            oldurl = String.valueOf(gallerylist.get(galleryindex).get(1));
            newurl = String.valueOf(gallerylist.get(galleryindex).get(2));
            
        }
        catch (Exception e){
            
        }   
        // write the gallery texts
        fill(0);
        textSize(20);
        text("old url: "+oldurl,50,450);
        currentold = oldurl; // update currentold old url
        fill(0);
        textSize(20);
        text("archived url: "+newurl,50,480);
        currentnew = newurl; //update currentnew archive url
        // darw buttons to screen
        oldcopy.draw(); 
        newcopy.draw();
        
    }

    /**
     * setup the 2D gallery list by finding all urls in images ist, then finding their archived equivalent inside the arhivelist
     * will make the gallery alphabetically sorted.
     * 2D gallery list should be like: {PImage, old url, new url},...
     */
    public void setupGallery(){ // generate the gallery 2d list.
        gallerylist = new ArrayList<List<Object>>();
        // - alphabetical
        // [image name, archived url]
        try {
            // get all images first.
            List<String[]> imagesmatrix = new ArrayList<String[]>();
            // turn the images xml into the images matrix
            Scanner fileInput = new Scanner(new File("images.xml"));
            while (fileInput.hasNext()){ // grab every line. looks like {imagepath, og url}
                // split every line
                String currline = fileInput.nextLine();
                String[] splittedline = currline.split(",");
                // add that splitted line to images matrix
                imagesmatrix.add(splittedline); 
            }
            fileInput.close();
            // get the archived links
            List<String[]> archivematrix = new ArrayList<String[]>();
            // turn archivedlinks.xml into archivedmatrix
            fileInput = new Scanner(new File("archivedlinks.xml"));
            while (fileInput.hasNext()){ // for every line
                // get splitted line
                String currline = fileInput.nextLine();
                String[] splittedline = currline.split(",");
                // add splitted line to arcived matrix
                archivematrix.add(splittedline); 
            }
            // close scanner GOOD SAMARITAN!
            fileInput.close();

            // bubble sort it.
            bubblesort(archivematrix,999); 
            // create the 2D ultimate matrix to return
            for (String[] i : imagesmatrix){
                // make a single list to add to the 2d matrix
                List<Object> addgallery = new ArrayList<Object>();
                // first add the PImage image
                try {
                    PImage newimage = loadImage("archive/"+i[0]);
                    addgallery.add(newimage);
                }
                catch (Exception e){
                    System.out.println("cant do that sir");
                    addgallery.add(null);
                }
                
                int binkey = linearsearch(archivematrix, i[1]); // get the key of the where the old url resides in the archived matrix
                addgallery.add(i[1]); // add og url
                if (binkey != -1){ // if there is a archived url
                    addgallery.add(archivematrix.get(binkey)[2]); // archived url add
                }
                else { // no archived url
                    addgallery.add("none"); // add none
                }
                // add this current small 1D to the large 2Dgallery list
                gallerylist.add(addgallery);
                
            }
            
            // set starting index as 0 DUHH
            galleryindex = 0;
                        
        }
        catch (Exception e){
            System.out.println("gallery"); 
            //e.printStackTrace();
        }
            
    }
    /**
     * linear search for java index (i didnt have time)
     * @param sortedArray 1D array of imagesmatrix 
     * @param urlkey the old url we want to search for
     * @return the integer index of where this resides in the archived matrix
     */
    public int linearsearch (List<String[]> sortedArray, String urlkey){
        for (int i = 0; i < sortedArray.size();i++){
            if (sortedArray.get(i)[1].equals(urlkey)){
                return i;
            }
        }
        return -1;
    }
    
    /** 
     * @return List<String[]>
     */
    // // java binary search
    // public int binarysearch (List<String[]> sortedArray, String urlkey, int low, int high){
    //     int middle = low  + ((high - low) / 2);
    //     //System.out.println(String.format("URL: %s, HIGH: %d, LOW: %d, MED: %d",urlkey,high,low,middle));
        
    //     if (high < low) {
    //         return -1;
    //     }
    //     int checkit = urlkey.compareTo(sortedArray.get(middle)[1]);
    //     if (checkit == 0) {
    //         return middle;
    //     } else if (checkit < 0) {
    //         return binarysearch(
    //         sortedArray, urlkey, low, middle - 1);
    //     } else {
    //         return binarysearch(
    //         sortedArray, urlkey, middle + 1, high);
    //     }
    // }

    /**
     * bubble sort a given list of stirngs
     * @param oglis A String list we need to sort
     * @param numswaps the # of swaps for the recurse method
     * @return the sorted list alphabetically
     */
    public List<String[]> bubblesort(List<String[]> oglis, int numswaps){
        if (numswaps <= 1){ // base case
            return oglis; // return the alphabetically sorted list 
        }
        int numchanges = 0;
        // swap every elements
        for (int i = 0; i<oglis.size()-1;i++){ // for every element in the original list
            // swap from image name
            String[] first = oglis.get(i); // this row
            String[] second = oglis.get(i+1);   // next row
            // check if this row's url is alphabetically lower than next row's url
            if (first[0].compareTo(second[0])>0){ 
                //switcheroo
                oglis.set(i,second);
                oglis.set(i+1,first);
                numchanges++; // a change has occured
            }
        }
        return bubblesort(oglis,numchanges); // reutrned newly swapped list
    }


    /**
     * mouse pressed to check mainly for button presses
     */
    public void mousePressed(){
        if (homebutton.checkIntersection(mouseX, mouseY)){ // check if clicking home button
            // set menu
            menu = "Home";
            // change button colors
            homebutton.setColor(0xFFFFBB22);
            gallerybutton.setColor(0xFFFFFFFF);
        }
        else if (gallerybutton.checkIntersection(mouseX, mouseY)){ // check if clicking gallery button
            // set up gallery
            setupGallery();
            // change menu
            menu = "Gallery";
            // change button colors
            homebutton.setColor(0xFFFFFFFF);
            gallerybutton.setColor(0xFFFFBB22);

        }
        else if (oldcopy.checkIntersection(mouseX, mouseY)){ // check if currently clicking old button
            // copy the current old url to clipboard
            StringSelection selection = new StringSelection(currentold);
            clipboard.setContents(selection, null);
        }
        else if (newcopy.checkIntersection(mouseX, mouseY)){ // check if clicking new button
            // copy current new url to clipboard
            StringSelection selection = new StringSelection(currentnew);
            clipboard.setContents(selection, null);
        }

    }
    
}
