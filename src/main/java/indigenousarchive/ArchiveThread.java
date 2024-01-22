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

public class ArchiveThread extends Thread {
    private String url;
    private static int numthreads;

    ArchiveThread(String url){
        this.url = url;
        numthreads++;
    }   

    public void run(){
        System.out.println("There are: "+numthreads+" Threads");
        archive(url);
    }

    public static void archive(String url) {
        System.out.println("ARCHIVING "+url);
        
        try{
            // open browser engine and connect to url
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            String title = doc.title();
            System.out.println(title);
            // make a SINGLE task to archive this
            List<ArchiveTask> taskList = new ArrayList<>();
            Elements image = doc.getElementsByTag("img");
            List<String> urls = new ArrayList<String>();
            int totalurls = 0;
            Archiver archiver = new Archiver();
            ImageDownload downloader = new ImageDownload();
            for (Element src: image){ 
                String imageurl = src.attr("abs:src"); // image url
                urls.add(imageurl);
                taskList.add(new ArchiveTask(imageurl,archiver,downloader));	
                totalurls++;
            }
            System.out.println("total urls:" + totalurls);
            // archive with a thread.
            List<Future<String[]>> resultList = null; // im actually not going to read this string. it takes too long
            // all available cores
            //int coreCount = Runtime.getRuntime().availableProcessors()-1;
            ExecutorService executor = Executors.newFixedThreadPool(1);
                
            resultList= executor.invokeAll(taskList, 3600, TimeUnit.SECONDS); // Timeout of 10 minutes.

            executor.shutdown(); 

        }
        catch (Exception e){
            System.out.println("Error occured during archival");
           
            //e.printStackTrace();
        }
    }
    
}
