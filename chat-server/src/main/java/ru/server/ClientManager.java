package ru.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    String name;
    public static final ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату.");
        }
        catch (IOException e) {
            closeEverything(bufferedWriter, bufferedReader, socket);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (!messageFromClient.startsWith("@")) {
                    sendPersonalMessage(messageFromClient);
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(bufferedWriter, bufferedReader, socket);
                break;
            }
        }
    }

    private void broadcastMessage(String message) {
        try {
            for (ClientManager client : clients) {
                if (!client.name.equals(name)) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
        }
        catch (IOException e) {
            closeEverything(bufferedWriter, bufferedReader, socket);
        }

    }

    private void sendPersonalMessage(String message) {
        try {
            String[] splitMessage = message.split(" ");
            String userName = splitMessage[1];
            for (ClientManager client : clients) {
                if (client.name.equals(userName.substring(1))) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeEverything(BufferedWriter bufferedWriter, BufferedReader bufferedReader, Socket socket){
        removeClient();
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Server: " + name + " покинул чат.");
    }
}
