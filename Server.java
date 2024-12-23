import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try {
            // port number
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the port number: ");
            int port = scanner.nextInt();
            scanner.close();

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");
                // Spawn a new thread to handle the request
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to run system commands based on client request
    private static String runCommand(String command) {
        StringBuilder result = new StringBuilder();
        try {
            switch (command) {
                case "1":
                    result.append("Date and Time: ").append(new Date()).append("\n");
                    break;
                case "2":
                    Process uptimeProcess = Runtime.getRuntime().exec("uptime");
                    BufferedReader uptimeReader = new BufferedReader(new InputStreamReader(uptimeProcess.getInputStream()));
                    String line;
                    while ((line = uptimeReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    break;
                case "3":
                    Process memoryProcess = Runtime.getRuntime().exec("free -h");
                    BufferedReader memoryReader = new BufferedReader(new InputStreamReader(memoryProcess.getInputStream()));
                    while ((line = memoryReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    break;
                case "4":
                    Process netstatProcess = Runtime.getRuntime().exec("netstat");
                    BufferedReader netstatReader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
                    while ((line = netstatReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    break;
                case "5":
                    Process usersProcess = Runtime.getRuntime().exec("who");
                    BufferedReader usersReader = new BufferedReader(new InputStreamReader(usersProcess.getInputStream()));
                    while ((line = usersReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    break;
                case "6":
                    Process processesProcess = Runtime.getRuntime().exec("ps -ef");
                    BufferedReader processesReader = new BufferedReader(new InputStreamReader(processesProcess.getInputStream()));
                    while ((line = processesReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    break;
                default:
                    result.append("Invalid command");
            }
        } catch (IOException e) {
            result.append("Error running command: ").append(e.getMessage()).append("\n");
        }
        return result.toString();
    }

    // A runnable class to handle each client connection in a separate thread
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String command = input.readLine();
                String result = runCommand(command);
                // Send the entire result in one go, unchanged
                output.println(result);
            } catch (IOException e) {
                System.out.println("Client handler error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // Ignore on close
                }
            }
        }
    }
}