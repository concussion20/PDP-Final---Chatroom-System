package po;

/**
 * Enum type MessageType.
 */
@SuppressWarnings({"PMD"})
public enum MessageType {
  CONNECT_MESSAGE(19),
  CONNECT_RESPONSE(20),
  DISCONNECT_MESSAGE(21),
  DISCONNECT_RESPONSE(22),
  QUERY_CONNECTED_USERS(23),
  QUERY_USER_RESPONSE(24),
  BROADCAST_MESSAGE(25),
  DIRECT_MESSAGE(26),
  FAILED_MESSAGE(27),
  SEND_INSULT(28);

  private int value;

  MessageType(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  /**
   * Given a int value, provide corresponding MessageType instance.
   * @param value int value
   * @return The actual MessageType obj.
   */
  public static MessageType valueOf(int value) {
    switch (value) {
      case 19:
        return CONNECT_MESSAGE;
      case 20:
        return CONNECT_RESPONSE;
      case 21:
        return DISCONNECT_MESSAGE;
      case 22:
        return DISCONNECT_RESPONSE;
      case 23:
        return QUERY_CONNECTED_USERS;
      case 24:
        return QUERY_USER_RESPONSE;
      case 25:
        return BROADCAST_MESSAGE;
      case 26:
        return DIRECT_MESSAGE;
      case 27:
        return FAILED_MESSAGE;
      case 28:
        return SEND_INSULT;
      default:
        return null;
    }
  }
}
