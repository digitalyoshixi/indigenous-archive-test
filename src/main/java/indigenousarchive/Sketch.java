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

public class Sketch extends PApplet {
    
    // attributes
    private String menu = "Home"; // Home, Gallery
    private PFont font; // Arial, 16 point, anti-aliasing on
    private UrlValidator urlvalidate = new UrlValidator();
    private Rect menubg = new Rect(this,20,20,960,560);
    private int cores = Runtime.getRuntime().availableProcessors();
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    // home attributes
    private String currenturl = "http://gnu.org";
    private int statustimer = 0; // when it hits 300, then you can swap.
    private String urlstatus = "Archive A URL!";
        // attributes for drawing
        private Rect searchbarouter = new Rect(this,35,300,930,50,0xFF000000);
        private Rect searchbarinner = new Rect(this,40,305,920,40);
        private PImage logo;
        private Button homebutton;
        private Button gallerybutton;
    // gallery attributes
    private List<List<Object>> gallerylist = new ArrayList<List<Object>>();
    private int galleryindex = 0;
    private Button oldcopy;
    private Button newcopy;
    String currentold = "";
    String currentnew = "";

    
    public void settings() {
        size(1000, 600);

    }

    public void setup() {
        background(152, 190, 100);
        font = createFont("Arial",16,true);
        textFont(font,30);
        logo = loadImage("images/logo.png");
        PImage home = loadImage("images/home.png");
        PImage gallery = loadImage("images/gallery.png");
        PImage oldimage = loadImage("images/old.png");
        PImage newimage  = loadImage("images/newarchive.png");
        homebutton = new Button(this,400,500,50,50,0xFFFFFFFF,home);
        homebutton.setColor(0xFFFFBB22);
        gallerybutton = new Button(this,480,500,50,50,0xFFFFFFFF,gallery);
        oldcopy = new Button(this,600,500,50,50,0xFFFFFFFF,oldimage);
        newcopy = new Button(this,680,500,50,50,0xFFFFFFFF,newimage);
    }

    
    public void draw() {
        // fires every frame
        clear();

        // different graphics depending on each menu        
        switch (menu){
            case "Home":
                drawmenu();
                
                break;
            case "Gallery":
                drawgallery();
                break;
        }
        drawbuttons();

    }

    public void drawbuttons(){
        homebutton.draw();
        gallerybutton.draw();

    }
    public void drawmenu(){
        background(240,120,100);
        menubg.draw();
        image(logo,60,40,900,250);
        // draw searchbar
        searchbarouter.draw(); 
        searchbarinner.draw(); 
        fill(0);
        textSize(30);
        text(currenturl,40,330);
        // archive status text.
        if (statustimer >= 300){ // only show the last status message for 300 frames (~5 seconds)
            urlstatus = "Archive A URL!";
            statustimer = 0;
        }
        if (!(urlstatus.equals("Archive A URL!"))){
            statustimer++;
        }
        text(urlstatus, 360,380);
        text((Thread.activeCount()-3)/2 + " sites are currently being archived...",250,440);
        text("Your PC should not archive more than: "+String.valueOf(cores-3)+" websites concurrently!",100,470);
        // draw the search query.


    }



    // if key is pressed down
    List<Integer> currkeys = new ArrayList<Integer>(); 
    public void keyPressed(){
        switch (menu){
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
                else if (currkeys.contains(17) && currkeys.contains(86)){
                    try {
                        String data = (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor); 
                        
                        currenturl+=String.valueOf(data);
                    }
                    catch (Exception e) {

                    }
                    
                }
                else if (!(keyCode == 17 || keyCode == 16)){ // not CTRL or Shift
                    currenturl += key; // character represenation of keyCode
                }
                break;
            case "Gallery":
                
                if (keyCode == 39){
                    if (galleryindex < gallerylist.size()-1){
                        galleryindex++;
                    }
                }
                else if(keyCode == 37){
                    if (galleryindex > 0){
                        galleryindex--;
                    }
                }
                
                break;
        }
        

    }

    public void keyReleased(){
        if (currkeys.contains(keyCode)){
            currkeys.remove(currkeys.indexOf(keyCode));
        }
    }

