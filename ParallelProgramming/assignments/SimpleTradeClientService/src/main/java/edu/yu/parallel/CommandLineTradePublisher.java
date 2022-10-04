package edu.yu.parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandLineTradePublisher {

    public static void main(String[] args) {
        final String host = System.getProperty("host", "localhost");
        final int port = Integer.parseInt(System.getProperty("port", "8625"));
        Scanner stdIn  = new Scanner(System.in);

        System.out.format("Connecting to server on port %d ...", port);

        //connect to server
        try (Socket kkSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()))) {
            System.out.println(" Done");

            //send request to server
            out.println(stdIn.nextLine());

            String input;
            String fromUser;
            //read response from server and print to std out
            while ((input = in.readLine()) != null) {
                System.out.println(input);
                if (input.split(",")[1].equals("Goodbye!"))
                    break;


                fromUser = stdIn.nextLine();
                if (fromUser != null) {
                    out.println(fromUser);
                }
            }

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
        catch (NoSuchElementException e)
        {
            System.out.println("No more command line input");
        }
    }
}
