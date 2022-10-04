package edu.yu.cs.com3800.stage1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.SimpleServer;
import com.sun.net.httpserver.HttpHandler;
import java.util.logging.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class SimpleServerImpl implements SimpleServer {


    HttpServer myserver;


    public SimpleServerImpl(int port) throws IOException {

        myserver = HttpServer.create(new InetSocketAddress(port), 0);
        myserver.createContext("/compileandrun", new IsiHandler());
        myserver.setExecutor(null); // creates a default executor

    }

    public static void main(String[] args)
    {
        int port = 9000;
        if(args.length >0)
        {
            port = Integer.parseInt(args[0]);
        }
        SimpleServer myserver = null;
        try
        {
            myserver = new SimpleServerImpl(port);
            myserver.start();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            myserver.stop();
        }
    }


    /**
     * start the server
     */
    @Override
    public void start() {
        myserver.start();
    }

    /**
     * stop the server
     */
    @Override
    public void stop() {
        myserver.stop(3);
    }


}

class IsiHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Logger logger = Logger.getLogger("ServerLogger");
        File path = new File("logs");
        path.mkdir();
        FileHandler fh = new FileHandler("logs/logs.log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);


        logger.info("Entered the handler.");
        //Make sure the content type is good before it throws an exception
        if(!t.getRequestHeaders().get("Content-Type").get(0).equalsIgnoreCase("text/x-java-source")){
            String response = "Wrong content type";
            t.sendResponseHeaders(400, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }
        InputStream is = t.getRequestBody();
        JavaRunner jr = new JavaRunner();
        String response = "";

        try {
            response = jr.compileAndRun(is);

        }
        //Send back error message
        catch (Exception e) {
            logger.info("Compile failed.");
            OutputStream os = t.getResponseBody();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            response = e.getMessage() + "\n" + ps.toString();
            t.sendResponseHeaders(400, response.length());
            os.write(response.getBytes());
            os.close();

            return;
        }
        logger.info("Compile succeeded.");

        //Send back message prnted by run ethod
        t.sendResponseHeaders(200, response.length());
        logger.info("Received response.");
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

