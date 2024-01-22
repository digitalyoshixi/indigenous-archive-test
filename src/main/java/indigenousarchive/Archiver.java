package indigenousarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
	 * 	Just a barebones wrapper for some methods.
	 * @author	 David Chen
	 * @since 2024-1-21
	 * @version 1.1
*/
public class Archiver {
    
	/**
	 * Just a default constructor. dont send it anything werid
	 */
    public Archiver(){

    }

	/**
	 * Archive a given url in full. will return the String[] list of the http response code, orginial url and archived url location
	 * @param url original url you wanted to archive
	 * @return the String[] including: {HTTP respones code, original URl, archived URL}
	 * @throws IOException
	 */
    public String[] archiveurl(String url) throws IOException{
		// get current directory
		String pwd = System.getProperty("user.dir");
		// the archive command with curl
		String command = String.format("powershell %s\\curl8\\bin\\curl.exe -I  https://web.archive.org/save/%s",pwd,url);
		String[] retstrarr = new String[3]; // HTTP reponse, content type, location
		//System.out.println("powershell C:\\Users\\Digit\\Documents\\Java\\testing\\curl8\\bin\\curl.exe -I https://web.archive.org/save/https://ergonomictrends.com/20-20-20-rest-eyes-health-tool/");
		
		List<List<String>> curldata = commandtoss(command); // the returned stdout wrapped as a 2d string matrix	
		
		// save http response code
		String httpresponse = curldata.get(0).get(0).substring(7,10);
		retstrarr[0] = httpresponse; // http response code
		// original url
		retstrarr[1] = url; // og url
		// get location
		if (Integer.parseInt(httpresponse)>=400){ // valid http response
			retstrarr[2] = "none";
		}
		else {
			retstrarr[2] = curldata.get(0).get(5); // location 
		}
		
		
		return retstrarr;

	}

	/**
	 * sends any powershell command to a new process
	 * @param command any powershell command
	 * @return the 2D matrix of the response of the command
	 * @throws IOException
	 */
	public List<List<String>> commandtoss(String command) throws IOException{
		// returning strings
		List<List<String>> retstrarr = new ArrayList<List<String>>(); // 2d array to return
		List<String> stdoutstr = new ArrayList<String>(); // 1d array for stdout
		List<String> stderrstr = new ArrayList<String>(); // 1d array for stderr
		// execute the command we gave it
        Process powerShellProcess = Runtime.getRuntime().exec(command); // powershell runs a command
		powerShellProcess = Runtime.getRuntime().exec(command); // It fails sometimes. send 2 just in case
        powerShellProcess.getOutputStream().close(); // close powershell
		// ----------- get stdout and stderr messages ------------------
		String line; // line for buffered reader that says current line of output
		// ----------- STDOUT ------------
		//System.out.println("Sending Curl Request...");
		BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
		while ((line = stdout.readLine()) != null) { // go through each line of stdout
			stdoutstr.add(line); // add this line to standard out string
			//System.out.println(line);
		}
		stdout.close();
		// ----------- STDERR ------------
		BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
		while ((line = stderr.readLine()) != null) { // go through each line of stderr
			stderrstr.add(line); // add this line to standard error stirng
			//System.out.println(line);
		}
		stderr.close();
		// add the stdoutstr and stderrstr to the return list
		retstrarr.add(stdoutstr);
		retstrarr.add(stderrstr);
		//System.out.println("Done");
		return retstrarr;
		
    }


}
