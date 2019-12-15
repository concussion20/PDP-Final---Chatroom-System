package protocol;

import grammar.Factory;
import grammar.Grammar;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.simple.parser.ParseException;
import po.Message;
import po.MessageType;

/**
 * Generate Message obj from bufferedReader stream or
 * generate Frames using Message objs.
 */
@SuppressWarnings({"PMD"})
public class ProtocolHelper {
  private static final String[] GRAMMAR_FILE_PATHS = {"grammars/insult_grammar.json",
      "grammars/poem.json"};

  /**
   * generate a Message Obj using bufferedReader of socket's inputStream.
   * @param bufferedReader bufferedReader of socket's inputStream.
   * @return A Message obj.
   */
  public static Message generateMessageObj(BufferedReader bufferedReader) {
    Message message = new Message();

    int type = readIntFromInputStream(bufferedReader);
    MessageType messageType = MessageType.valueOf(type);
    message.setType(messageType);
    skipSingleSpace(bufferedReader);

    int size;
    String messageStr, senderUserName, recipientUserName;
    assert messageType != null;
    switch (messageType) {
      case CONNECT_MESSAGE:
      case DISCONNECT_MESSAGE:
      case QUERY_CONNECTED_USERS:
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        senderUserName = readNCharsFromInputStream(bufferedReader, size);
        message.setFrom(senderUserName);
        break;
      case CONNECT_RESPONSE:
        boolean isConnect = readBooleanFromInputStream(bufferedReader);
        message.setSuccess(isConnect);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        messageStr = readNCharsFromInputStream(bufferedReader, size);
        message.setMsg(messageStr);
        break;
      case DISCONNECT_RESPONSE:
      case FAILED_MESSAGE:
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        messageStr = readNCharsFromInputStream(bufferedReader, size);
        message.setMsg(messageStr);
        break;
      case QUERY_USER_RESPONSE:
        int numUsers = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        List<String> userList = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
          size = readIntFromInputStream(bufferedReader);
          skipSingleSpace(bufferedReader);
          senderUserName = readNCharsFromInputStream(bufferedReader, size);
          userList.add(senderUserName);
          skipSingleSpace(bufferedReader);
        }
        message.setUserList(userList);
        break;
      case BROADCAST_MESSAGE:
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        senderUserName = readNCharsFromInputStream(bufferedReader, size);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        messageStr = readNCharsFromInputStream(bufferedReader, size);
        message.setFrom(senderUserName);
        message.setMsg(messageStr);
        break;
      case DIRECT_MESSAGE:
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        senderUserName = readNCharsFromInputStream(bufferedReader, size);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        recipientUserName = readNCharsFromInputStream(bufferedReader, size);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        messageStr = readNCharsFromInputStream(bufferedReader, size);
        message.setMsg(messageStr);
        message.setFrom(senderUserName);
        message.setTo(recipientUserName);
        break;
      case SEND_INSULT:
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        senderUserName = readNCharsFromInputStream(bufferedReader, size);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        recipientUserName = readNCharsFromInputStream(bufferedReader, size);
        skipSingleSpace(bufferedReader);
        size = readIntFromInputStream(bufferedReader);
        skipSingleSpace(bufferedReader);
        String insultMsg = readNCharsFromInputStream(bufferedReader, size);
        message.setFrom(senderUserName);
        message.setTo(recipientUserName);
        message.setMsg(insultMsg);
        break;
      default:
    }

