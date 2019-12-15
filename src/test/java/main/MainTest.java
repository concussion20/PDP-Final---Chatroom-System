package main;

import client.Client;
import java.util.Scanner;
import org.junit.Test;
import server.Server;

@SuppressWarnings({"PMD"})
public class MainTest {

  @Test
  public void mainTest() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Server server = new Server();
        server.startServer();
      }
    }).start();

    //wait for server to start up
    try {
      Thread.sleep(1000 * 5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    new Thread(new Runnable() {
      @Override
      public void run() {
        String clientAInput = "xxx\n" + "logon userA\n" + "who\n" + "xxx\n" + "@userB 123 asd\n"
            + "@all 123 jjj\n" + "!userB\n" + "logoff\n";
        Scanner scanner = new Scanner(clientAInput);
        Client.startClient(scanner);
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        String clientBInput = "xxx\n" + "logon userB\n" + "who\n" + "xxx\n" + "@userA 123 asd\n"
            + "@all 123 jjj\n" + "!userA\n" + "logoff\n";
        Scanner scanner2 = new Scanner(clientBInput);
        Client.startClient(scanner2);
      }
    }).start();

    try {
      Thread.sleep(1000 * 20);
      // System.exit(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}