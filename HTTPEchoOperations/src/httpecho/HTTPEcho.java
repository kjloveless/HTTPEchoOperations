/**
 * Kyle Loveless
 * CSC 415
 * Dr. Muganda
 * 
 * This program is intended to read in information from a webpage post and 
 * write out an operation the input dictated by the post.
 */

package httpecho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class HTTPEcho
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSock = new ServerSocket(50002);
        while (true)
        {
            Socket sock = serverSock.accept();
            System.err.print("\n");
            System.err.println("Received connection request");
            http_echo(sock);
        }
    }
    
    static public void http_echo(Socket sock) throws IOException
    {
       BufferedReader in = new BufferedReader
                      (new InputStreamReader(sock.getInputStream())) ;
       PrintWriter out = new  PrintWriter(sock.getOutputStream(), true);   
       StringBuilder requestBodyString = new StringBuilder();
    
       
       //prepare to return response to the browser
       out.println("HTTP/1.1 200 OK");    
       out.println("Content-Type: text/plain\n");
       
       String line = in.readLine();
       
       // Work around to prevent crash when browser sends a 
       // bogus request.
       if (line == null || line.startsWith("GET /favicon.ico"))
       {
           in.close(); out.close(); sock.close();
           return;
       }
       // End workaround
       
       // Process HTTP request line and  request header lines
       while (!line.isEmpty())
       {
           out.println(line);
           System.err.println(line);
           line = in.readLine();
       }
       out.println(line);
       System.err.println();
       
       // Read the body, if present
       while (in.ready())
       {    
           int ch = in.read();
           requestBodyString.append((char)ch);
           System.err.print((char)ch);
           out.print((char)ch);           
       } 
       System.err.print("\n");
       out.print("\n" + calculation(requestBodyString));
       out.flush();
       in.close();
       out.close();
       sock.close();       
    }
    
    //used to perform the calculation of the operation request
    static private String calculation(StringBuilder calculationString)
    {
        String[] splitArray = calculationString.toString().split("&");
        String[] tempArray;
        HashMap<String, String> operationMap = new HashMap();
        double doubleAnswer = 0.0;
        for (String subStr : splitArray) {
            tempArray = subStr.split("=");
            operationMap.put(tempArray[0], tempArray[1]);
        }
        
        String operation = operationMap.get("operation");
        
        switch(operation) 
        {
            case "sum" :    doubleAnswer = (Double.parseDouble(operationMap.get
                            ("x")) + Double.parseDouble(operationMap.get("y")));
                            break;
            
            case "prod" :   doubleAnswer = (Double.parseDouble(operationMap.get
                            ("x")) * Double.parseDouble(operationMap.get("y")));
                            break;
                            
            case "quotient" :   doubleAnswer = (Double.parseDouble(operationMap.
                         get("x")) / Double.parseDouble(operationMap.get("y")));
                                break;
            case "diff" :   doubleAnswer = (Double.parseDouble(operationMap.get
                            ("x")) - Double.parseDouble(operationMap.get("y")));
                            break;
            case "square" :   doubleAnswer = (Double.parseDouble(operationMap.
                         get("x")) * Double.parseDouble(operationMap.get("x")));
                              break;
            
        }
        return ("The " + operationMap.get("operation") + " of " + operationMap.
            get("x") + " and " + operationMap.get("y") + " is " + doubleAnswer);
    }
}