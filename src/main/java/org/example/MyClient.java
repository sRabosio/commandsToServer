package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClient {

    private static String hostName = "127.0.0.1";
    private static int portNumber = 1234;
    //reads from user
    private static BufferedReader stdIn;
    //reads from client
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket echoSocket;


    public static void main(String[] args){
        useArgs(args);
        initClient();

        try {
            String userInput;
            inFromServer(in);
            while ((userInput = stdIn.readLine()) != null) {
                //prints line to server
                out.println(userInput);
            }
        }catch (Exception e){
            System.out.println("CONNECTION ERROR");
            System.exit(1);
        }
    }

    private static void initClient(){
        //Client init
        try{
            echoSocket = new Socket(hostName, portNumber);        // 1st statement
            out =                                            // 2nd statement
                    new PrintWriter(echoSocket.getOutputStream(), true);
            in =                                          // 3rd statement
                    new BufferedReader(
                            new InputStreamReader(echoSocket.getInputStream()));
            stdIn =                                       // 4th statement
                    new BufferedReader(
                            new InputStreamReader(System.in));
        }catch(Exception e){
            System.out.println("inizializzazione client non riuscita");
            System.exit(1);
        }
    }

    private static void useArgs(String[] args){
        int argc = args.length;
        switch(argc){
            case 1:
                //checks if we have a port or an ip
                if(args[0].contains(".")){
                    hostName = args[0];
                    return;
                }
                portNumber = Integer.parseInt(args[0]);
                return;

            case 2:
                setBothArgs(args);
                return;

            default:
                if(args.length == 0)
                    return;
                System.out.println("-ILLEGAL NUMBER OF ARGUMENS");
                System.exit(1);
        }
    }

    private static void setBothArgs(String[] args){
        boolean isArgIp0 = args[0].contains(".");
        boolean isArgIp1 = args[1].contains(".");

        if(isArgIp1 && isArgIp0){
            System.out.println("-ILLEGAL ARGUMENTS: invalid port number");
            System.exit(1);
        }

        if(!isArgIp1 && !isArgIp0){
            System.out.println("-ILLEGAL ARGUMENTS: invalid ip address");
            System.exit(1);
        }

        if(isArgIp0){
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }
        else{
            hostName = args[1];
            portNumber = Integer.parseInt(args[0]);
        }

    }

    private static void inFromServer(final BufferedReader in){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //awaits input from server
                    for(String msg; (msg = in.readLine()) != null;){
                        System.out.println(msg);
                    }
                } catch (Exception e) {
                    System.exit(0);
                }
            }
        }).start();
    }
}
