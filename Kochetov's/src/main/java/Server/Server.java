package Server;

import Config.Config;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

  private List<ClientHandler> clients = new ArrayList<ClientHandler>();

  public Server() {
    Socket clientSocket = null;
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(Config.PORT);
      System.out.println("Server has been started!");
      while (true) {
        clientSocket = serverSocket.accept();
        ClientHandler client = new ClientHandler(clientSocket, this);
        clients.add(client);
        new Thread(client).start();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
        System.out.println("Server stopped");
        if (serverSocket != null) {
          serverSocket.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void sendMessageToAllClients(String msg) {
    for (ClientHandler client : clients) {
      client.sendMsg(msg);
    }
  }

  public void removeClient(ClientHandler client) {
    clients.remove(client);
  }

}