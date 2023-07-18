import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server{

    //Lista para guardar os serverWorkers (novas ligações ao port)
    private List<ServerWorker> serverWorkersList;
    private int portNumber = 7777;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        Server server = new Server();
        server.work();
    }

    public void sendAll(String message)  {

        try {

            //serverWorkersList.forEach(serverWorker -> out.println(message));
            for(ServerWorker worker : serverWorkersList){
                in = new BufferedReader(new InputStreamReader(worker.clientSocket.getInputStream()));
                out = new PrintWriter(worker.clientSocket.getOutputStream(), true);
                out.println(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void work() {

        serverWorkersList = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true){
            System.out.println("Server waiting for a connection...");
            try {

                clientSocket = serverSocket.accept();
                System.out.println("A client is connected!");

                ServerWorker sw = new ServerWorker(clientSocket, this);
                serverWorkersList.add(sw);
                System.out.println("A server worker was saved in the list");

                Thread swThread = new Thread(sw);
                swThread.start();
                System.out.println("New ServerWorker Thread is active");

            } catch (IOException e) {
                System.out.println("Not working so good like you expected!");
            }

        }
    }

    public class ServerWorker implements Runnable{
        private Socket clientSocket;


        private BufferedReader in;
        private PrintWriter out;
        private String clientMessage;
        private Server server;

        public ServerWorker(Socket clientSocket, Server server) {
            this.clientSocket = clientSocket;
            this.server = server;
        }

        @Override
        public void run() {

            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

            //serverWorker
                while(true) {

                    System.out.println("Server Worker is trying to read/send message");

                    // lê a mensagem
                    clientMessage = in.readLine();

                    //vai enviar a mensagem para todos
                    //serverWorkersList.forEach(serverWorker -> out.println(clientMessage));
                    server.sendAll(clientMessage);
                    System.out.println(clientMessage);
                    System.out.println("Server worker has sent a message.");

                }

                } catch (IOException e) {
                System.out.println("Server Worker could not read/send message");
                throw new RuntimeException(e);

            }
        }
    }

}
