package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import po.Message;
import po.MessageType;

@SuppressWarnings({"PMD"})
public class ProtocolHelperTest {

  @Test
  public void testProtocolHelper() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          ServerSocket serverSocket = new ServerSocket(8888);
          Socket socket = serverSocket.accept();
          BufferedReader reader = new BufferedReader(new
              InputStreamReader(socket.getInputStream()));

          while (true) {
            Message message = ProtocolHelper.generateMessageObj(reader);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
    Socket socket = null;
    try {
      socket = new Socket("127.0.0.1", 8888);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      char[] messageChars;
      Message message = new Message();
      message.setType(MessageType.BROADCAST_MESSAGE);
      message.setFrom("userA");
      String msg = "I'm A";
      message.setMsg(msg);
      messageChars = ProtocolHelper.generateFrame(message);
      writer.write(new String(messageChars));

      Message message2 = new Message();
      message2.setType(MessageType.SEND_INSULT);
      message2.setFrom("userB");
      message2.setTo("userA");
      messageChars = ProtocolHelper.generateFrame(message2);
      writer.write(new String(messageChars));

      Message message3 = new Message();
      message3.setType(MessageType.DIRECT_MESSAGE);
      message3.setFrom("userC");
      message3.setTo("userA");
      String msg3 = "Hi this is userC";
      message3.setMsg(msg3);
      messageChars = ProtocolHelper.generateFrame(message3);
      writer.write(new String(messageChars));

      Message message4 = new Message();
      message4.setType(MessageType.QUERY_USER_RESPONSE);
      message4.setTo("userA");
      List<String> userList = Arrays.asList("userB", "userC");
      message4.setUserList(userList);
      messageChars = ProtocolHelper.generateFrame(message4);
      writer.write(new String(messageChars));

      Message message5 = new Message();
      message5.setType(MessageType.FAILED_MESSAGE);
      message5.setMsg("I failed");
      messageChars = ProtocolHelper.generateFrame(message5);
      writer.write(new String(messageChars));
      writer.flush();

      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}