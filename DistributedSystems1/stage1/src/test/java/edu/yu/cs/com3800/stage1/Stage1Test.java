package edu.yu.cs.com3800.stage1;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.SimpleServer;
import org.junit.Test;

import static edu.yu.cs.com3800.stage1.Client.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Stage1Test {

@Test
    public void testWorks() throws IOException{
    String java = "package edu.yu.cs.com3800; public class HelloWorld { public HelloWorld(){ } public String run(){  return \"It. Has. Worked.\"; }}";
    Client client = new ClientImpl("localhost", 8000);
    SimpleServer simpleServer = new SimpleServerImpl(8000);
    simpleServer.start();
    client.sendCompileAndRunRequest(java);
    Response response = client.getResponse();
    System.out.printf("Expected response:\n%s: %s\n", 200, "It. Has. Worked.");
    System.out.printf("Actual response:\n%s: %s\n", response.getCode(), response.getBody());
    assertEquals(response.getBody(), "It. Has. Worked.");
    assertEquals(200, response.getCode());

    }

    @Test
    public void testNotWorking() throws IOException{

        String java = "package edu.yu.cs.com3800; public class HelloWorld { public HelloWorld(){ } public Sring run(){  return \"It. Has. Worked.\"; }}";
        Client client = new ClientImpl("localhost", 9000);
        SimpleServer simpleServer = new SimpleServerImpl(9000);
        simpleServer.start();
        client.sendCompileAndRunRequest(java);
        Response response = client.getResponse();
        System.out.printf("Expected response:\n%s: %s\n", 400, "Code did not compile:\n");
        System.out.printf("Actual response:\n%s: %s\n", response.getCode(), response.getBody());
        assertEquals(400, response.getCode());
    }

}
