package indigenousarchive;

import java.io.IOException;

import java.util.concurrent.Callable;

import java.io.FileWriter;

/**
	 * A task meant to be called from a thread(overload? maybe, but consistent? YES) designed to archive a given url
	 * @author	 David Chen
	 * @since 2024-1-21
	 * @version 1.0
*/
public class ArchiveTask implements Callable<String[]> {
    private String url;
    private Archiver archiver;
    private ImageDownload downloader;
     
    /**
     * Default constructor
     * @param url URL of the website to archive
     * @param archiver the Archiver we want to use to archive with
     * @param downloader the Downloader we want to downloadw with
     */
    public ArchiveTask(String url, Archiver archiver, ImageDownload downloader){
        this.url = url;
        this.archiver = archiver;
        this.downloader = downloader;
    }

    /**
     * the method that is called (the big one). will download and archive the url at once
     * @return String[] of the returned value after archive. {HTTP reponse, og url, archived url location}
     */
    @Override
    public String[] call() {
        System.out.println("archiving... "+this.url); // just debugging
        
        try {
            Thread.sleep(15000); // 15 second wait to avoid sending too many requrests
            downloader.downloadimg(this.url); // download this url as image
            String[] retstrarr = archiver.archiveurl(this.url); // the return string we want to return
            System.out.println(retstrarr[0]);
            if (Integer.parseInt(retstrarr[0])<=400){ // not a bad http return code
                archivewrite(retstrarr);
            }        
            // just some data to be sent back. i dont think i will need it tho.
            return retstrarr;
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        String[] retstrarr = {"none","none","none"}; // if we failed. just return nothing, we didnt connect to webpage any get anythig good
        return retstrarr;
        
    }

    /**
     * Write to the archivedlinks.xml file the String[] of {HTTP reponse, og url, arcived location}
     * @param archiveinfo the {HTTP reponse, og url, arcived location}
     */
    public void archivewrite(String[] archiveinfo){
        try {
            // write the archive info list with the http code, og url, new archived url to the json file 
            FileWriter writer = new FileWriter("archivedlinks.xml", true); // append to this file
            String towrite = String.format("%s,%s,%s\n",archiveinfo[0],archiveinfo[1],archiveinfo[2].substring(10,archiveinfo[2].length()));
            writer.write(towrite); // write to the xml
            writer.close();
        }
        catch (IOException e){

        }

    }
    
}
