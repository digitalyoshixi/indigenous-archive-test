package indigenousarchive;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;

public class ArchiveTask implements Callable<String[]> {
    private String url;
    private Archiver archiver;
    private ImageDownload downloader;
     

    public ArchiveTask(String url, Archiver archiver, ImageDownload downloader){
        this.url = url;
        this.archiver = archiver;
        this.downloader = downloader;
    }

    @Override
    public String[] call() {
        System.out.println("archiving... "+this.url);
        
        try {
            Thread.sleep(15000); // 15 second wait to avoid sending too many requrests
            downloader.downloadimg(this.url);
            String[] retstrarr = archiver.archiveurl(this.url);
            System.out.println(retstrarr[0]);
            if (Integer.parseInt(retstrarr[0])<=400){ // not a bad http return code
                archivewrite(retstrarr);
            }        
            // just some data to be sent back. i dont think i will need it tho.
            return retstrarr;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String[] retstrarr = {"none","none","none"};
        return retstrarr;
        
    }

    public void archivewrite(String[] archiveinfo){
        try {
            // write the archive info list with the http code, og url, new archived url to the json file 
            FileWriter writer = new FileWriter("archivedlinks.xml", true);
            String towrite = String.format("%s,%s,%s\n",archiveinfo[0],archiveinfo[1],archiveinfo[2].substring(10,archiveinfo[2].length()));
            writer.write(towrite);
            writer.close();
        }
        catch (IOException e){

        }

    }
    
}
