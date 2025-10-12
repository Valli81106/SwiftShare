// SimpleClient.java - Run this AFTER starting the server
import java.net.*;
import java.io.*;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            // connecting to the server at localhost:5000
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to server");

            // creating a way to send messages
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // sending a message
            out.println("Hello from client");
            System.out.println("Message sent");

            // cleanup
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}