package indigenousarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Archiver {
    
    public Archiver(){

    }

    public String[] archiveurl(String url) throws IOException{
		String pwd = System.getProperty("user.dir");
		String command = String.format("powershell %s\\curl8\\bin\\curl.exe -I  https://web.archive.org/save/%s",pwd,url);
		String[] retstrarr = new String[3]; // HTTP reponse, content type, location
		//System.out.println("powershell C:\\Users\\Digit\\Documents\\Java\\testing\\curl8\\bin\\curl.exe -I https://web.archive.org/save/https://ergonomictrends.com/20-20-20-rest-eyes-health-tool/");
		
		List<List<String>> curldata = commandtoss(command);
		
		// System.out.println("STDOUT:");
		// for (String i : curldata.get(0)){ // for every stdout
		// 	System.out.println(i);
		// }
		// System.out.println("STDERR:");
		// for (String i : curldata.get(1)){ // for every stderr
		// 	System.out.println(i);
		// }
		
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

	// return a 2d list
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
