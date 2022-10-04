package edu.yu.parallel;


import javax.swing.plaf.TableHeaderUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;

public class SimpleTradeServer {

    static ITradingAccount trading = new ITradingAccountImpl();


    public static void main(String[] args) {
        final int port = Integer.parseInt(System.getProperty("port", "8625"));
       // System.out.format("Starting server on port %d ...\n", port);

        try (
                ServerSocket serverSocket = new ServerSocket(port);
        ) {
            while(true) {
                //Prepare thread to run with the new client
                serverSocket.setReuseAddress(true);
                Socket clientSocket = serverSocket.accept();
                //System.out.printf("Welcome to our new client %s\n", clientSocket.getInetAddress().getHostAddress());
                IsiClientHandler clientHandler = new IsiClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }


        } catch (IOException e) {
            //System.out.println("Error starting server: " + e.getMessage());
        }

    }

    private static boolean isValidDouble(String str) {
        try {
            double a = Double.parseDouble(str);

            return !(a <= 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private static class IsiClientHandler implements Runnable {
        private Socket clientSocket;
        String outputLine;
        String inputLine;


        public IsiClientHandler(Socket client){
            this.clientSocket = client;
        }

        @Override
        public void run() {

            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {

                //read message from client and trim in case of whitespaces
                while ((inputLine = in.readLine()) != null) {
                    inputLine = inputLine.replaceAll("^\\s+", "");
                    String[] input = inputLine.split("\\s+");
                    //System.out.println(Arrays.toString(input));
                    double before;

                    //Set id string for printing
                    String id = String.format("[%s:%d:%s]",Thread.currentThread().getName(),
                            Thread.currentThread().getId(),clientSocket.getLocalPort());

                    //All the edge cases
                    //Input must be one or two, buys and sells must be 2, balance and exit must be 1
                    if (input.length >= 3 || (input[0].equalsIgnoreCase("BALANCES") || input[0].equalsIgnoreCase("EXIT")) && input.length != 1
                            || (input[0].equalsIgnoreCase("BUY") || input[0].equalsIgnoreCase("SELL")) && (input.length != 2 || !isValidDouble(input[1]))) {
                        outputLine = String.format("%s,Unhandled Input", id);
                        out.println(outputLine);
                        continue;
                    }
                    input[0] = input[0].toUpperCase(Locale.ROOT);

                    switch (input[0]) {
                        case "BUY":
                            before = trading.getPositionBalance();
                            trading.Buy(Double.parseDouble(input[1]));
                            outputLine = String.format("%s,PrevPos=%1.2f,BUY=%1.2f,NewPos=%1.2f",id, before, Double.parseDouble(input[1]), trading.getPositionBalance());
                            break;
                        case "SELL":
                            before = trading.getPositionBalance();
                            trading.Sell(Double.parseDouble(input[1]));
                            outputLine = String.format("%s,PrevPos=%1.2f,Sell=%1.2f,NewPos=%1.2f",id, before, Double.parseDouble(input[1]), trading.getPositionBalance());
                            break;
                        case "BALANCES":
                            outputLine = String.format("%s,%s",id,trading.getBalanceString());
                            break;
                        case "EXIT":
                            outputLine = String.format("%s,Goodbye!", id);
                            break;

                        default:
                            outputLine = String.format("%s,Unhandled Input", id);

                    }
                    //send message back to client
                    out.println(outputLine);
                    if (input[0].equals("EXIT")) {
                        clientSocket.close();
                        break;
                    }
                }


            } catch (IOException e) {
                System.out.println("Error starting server: " + e.getMessage());
            }
        }
    }
}