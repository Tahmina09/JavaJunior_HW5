package ru.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer(){
        while(!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientManager clientManager = new ClientManager(socket);
                System.out.println("Новый клиент подключён!");
                Thread thread = new Thread(clientManager);
                thread.start();
            } catch (IOException e) {
                closeServer();
            }
        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
