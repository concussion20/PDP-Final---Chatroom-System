package po;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"PMD"})
public class MessageTypeTest {

  @Test
  public void valueOf() {
    Assert.assertEquals(MessageType.CONNECT_MESSAGE, MessageType.valueOf(19));
    Assert.assertEquals(MessageType.CONNECT_RESPONSE, MessageType.valueOf(20));
    Assert.assertEquals(MessageType.DISCONNECT_MESSAGE, MessageType.valueOf(21));
    Assert.assertEquals(MessageType.DISCONNECT_RESPONSE, MessageType.valueOf(22));
    Assert.assertEquals(MessageType.QUERY_CONNECTED_USERS, MessageType.valueOf(23));
    Assert.assertEquals(MessageType.QUERY_USER_RESPONSE, MessageType.valueOf(24));
    Assert.assertEquals(MessageType.BROADCAST_MESSAGE, MessageType.valueOf(25));
    Assert.assertEquals(MessageType.DIRECT_MESSAGE, MessageType.valueOf(26));
    Assert.assertEquals(MessageType.FAILED_MESSAGE, MessageType.valueOf(27));
    Assert.assertEquals(MessageType.SEND_INSULT, MessageType.valueOf(28));
  }
}