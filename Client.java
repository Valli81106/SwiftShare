// SimpleClient.java - Run this AFTER starting the server
import java.net.*;
import java.io.*;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            // Step 1: Connect to the server at localhost:5000
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to server!");

            // Step 2: Create a way to send messages
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Step 3: Send a message
            out.println("Hello from client!");
            System.out.println("Message sent!");

            // Step 4: Cleanup
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}