    public void drawgallery(){
        background(240,120,100);
        menubg.draw();
        // draw the current gallery image
        try {
            PImage image = (PImage)gallerylist.get(galleryindex).get(0);
            
            image(image,300,100,300,300);
        }
        catch (Exception e){
            
        }   
            String oldurl = String.valueOf(gallerylist.get(galleryindex).get(1));
            String newurl = String.valueOf(gallerylist.get(galleryindex).get(2));
            // write the gallery texts
            fill(0);
            textSize(20);
            text("old url: "+oldurl,50,450);
            currentold = oldurl;
            fill(0);
            textSize(20);
            text("archived url: "+newurl,50,480);
            currentnew = newurl;
            oldcopy.draw();
            newcopy.draw();
        
        
    }

    public void setupGallery(){ // generate the gallery 2d list.
        gallerylist = new ArrayList<List<Object>>();
        // - alphabetical
        // [image name, archived url]
        try {
            // get all images first.
            List<String[]> imagesmatrix = new ArrayList<String[]>();
            Scanner fileInput = new Scanner(new File("images.xml"));
            while (fileInput.hasNext()){
                String currline = fileInput.nextLine();
                String[] splittedline = currline.split(",");
                imagesmatrix.add(splittedline); 
            }
            fileInput.close();
            // get the archived links
            List<String[]> archivematrix = new ArrayList<String[]>();
            fileInput = new Scanner(new File("archivedlinks.xml"));
            while (fileInput.hasNext()){
                String currline = fileInput.nextLine();
                String[] splittedline = currline.split(",");
                archivematrix.add(splittedline); 
            }
            fileInput.close();
            // bubble sort it.
            bubblesort(archivematrix,999);
            System.out.println(archivematrix.size());
            for (String[] i : imagesmatrix){
                
                List<Object> addgallery = new ArrayList<Object>();
                try {
                    PImage newimage = loadImage("archive/"+i[0]);
                    addgallery.add(newimage);
                }
                catch (Exception e){
                    System.out.println("cant do that sir");
                    addgallery.add(null);
                }
                
                int binkey = linearsearch(archivematrix, i[1]);
                addgallery.add(i[1]); // add og url
                if (binkey != -1){
                    addgallery.add(archivematrix.get(binkey)[2]); // archived url
                }
                else {
                    addgallery.add("none");
                }
                // add this current small to the large gallery list
                gallerylist.add(addgallery);
                
            }
            
            
            galleryindex = 0;
            
            
        }
        catch (Exception e){
            System.out.println("gallery");
            e.printStackTrace();
        }
            
    }

    public int linearsearch (List<String[]> sortedArray, String urlkey){
        for (int i = 0; i < sortedArray.size();i++){
            if (sortedArray.get(i)[1].equals(urlkey)){
                return i;
            }
        }
        return -1;
    }
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

    // java bubble sort 
    public List<String[]> bubblesort(List<String[]> oglis, int numswaps){
        if (numswaps <= 1){ // base case
            return oglis;
        }
        int numchanges = 0;
        // swap every elements
        for (int i = 0; i<oglis.size()-1;i++){
            // swap from image name
            String[] first = oglis.get(i);
            String[] second = oglis.get(i+1);    
            if (first[0].compareTo(second[0])>0){
                //switcheroo
                oglis.set(i,second);
                oglis.set(i+1,first);
                numchanges++;
            }
        }
        return bubblesort(oglis,numchanges);
    }


    // check the clicks
    public void mousePressed(){
        if (homebutton.checkIntersection(mouseX, mouseY)){
            menu = "Home";
            homebutton.setColor(0xFFFFBB22);
            gallerybutton.setColor(0xFFFFFFFF);
        }
        else if (gallerybutton.checkIntersection(mouseX, mouseY)){
            setupGallery();
            menu = "Gallery";
            homebutton.setColor(0xFFFFFFFF);
            gallerybutton.setColor(0xFFFFBB22);

        }
        else if (oldcopy.checkIntersection(mouseX, mouseY)){
            StringSelection selection = new StringSelection(currentold);
            clipboard.setContents(selection, null);
        }
        else if (newcopy.checkIntersection(mouseX, mouseY)){
            StringSelection selection = new StringSelection(currentnew);
            clipboard.setContents(selection, null);
        }

    }
    
}
