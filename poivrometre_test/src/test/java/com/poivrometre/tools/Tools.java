package com.poivrometre.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tools {
	
	/**
	 * 
	 * @param url URL du WebService
	 * @param input message à envoyé au WebService
	 * @return Le résultat renvoyé par le WebService
	 * @throws IOException
	 */
	public static String appelWebservice(URL url, String input) throws IOException {
		HttpURLConnection connection;
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		
		OutputStream os = connection.getOutputStream();
        os.write(input.getBytes());
        
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        String result = br.readLine();
        connection.disconnect();
        
        return result;
	}

}
