package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    private static int portNumber = 1234;
    private static String hostName ="127.0.0.1";

    public static void main( String[] args )
    {

        double r = calculatorSplitter("5+5*10+8*2-10");
        System.out.println(r);

    }


    //risolve le espressioni e ne ritorna il risultato
    private static double calculatorSplitter(String clientIn){
        boolean hasOperation = false;
        String[] opList = {"+", "-", "*", "/"};
        ArrayList<String> expression = new ArrayList<>();
        int smallerIndex = -1;
        String currentOp = null;

        for(;;){

            //gets index of nearest op
            for(int c = 0; c < opList.length; c++){
                int temp = clientIn.indexOf(opList[c]);
                if(smallerIndex == -1){
                    smallerIndex = temp;
                    currentOp = opList[c];
                }
                if(temp > 0 && temp < smallerIndex){
                    smallerIndex = temp;
                    currentOp = opList[c];
                }
            }


            if(smallerIndex > 0){

                //calcolo le parentesi per prime
                /*if(currentOp.equals("(")){
                    int bracketIndex = smallerIndex;
                    int closeBracketIndex;
                    String tempExp = clientIn.substring(bracketIndex);
                    bracketIndex = tempExp.indexOf("(");
                    closeBracketIndex = tempExp.indexOf(")");

                    if(closeBracketIndex < 0) break;
                    if(bracketIndex > 0 && bracketIndex < closeBracketIndex){

                    }


                }*/

                hasOperation = true;

                //rebuilding input as expression to solve later
                expression.add(
                        clientIn.substring(0,smallerIndex));

                expression.add(currentOp);

                clientIn = clientIn.substring(smallerIndex+1);
                currentOp = null;
                smallerIndex = -1;
            }
            else{
                expression.add(clientIn);
                break;
            }
        }
        if(!hasOperation) return 0;

        boolean d = expression.contains("*");

        if(expression.contains("*")) expression = multipy(expression);
        if(expression.contains("/")) expression = divide(expression);
        double result = calc(expression);
        return result;
    }

    private static double calc(ArrayList<String> expression){
        double result = Double.parseDouble(
                expression.get(0)
        );
        //boolean exit = false;



        for(int i = 1; i < expression.size() ; i+=2){

            //if(i >= expression.size()) exit = true;

            switch (expression.get(i)){
                case "+":
                    result += Double.parseDouble(
                            expression.get(i+1)
                    );
                    break;
                case "-":
                    result -= Double.parseDouble(
                            expression.get(i+1)
                    );
                    break;
            }
        }

        return result;
    }

    private static ArrayList<String> multipy(ArrayList<String> expression){
        double result, op1, op2;
        ArrayList<String> finalArray = new ArrayList<>();

        for(int i = 1; i < expression.size(); i+=2){
            if(expression.get(i).equals("*")){

                //calculation
                op1 = Integer.parseInt(
                        expression.get(i-1));
                op2 = Integer.parseInt(
                        expression.get(i+1));
                result = op1*op2;

                //adding result to array
                finalArray.add(
                        String.valueOf(result));

                i+=2;

                finalArray.add(
                        expression.get(i));
            }
            else{
                //adds previus number and operator to finalArray
                finalArray.add(
                        expression.get(i-1));
                finalArray.add(
                        expression.get(i));
            }
        }
        finalArray.add(
                expression.get(expression.size()-1));
        return finalArray;
    }

    private static ArrayList<String> divide(ArrayList<String> expression){
        double result, op1, op2;
        ArrayList<String> finalArray = new ArrayList<>();

        for(int i = 1; i < expression.size(); i+=2){
            if(expression.get(i).equals("/")){

                //calculation
                op1 = Integer.parseInt(
                        expression.get(i-1));
                op2 = Integer.parseInt(
                        expression.get(i+1));
                result = op1/op2;

                //adding result to array
                finalArray.add(
                        String.valueOf(result));

                i+=2;

                finalArray.add(
                        expression.get(i));
            }
            else{
                //adds previus number and operator to finalArray
                finalArray.add(
                        expression.get(i-1));
                finalArray.add(
                        expression.get(i));
            }
        }
        finalArray.add(
                expression.get(expression.size()-1));
        return finalArray;
    }
}
