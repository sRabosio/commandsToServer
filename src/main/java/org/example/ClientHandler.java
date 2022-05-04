package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

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

    private boolean hasMinArgs(String[] args, int min, PrintWriter outToClient){
        min--;
        if(args.length > min)
            return true;
        outToClient.println("-Not enough arguments!\n");
        return  false;
    }


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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
