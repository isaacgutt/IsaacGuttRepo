package edu.yu.cs.com3800.stage1;

import edu.yu.cs.com3800.SimpleServer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientImpl implements Client {

    String hostName;
    int hostPort;
    HttpURLConnection http;
    Response response;
    
    public ClientImpl(String hostName, int hostPort) throws MalformedURLException {
        this.hostName = hostName;
        this.hostPort = hostPort;

    }

    //Send the java source code string to the server
    @Override
    public void sendCompileAndRunRequest(String src) throws IOException {//Start http connectioon
        http = (HttpURLConnection) new URL("http", hostName, hostPort, "/compileandrun").openConnection();

        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "text/x-java-source");
        http.setDoOutput(true);

        //Send code to server
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.writeBytes(src);
            wr.flush();
        }

        //receive response
        int responseCode = http.getResponseCode();
        BufferedReader in;

        if(responseCode != 400)
            in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        else
            in = new BufferedReader(new InputStreamReader(http.getErrorStream()));

        String line;
        StringBuilder responseString = new StringBuilder();

        while ((line = in.readLine()) != null) {
            responseString.append(line);
            if(responseCode == 400){
                responseString.append("\n");
            }
        }

        response = new Response(responseCode, responseString.toString());


    }

    //Return it's response as an instance of client.response
    @Override
    public Response getResponse() throws IOException {

        return response;
    }
}
