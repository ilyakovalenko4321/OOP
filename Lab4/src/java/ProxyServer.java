import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {
    public static void main(String[] args) {
        int port = 8888;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HTTP Proxy Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ProxyHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            e.printStackTrace();
        }
    }
}