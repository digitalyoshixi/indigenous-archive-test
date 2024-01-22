package indigenousarchive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
	 * 	A thread reponsible for downloading and archive all images from a URL. calls the ArchiveTask and be in charge for its timeout terminatina after 6 minutes
	 * @author	 David Chen
	 * @since 2024-1-21
	 * @version 1.1
*/
public class ArchiveThread extends Thread {
    private String url;
    private static int numthreads; // static variable to count # of threads

    /**
     * Constructor that requests only a single url
     * @param url The url to archive all the images of.
     */
    ArchiveThread(String url){
        this.url = url;
        numthreads++; // incrase the static variable
    }   

    /**
     * required command for threads to have. just says what to start. will just start the archive method
     */
    public void run(){
        //System.out.println("There are: "+numthreads+" Threads");
        archive(url);
    }

    /**
     * callin the archiving task and watching it to make sureit doesn't exceed 6 minutes.
     * @param url URL to archive.
     */
    public static void archive(String url) {
        System.out.println("ARCHIVING "+url);
        
        try{
            // open browser engine and connect to url
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            String title = doc.title();
            System.out.println(title);
            // make a SINGLE task to archive this
            List<ArchiveTask> taskList = new ArrayList<>();
            Elements image = doc.getElementsByTag("img"); // grab all images from the webpage
            List<String> urls = new ArrayList<String>(); // all image urls on this webpage
            int totalurls = 0; // counter to just say how many images there are in total
            Archiver archiver = new Archiver(); // new archiver
            ImageDownload downloader = new ImageDownload(); // new downloader
            for (Element src: image){  // for every image url
                String imageurl = src.attr("abs:src"); // image url
                urls.add(imageurl); // add this to urls list
                taskList.add(new ArchiveTask(imageurl,archiver,downloader));	
                totalurls++; 
            }
            System.out.println("total urls:" + totalurls); // how many images we can archive
            // archive with a thread.
            List<Future<String[]>> resultList = null; // im actually not going to read this string. it takes too long
            
            ExecutorService executor = Executors.newFixedThreadPool(1); // just with one thread. dont want to overload requests
                
            resultList= executor.invokeAll(taskList, 3600, TimeUnit.SECONDS); // Timeout of 6 minutes.

            executor.shutdown(); // shutdown after the 6 minutes

        }
        catch (Exception e){
            System.out.println("Error occured during archival");
           
            //e.printStackTrace();
        }
    }
    
}
