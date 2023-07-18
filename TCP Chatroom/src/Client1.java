import java.io.*;
import java.net.Socket;

public class Client1{

    private int portNumber = 7777;
    private String hostName = "localhost";
    private Socket clientSocket;
    public static String clientName;

    public Client1(String clientName){
        this.clientName = clientName;
    }

    public static void main(String[] args) {

        Client1 client1 = new Client1("Pandora");
        client1.run();

    }


    public void run() {
        try {
            System.out.println("Waiting for connection...");
            clientSocket = new Socket(hostName, portNumber);
            System.out.println("Connection accepted");
            System.out.println("Connection from " + clientName);

            // Thread for writing
            Thread writeThread = new Thread(() -> {
                try {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        System.out.println("Write something...");
                        String sentence = reader.readLine();
                        out.println(sentence);
                        if (sentence.equals("quit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while writing: " + e.getMessage());
                }
            });
            writeThread.start();

            // Thread for reading
            Thread readThread = new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while (true) {
                        String request = in.readLine(); // blocking method
                        System.out.println(request);
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while reading: " + e.getMessage());
                }
            });

            // Start the threads

            readThread.start();


        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }



    public String getClientName(){
        return clientName;
    }

}
