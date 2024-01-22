package indigenousarchive;

import processing.core.PApplet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
	 * Main metod just reroutes to Sketch.java
	 * @author	 David Chen
	 * @since 2024-1-21
	 * @version 1.1
*/
class Main {
  
  /**
   * main method simply sets the PApplet canvas inside Sketch.java
   * @param args For the arguments provided in command line
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public static void main(String[] args) throws IOException,NoSuchAlgorithmException, InterruptedException, ExecutionException {
    Sketch.main("indigenousarchive.Sketch");
    

  }


  

}