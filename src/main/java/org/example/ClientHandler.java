package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final String line = "---------------------------------------------------------";
    private Socket socket;
    private static Calculator calc = new Calculator();

    public ClientHandler(Socket socket){
        setSocket(socket);
        run();
    }

    @Override
    public void run() {
        System.out.println("establishing connection...");

        //connection initialization
        BufferedReader in = initReader(socket);
        if(in == null) return;
        PrintWriter outToClient = initOutStream(socket);
        if(outToClient == null) return;
        outToClient.println("\nConnection established!");

        //Handling client
        userCommands(in, outToClient);
    }

    //reader gets input from a client
    public BufferedReader initReader(Socket cs){
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

    //checks if command has enough arguments
    private boolean hasMinArgs(String[] args, int min, PrintWriter outToClient, boolean sendResponseToClient){
        min--;
        if(args.length > min)
            return true;

        if(sendResponseToClient) outToClient.println("-Not enough arguments!\n");

        return  false;
    }


    //returns an upper case string
    private String capitlizeOp(String[] args){

        String result = "";
        for (int i = 1; i < args.length; i++) {
            result += args[i]+" ";
        }
        return result.toUpperCase();
    }

    //initializes output stream needed for answering to a client
    private PrintWriter initOutStream(Socket s){
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

    private void userCommands(BufferedReader in, PrintWriter outToClient){
        outToClient.println("\n\n\n" +line+
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
                    if(!hasMinArgs(args, 2, outToClient, true)) break;

                    double result = calc.getResult(args[1]);
                    outToClient.println(result);
                    break;

                case "cap":
                    if(!hasMinArgs(args, 2, outToClient, true)) break;

                    outToClient.println(
                            capitlizeOp(args));
                    break;

                case "exit", "quit", "q":
                    //prevents closing connection when writing more than 1 argument
                    if(hasMinArgs(args, 2, outToClient, false)) break;

                    outToClient.println(line+"\n\n\n\n\n Connection closed");
                    closeConnection();

                default:
                    outToClient.println("Command doesn't exist\n");
                    break;
            }
        }
    }

    public void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error when closing");
            return;
        }

        System.out.println("closed connection with: "+ socket.getInetAddress());
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
