package indigenousarchive;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.NoSuchAlgorithmException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * ImageDownload class is an organized class primarily to use downloading methods
 * @author David Chen
 * @verion 1
 * @since 2024-1-20
 */
public class ImageDownload {
    String filetype = "";
    String filename = "";

    /**
     * Barebones constructor. Just instantiates the object
     */
    public ImageDownload() {
       
    }

    /**
     * Download the image of a given url in full. Checks if there are duplicates and gives it a name
     * @param url This is the url of the image
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void downloadimg(String url) throws IOException,NoSuchAlgorithmException{
        //Open a URL Stream
        Connection.Response resultImageResponse = Jsoup.connect(url).ignoreContentType(true).execute();
    
        // get filename
        Pattern pattern = Pattern.compile("[^/]+(?=\\.(png|gif|jpg|jpeg|webp)$)"); // positive lookahead
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            filename = url.substring(matcher.start(),matcher.end()); // keep this filename
        }

        // get filetype
        pattern = Pattern.compile(".(png|gif|jpg|jpeg|webp)"); // only for these filetypes
        matcher = pattern.matcher(url);
        boolean truth = matcher.find();
        if (truth){ // there is a match
            filetype = url.substring(matcher.start(),matcher.end());
        }

        // there is a filename and a filetype
        if (!(filetype.equals("")) && !(filename.equals(""))){
            // download as temp
            saveImg(resultImageResponse, "tmp/temp"+filetype); 

            // check if not already saved via MD5 Checksums
            File tempfile = new File("tmp/temp"+filetype);
            boolean dupes = checkdupes("archive/",tempfile);
            if (!dupes){
                filename = getFileName(filename); // add a salt to the filename
                saveImg(resultImageResponse, "archive/"+filename+filetype);
                jsonwrite(filename+filetype,url); // save to json file the datas
            }
            
            
        }
        
    }
    /**
     * saveImg will save the image to local computer given a relative path
     * @param resultImageResponse The bytes of the image
     * @param path The filepath to save to
     * @throws IOException
     */
    public void saveImg(Connection.Response resultImageResponse, String path) throws IOException{
        // save the image dude
        FileOutputStream out = (new FileOutputStream(new java.io.File(path)));
        out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
        out.close(); 
    }

    /**
     * function exists solely to prevent duplicate filenames from being saved.
     * @param filename a filename without a filetype
     * @return a valid filename without filetype
     */
    public String getFileName(String filename){ 
        // preventing duplicates
        boolean nodupes = false; // there are no dupes
        String retname = filename+getRandName("");
        while (nodupes==false){ // check if there is always no dupes
            nodupes = true; // lock 
            File folder = new File("archive/");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String currfname = listOfFiles[i].getName();
                    if (currfname.equals(retname+filetype)){
                        nodupes=false; // yes there are duplicates
                        retname = filename+getRandName(""); // get a new filename
                    }  
                } 
            }
        }
        return retname; // return the newly made filename or the old one
         
    }


    char[] saltchars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    /**
     * get a random salt. will just make a salt for you to append to your filename.
     * @param givestr A initial salt for the salt to add to. usually just ""
     * @return a new 5-character alphabetical salt
     */
    public String getRandName(String givestr){ 
        if (givestr.length()>=5){
            return givestr;
        }
        
        // not >=5.
        int randomNum = ThreadLocalRandom.current().nextInt(0, saltchars.length);
        givestr+=saltchars[randomNum];
        return getRandName(givestr);
    }

    /**
     * checks the filepath if there are any duplicate images with exact same pixel-pixel as the given tempfile image
     * @param path The filepath you would like to directly look at into
     * @param tempfile The file you dont want any duplicates of
     * @return a boolean if there are duplicates or not
     * @throws NoSuchAlgorithmException
     */
    public boolean checkdupes(String path, File tempfile) throws NoSuchAlgorithmException{ // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
        boolean retbool = false; // returned polarity
        File folder = new File(path); // open this filepath
        for (File fileEntry : folder.listFiles()) { // for every file in filepath
            if (fileEntry.isDirectory()) { // dont open another folder in this path
                System.out.println("no second order shit");
            } 
            else { 
                // see if the files are the EXACT SAME. pixel for pixel type shit
                try {
                    String f1md5 = getMD5(tempfile); // get hash for tempfile
                    String f2md5 = getMD5(fileEntry); // get has for this file
                    if (f1md5.equals(f2md5)){ // if they are the same, then they are duplicate
                        retbool = true;
                        break;
                    }
                }
                catch (Exception e){
                    //System.out.println(e);   
                }
                
            }
        }
        return retbool;
    }

    /**
     * mathod to get the MD5 hash of a file
     * @param filename will be the filepath you want to get the checksum of
     * @return a string of the hash
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(File filename) throws NoSuchAlgorithmException{
		String md5 = ""; // empty string to be filled
		try {
			InputStream filestream = new FileInputStream(filename); // if you can open the file as input. line by line
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(filestream); // md5 is generated
		}
		catch (Exception e){
			System.out.println(e);
		}
		return md5;
	}

    /**
     * add to xml file called images.xml
     * @param imagename the image path including the filetype
     * @param urlname the url of where this image was gotten
     */
    public void jsonwrite(String imagename, String urlname){
        try {
            // write the archive info list with the http code, og url, new archived url to the json file 
            FileWriter writer = new FileWriter("images.xml", true);
            String towrite = String.format("%s,%s\n",imagename,urlname);
            writer.write(towrite); // append this line to the xml file
            writer.close();
        }
        catch (IOException e){

        }

    }

}