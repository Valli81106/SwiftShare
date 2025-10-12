import java.net.*;
import java.io.*;

public class SimpleServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Server waiting on port 5000.");

            Socket client = server.accept();
            System.out.println("Someone connected");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            String msg = in.readLine();
            System.out.println("Got message: " + msg);

            client.close();
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}