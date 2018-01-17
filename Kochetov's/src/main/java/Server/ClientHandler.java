package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler implements Runnable {

  private Server server;
  private PrintWriter outMessage;
  private Scanner inMessage;
  private Socket clientSocket = null;
  private static int clients_count = 0;

  public ClientHandler(Socket socket, Server server) {
    try {
      clients_count++;
      this.server = server;
      this.clientSocket = socket;
      this.outMessage = new PrintWriter(socket.getOutputStream());
      this.inMessage = new Scanner(socket.getInputStream());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }


  public void run() {
    try {
      while (true) {
        server.sendMessageToAllClients("Unknown user joined the chat");
        System.out.println("Unknown user joined the chat");
        server.sendMessageToAllClients("Online: " + clients_count);
        break;
      }

      while (true) {
        if (inMessage.hasNext()) {
          String clientMessage = inMessage.nextLine();
          String client = clientMessage.split(":")[0];
          if (clientMessage.contains("left the chat!")) {
            System.out.println(clientMessage);
            server.sendMessageToAllClients(clientMessage);
            break;
          }
          if (!client.split("#")[0].equals(client.split("#")[1])) {
            server.sendMessageToAllClients(
                "User " + client.split("#")[0] + " change name to " + client.split("#")[1]);
          }
          System.out
              .println("[" + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + "] "
                  + client.split("#")[1] + ":" + clientMessage.split(":")[1]);
          server.sendMessageToAllClients(
              "[" + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + "] "
                  + client.split("#")[1] + ":" + clientMessage.split(":")[1]);
        }
      }
    } finally {
      this.close();
    }
  }

  public void sendMsg(String msg) {
    try {
      outMessage.println(msg);
      outMessage.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void close() {
    server.removeClient(this);
    clients_count--;
    server.sendMessageToAllClients("Online: " + clients_count);
  }
}