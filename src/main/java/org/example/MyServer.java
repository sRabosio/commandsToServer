package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

    private static ServerSocket socket;
    private static int portNumber = 1234;
    private static Calculator calc = new Calculator();

    public static void main(String[] args){



        //Server initialization
        socket = initServer();
        if(socket == null) return;
        waitClients();



    }

    //gets input String from a client and returns it capitalized to said client
    private static void capitalizeOp(BufferedReader reader, PrintWriter outToClient){
        String input;
        System.out.println("awaiting input...");
        try{
            while((input = reader.readLine()) != null){
                System.out.println(input);
                sendMSG(input.toUpperCase(), outToClient);
            }
        }catch (Exception e){
            System.out.println("comms endend with error");
        }
        System.out.println("comms endend normally");
    }

    private static void sendMSG(String message, PrintWriter outToClient){
            outToClient.println(message);
            System.out.println("--Sent back ("+message+")");
    }

    public static ServerSocket initServer(){
        System.out.println("----------------------------------------------------\nInitializing server...");
        ServerSocket s;
        //Server init
        try {
            s = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("no socket found");
            return null;
        }
        System.out.println("done\n--------------------------------");
        return s;
    }

    //reader gets input from a client
    public static BufferedReader initReader(Socket cs){
        //buffered reader
        BufferedReader in;
        try {
            in = new BufferedReader(
                    new InputStreamReader(cs.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("got input stream");
        return in;
    }

    //constantly waits for clients
    private static void waitClients(){
        System.out.println("waiting for clients...");
        for(;;){
            try {
                //Moves communication to a separate thread
                initSocket(socket.accept());
            } catch (IOException e) {
                System.out.println("-Failed to establish connection to client");
                return;
            }
        }
    }

    //initializes socket that establishes a connection with a client
    private static Socket initInSocket(){
        Socket s;
        try{
            s = socket.accept();
        }catch (Exception e){
            System.out.println("failed input socket");
            return null;
        }
        return s;
    }

    //Handles clients in a separate thread
    private static void initSocket(Socket fromServer){
        System.out.println("establishing connection...");
        new Thread(() -> {
            //connection initialization
            BufferedReader in = initReader(fromServer);
            if(in == null) return;
            PrintWriter outToClient = initOutStream(fromServer);
            if(outToClient == null) return;
            outToClient.println("\nConnection established!");

            userCommands(in, outToClient);

        }).start();
    }

    private static void userCommands(BufferedReader in, PrintWriter outToClient){
        outToClient.println("\n\n\n--------------------------------------------------" +
                "\nconnection established\n\nenter command...");

        for(;;){
            String commands = null;
            String[] args = null;
            try {
                commands = in.readLine();
            } catch (IOException e) {
                outToClient.println("Command error!\n\n");
            }
            if(commands == null) return;
            args = commands.split(" ");
            if(args[0] == null) continue;

            switch (args[0]){
                case "calc":
                    if(!hasMinArgs(args, 2, outToClient)) break;

                    double result = calc.getResult(args[1]);
                    outToClient.println(result);
                    break;

                case "cap":
                    if(!hasMinArgs(args, 2, outToClient)) break;

                    outToClient.println(
                            capitlizeOp(args));
                    break;

                default:
                    outToClient.println("Command doesn't exist\n");
                    break;
            }
        }
    }

    private static boolean hasMinArgs(String[] args, int min, PrintWriter outToClient){
        min--;
        if(args.length > min)
            return true;
        outToClient.println("-Not enough arguments!\n");
        return  false;
    }


    private static String capitlizeOp(String[] args){

        String result = "";
        for (int i = 1; i < args.length; i++) {
            result += args[i]+" ";
        }
        return result.toUpperCase();
    }

    //initializes output stream needed for answering to a client
    private static PrintWriter initOutStream(Socket s){
         PrintWriter w;
         try{
             w = new PrintWriter(s.getOutputStream(),true);
         }catch (Exception e){
             System.out.println("failed output stream");
             return null;
         }
        System.out.println("got output stream");
         return w;
    }
}