    return message;
  }

  /**
   * read a single int from InputStream.
   * @param bufferedReader bufferedReader of socket's inputStream.
   * @return a int value converted from len 4 char array.
   */
  private static int readIntFromInputStream(BufferedReader bufferedReader) {
    char[] buffer = new char[4];
    int res = 0;
    try {
      bufferedReader.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < 4; i++) {
      res |= buffer[i] & 0xff;
      if (i< 3) {
        res = res << 8;
      }
    }
    return res;
  }

  /**
   * read size chars from InputStream.
   * @param bufferedReader bufferedReader of socket's inputStream.
   * @param size how many chars need to be read.
   * @return String. len is size.
   */
  private static String readNCharsFromInputStream(BufferedReader bufferedReader, int size) {
    char[] chars = new char[size];
    try {
      bufferedReader.read(chars);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(chars);
  }

  /**
   * read a single boolean from InputStream.
   * @param bufferedReader bufferedReader of socket's inputStream.
   * @return a single boolean value.
   */
  private static boolean readBooleanFromInputStream(BufferedReader bufferedReader) {
    int intVal = 0;
    try {
      intVal = bufferedReader.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return intVal != 0x00;
  }

  /**
   * skip one single space char in inputStream.
   * @param bufferedReader bufferedReader of socket's inputStream.
   */
  private static void skipSingleSpace(BufferedReader bufferedReader) {
    try {
      bufferedReader.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Generate the Frame char array based on the Message obj.
   * @param message Message obj.
   * @return a char array represents the frame.
   */
  public static char[] generateFrame(Message message) {
    List<Character> characterList = new ArrayList<>();
    int type = message.getType().getValue();
    char[] tmpChars = int2CharArr(type);
    addAllChars(characterList, tmpChars);
    characterList.add(' ');
    switch (message.getType()) {
      case CONNECT_MESSAGE:
      case DISCONNECT_MESSAGE:
      case QUERY_CONNECTED_USERS:
        tmpChars = int2CharArr(message.getFrom().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getFrom().toCharArray());
        break;
      case CONNECT_RESPONSE:
        characterList.add(message.isSuccess() ? (char)0x01 : 0x00);
        characterList.add(' ');
        tmpChars =int2CharArr(message.getMsg().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getMsg().toCharArray());
        break;
      case DISCONNECT_RESPONSE:
      case FAILED_MESSAGE:
         tmpChars = int2CharArr(message.getMsg().length());
         addAllChars(characterList, tmpChars);
         characterList.add(' ');
         addAllChars(characterList, message.getMsg().toCharArray());
         break;
      case QUERY_USER_RESPONSE:
        tmpChars = int2CharArr(message.getUserList().size());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        for (String userName: message.getUserList()) {
          tmpChars = int2CharArr(userName.length());
          addAllChars(characterList, tmpChars);
          characterList.add(' ');
          addAllChars(characterList, userName.toCharArray());
          characterList.add(' ');
        }
        break;
      case BROADCAST_MESSAGE:
        tmpChars = int2CharArr(message.getFrom().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getFrom().toCharArray());
        characterList.add(' ');
        tmpChars = int2CharArr(message.getMsg().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getMsg().toCharArray());
        break;
      case DIRECT_MESSAGE:
        tmpChars = int2CharArr(message.getFrom().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getFrom().toCharArray());
        characterList.add(' ');
        tmpChars = int2CharArr(message.getTo().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getTo().toCharArray());
        characterList.add(' ');
        tmpChars = int2CharArr(message.getMsg().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getMsg().toCharArray());
        break;
      case SEND_INSULT:
        tmpChars = int2CharArr(message.getFrom().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getFrom().toCharArray());
        characterList.add(' ');
        tmpChars = int2CharArr(message.getTo().length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, message.getTo().toCharArray());
        characterList.add(' ');
        String insultMsg = null;
        Random random = new Random();
        int index = random.nextInt(2);
        try {
          Grammar grammar = Factory.getInstance().getGrammar(GRAMMAR_FILE_PATHS[index]);
          insultMsg = grammar.generateSentence();
        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
        assert insultMsg != null;
        tmpChars = int2CharArr(insultMsg.length());
        addAllChars(characterList, tmpChars);
        characterList.add(' ');
        addAllChars(characterList, insultMsg.toCharArray());
        break;
      default:
    }
    char[] res = new char[characterList.size()];
    int cnt = 0;
    for (Character c: characterList) {
      res[cnt++] = c;
    }
    return res;
  }

  /**
   * Convert a int value into len 4 char array.
   * @param num the original int value.
   * @return a char array representing the int value.
   */
  private static char[] int2CharArr(int num) {
    char[] chars = new char[4];
    chars[3] = (char) (num & 0xff);
    chars[2] = (char) (num >> 8 & 0xff);
    chars[1] = (char) (num >> 16 & 0xff);
    chars[0] = (char) (num >> 24 & 0xff);
    return chars;
  }

  /**
   * Add all chars in an array into a Character List.
   * @param characterList the dest Character List.
   * @param chars the input char array.
   * @return The dest Character List.
   */
  private static List<Character> addAllChars(List<Character> characterList, char[] chars) {
    for (char c: chars) {
      characterList.add(c);
    }
    return characterList;
  }


}
