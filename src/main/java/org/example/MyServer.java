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

    public static void main(String[] args){



        //Server initialization
        socket = initServer();
        if(socket == null) return;
        waitClients();



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



    //constantly waits for clients and gives them a handler
    private static void waitClients(){
        System.out.println("waiting for clients...");
        for(;;){
            try {
                //Moves communication to a separate thread
                new ClientHandler(socket.accept());
            } catch (IOException e) {
                System.out.println("-Failed to establish connection to client");
                return;
            }
        }
    }


}
