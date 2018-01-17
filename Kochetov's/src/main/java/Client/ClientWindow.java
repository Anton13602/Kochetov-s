package Client;

import Config.Config;
import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ClientWindow extends JFrame {

  private Socket clientSocket;
  private Scanner inMessage;
  private PrintWriter outMessage;
  private JTextField jtfMessage;
  private JTextField jtfName;
  private JTextArea jtaTextAreaMessage;
  private JLabel jlNumberOfClients;
  private String clientName = "";
  private String lastClientName = "Unknown";

  public ClientWindow() {
    try {
      clientSocket = new Socket(Config.IP, Config.PORT);
      inMessage = new Scanner(clientSocket.getInputStream());
      outMessage = new PrintWriter(clientSocket.getOutputStream());
    } catch (IOException e) {
      System.out.println("Cannot connect");
    }
    setBounds(600, 300, 600, 500);
    setTitle("Client");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    jtaTextAreaMessage = new JTextArea();
    jtaTextAreaMessage.setEditable(false);
    jtaTextAreaMessage.setLineWrap(true);
    JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
    add(jsp, BorderLayout.CENTER);
    jlNumberOfClients = new JLabel("Online: ");
    add(jlNumberOfClients, BorderLayout.NORTH);
    JPanel bottomPanel = new JPanel(new BorderLayout());
    add(bottomPanel, BorderLayout.SOUTH);
    JButton jbSendMessage = new JButton("Send");
    bottomPanel.add(jbSendMessage, BorderLayout.EAST);
    jtfMessage = new JTextField("Enter your message");
    bottomPanel.add(jtfMessage, BorderLayout.CENTER);
    jtfName = new JTextField("Enter your name");
    bottomPanel.add(jtfName, BorderLayout.WEST);
    jbSendMessage.addActionListener(event -> {
      if (!jtfMessage.getText().trim().equals("") && !jtfName.getText().trim().equals("")
          && !jtfMessage.getText().trim().equals("Enter your message") && !jtfName.getText()
          .trim().equals("Enter your name")) {
        clientName = jtfName.getText();
        String messageStr =
            lastClientName + "#" + jtfName.getText() + ": " + jtfMessage.getText();
        lastClientName = jtfName.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
        jtfMessage.grabFocus();
      }
    });
    jtfMessage.addFocusListener(new FocusAdapter() {
      //@Override
      public void focusGained(FocusEvent e) {
        jtfMessage.setText("");
      }
    });
    jtfName.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        jtfName.setText("");
      }
    });
    new Thread(() -> {
      try {
        while (true) {
          if (inMessage.hasNext()) {
            String inMes = inMessage.nextLine();
            String clientsInChat = "Online: ";
            if (inMes.indexOf(clientsInChat) == 0) {
              jlNumberOfClients.setText(inMes);
            } else {
              jtaTextAreaMessage.append(inMes);
              jtaTextAreaMessage.append("\n");
            }
          }
        }
      } catch (Exception e) {
      }
    }).start();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        try {
          if (!clientName.equals("")) {
            outMessage.println(clientName + " left the chat!");
          } else {
            outMessage.println("Unknown user left the chat!");
          }
          outMessage.flush();
          outMessage.close();
          inMessage.close();
          clientSocket.close();
        } catch (IOException exc) {
        }
      }
    });
    setVisible(true);
  }
}