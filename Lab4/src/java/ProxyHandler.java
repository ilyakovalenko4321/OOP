import java.io.*;
import java.net.*;

public class ProxyHandler implements Runnable {
    private final Socket clientSocket;

    public ProxyHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (InputStream clientIn = clientSocket.getInputStream();
             OutputStream clientOut = clientSocket.getOutputStream()) {


            BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] parts = requestLine.split(" ");
            if (parts.length < 3) return;
            String method = parts[0];
            String fullUrl = parts[1];
            String version = parts[2];


            URI uri = new URI(fullUrl);
            String host = uri.getHost();
            int port = (uri.getPort() == -1) ? 80 : uri.getPort();
            String path = (uri.getPath() == null || uri.getPath().isEmpty()) ? "/" : uri.getPath();
            if (uri.getQuery() != null) path += "?" + uri.getQuery();

            try (Socket serverSocket = new Socket(host, port);
                 InputStream serverIn = serverSocket.getInputStream();
                 OutputStream serverOut = serverSocket.getOutputStream()) {

                String newRequestLine = String.format("%s %s %s\r\n", method, path, version);
                serverOut.write(newRequestLine.getBytes());

                String header;
                while (!(header = reader.readLine()).isEmpty()) {

                    if (!header.toLowerCase().startsWith("proxy-connection")) {
                        serverOut.write((header + "\r\n").getBytes());
                    }
                }
                serverOut.write("Connection: close\r\n\r\n".getBytes());
                serverOut.flush();


                System.out.print(method + " " + fullUrl + " -> ");


                String responseStatus = readLine(serverIn);
                System.out.println(responseStatus);
                clientOut.write((responseStatus + "\r\n").getBytes());


                transferData(serverIn, clientOut);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }


    private String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') break;
            if (b != '\r') baos.write(b);
        }
        return baos.toString();
    }

    private void transferData(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[16384];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                out.flush();
            }
        } catch (IOException ignored) {}
    }
}