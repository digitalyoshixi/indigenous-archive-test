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

public class ImageDownload {
    String filetype = "";
    String filename = "";

    public ImageDownload() {
       
    }
    public void downloadimg(String url) throws IOException,NoSuchAlgorithmException{
        //Open a URL Stream
        Connection.Response resultImageResponse = Jsoup.connect(url).ignoreContentType(true).execute();
        

        // Download image
    
        // get filename
        Pattern pattern = Pattern.compile("[^/]+(?=\\.(png|gif|jpg|jpeg|webp)$)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            filename = url.substring(matcher.start(),matcher.end());
        }

        // get filetype
        pattern = Pattern.compile(".(png|gif|jpg|jpeg|webp)");
        matcher = pattern.matcher(url);
        boolean truth = matcher.find();
     
        if (truth){
            filetype = url.substring(matcher.start(),matcher.end());
        }

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

    public void saveImg(Connection.Response resultImageResponse, String path) throws IOException{
        FileOutputStream out = (new FileOutputStream(new java.io.File(path)));
        out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
        out.close(); 
    }

    public String getFileName(String filename){ 
        // function exists solely to prevent duplicate filenames from being saved.
        boolean nodupes = false;
        String retname = filename+getRandName("");
        while (nodupes==false){
            nodupes = true;
            File folder = new File("archive/");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String currfname = listOfFiles[i].getName();
                    if (currfname.equals(retname+filetype)){
                        nodupes=false;
                        retname = filename+getRandName("");
                    }  
                } 
            }
        }
        return retname;
         
    }


    char[] saltchars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    public String getRandName(String givestr){ // https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
        if (givestr.length()>=5){
            return givestr;
        }
        
        // not >=5.
        int randomNum = ThreadLocalRandom.current().nextInt(0, saltchars.length);
        givestr+=saltchars[randomNum];
        return getRandName(givestr);
    }

    public boolean checkdupes(String path, File tempfile) throws NoSuchAlgorithmException{ // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
        boolean retbool = false;
        File folder = new File(path);
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("no second order shit");
            } 
            else {
                // see if the files are the EXACT SAME. pixel for pixel type shit
                try {
                    String f1md5 = getMD5(tempfile);
                    String f2md5 = getMD5(fileEntry);
                    if (f1md5.equals(f2md5)){
                        retbool = true;
                        break;
                    }
                }
                catch (Exception e){
                    System.out.println(e);   
                }
                
            }
        }
        return retbool;
    }

    public static String getMD5(File filename) throws NoSuchAlgorithmException{
		String md5 = "";
		try {
			InputStream filestream = new FileInputStream(filename);
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(filestream);
		}
		catch (Exception e){
			System.out.println(e);
		}
		return md5;
	}

    public void jsonwrite(String imagename, String urlname){
        try {
            // write the archive info list with the http code, og url, new archived url to the json file 
            FileWriter writer = new FileWriter("images.xml", true);
            String towrite = String.format("%s,%s\n",imagename,urlname);
            writer.write(towrite);
            writer.close();
        }
        catch (IOException e){

        }

    }

}