package client;

import config.ConfigLoader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import po.Message;
import po.MessageType;
import po.SocketConfig;
import po.User;
import protocol.ProtocolHelper;
import java.util.Scanner;

/**
 * Client side runnable obj.
 */
@SuppressWarnings({"PMD"})
public class Client implements Runnable {

    private User user;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean isLoggedIn;
    private boolean running;

    /**
     * Create a bonding User obj for this client and set up a Socket obj too.
     * @param ip ip of the Socket.
     * @param port port of the Socket.
     * @throws IOException IOException
     */
    public Client(String ip, int port) throws IOException {
        this.user = new User("", new Socket(ip, port));
        this.writer = this.user.getWriter();
        this.reader = this.user.getReader();
        this.isLoggedIn = false;
        this.running = true;
    }

    /**
     * Send a String message to server side using Socket stream.
     * @param msg String msg.
     */
    public void sendMessage(String msg) {
        try {
            writer.write(msg);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the Socket obj and stream objs.
     */
    public void close() {
        this.reader = null;
        this.writer = null;
        this.user.offLine();
    }

    /**
     * The main run method for this runnable obj.
     * Handle messages come from server side.
     */
    @Override
    public void run() {
        // new thread for listening
        this.running = true;
        while (this.running) {
            // decode the msg stream
            Message msg = ProtocolHelper.generateMessageObj(this.reader);  // may block here
            switch (msg.getType()) {
                case CONNECT_RESPONSE:
                    System.out.println(msg.getMsg());
                    if (!msg.isSuccess()) {
                        this.user.setName("");
                        //let the client program be dead afterwards.
                        this.running = false;
                        System.out.println("Connecting failed, client exits.");
                    } else {
                        //confirm logged in
                        this.isLoggedIn = true;
                    }
                    break;
                case DISCONNECT_RESPONSE:
                    //logged out
                    this.isLoggedIn = false;
                    this.user.setName("");
                    //let the client program be dead afterwards.
                    this.running = false;
                    System.out.println(msg.getMsg());
                    break;
                case QUERY_USER_RESPONSE:
                    System.out.println("There are " + msg.getUserList().size() + " other users"
                        + " online.");
                    List<String> otherUsers = msg.getUserList();
                    for (int i = 0; i < otherUsers.size(); i++) {
                        System.out.print(otherUsers.get(i) + " ");
                    }
                    System.out.println();
                    break;
                case FAILED_MESSAGE:
                    System.out.println(msg.getMsg());
                case DIRECT_MESSAGE:
                case BROADCAST_MESSAGE:
                case SEND_INSULT:
                    System.out.println("From: " + msg.getFrom() + ", " + msg.getMsg());
                default:
            }
        }
    }

    private static final String COMMAND_LIST =
        "logon {userName}: sends a CONNECT_MESSAGE to the server\n"
            + "logoff: sends a DISCONNECT_MESSAGE to the server\n"
            + "who: sends a QUERY_CONNECTED_USERS to the server\n"
            + "@{user} {msg}: sends a DIRECT_MESSAGE to the specified user to the server\n"
            + "@all {msg}: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected\n"
            + "!{user}: sends a SEND_INSULT message to the server, to be sent to the specified user";
    private static final String LOG_ON = "logon";
    private static final String LOG_OFF = "logoff";
    private static final String WHO = "who";
    private static final String ALL = "@all";
    private static final String EXCLAMATORY = "!";
    private static final String AT = "@";

    /**
     * Pull up Client Thread to send messages and listener thread for messages from server side.
     * @param scanner scanner obj to receive user input.
     */
    public static void startClient(Scanner scanner) {
        Client client = null;
        try {
            SocketConfig socketConfig = ConfigLoader.loadIpPort();
            client = new Client(socketConfig.getIp(), Integer.parseInt(socketConfig.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread listener = new Thread(client);
        listener.start();
        // todo: loop for input

        System.out.println("Please type in command. \nInput ? to get full command list.\n"
            + "Input q to exit.");
        assert client != null;
        while (client.running && scanner.hasNextLine()) {
            try {
                String line = scanner.nextLine();
                if (line.equals("q")) {
                    break;
                }
                Message message = new Message();
                if (line.equals("?")) {
                    System.out.println(COMMAND_LIST);
                    continue;
                }
                if (!line.startsWith(LOG_ON) && !client.isLoggedIn) {
                    System.out.println("Must be logged in first.");
                    continue;
                } else if (line.startsWith(LOG_ON)) {
                    String[] fields = line.split("\\s+");
                    message.setType(MessageType.CONNECT_MESSAGE);
                    message.setFrom(fields[1]);
                    client.user.setName(fields[1]);
                } else if (line.startsWith(LOG_OFF)) {
                    message.setType(MessageType.DISCONNECT_MESSAGE);
                    message.setFrom(client.user.getName());
                } else if (line.startsWith(WHO)) {
                    message.setType(MessageType.QUERY_CONNECTED_USERS);
                    message.setFrom(client.user.getName());
                } else if (line.startsWith(ALL)) {
                    message.setType(MessageType.BROADCAST_MESSAGE);
                    message.setFrom(client.user.getName());
                    String msg = line.substring(line.indexOf(" ")).trim();
                    message.setMsg(msg);
                } else if (line.startsWith(EXCLAMATORY)) {
                    message.setType(MessageType.SEND_INSULT);
                    message.setFrom(client.user.getName());
                    message.setTo(line.substring(1));
                } else if (line.startsWith(AT)) {
                    message.setType(MessageType.DIRECT_MESSAGE);
                    message.setFrom(client.user.getName());
                    message.setTo(line.substring(1, line.indexOf(' ')));
                    String msg = line.substring(line.indexOf(" ")).trim();
                    message.setMsg(msg);
                } else {
                    System.out.println("Wrong command!");
                    continue;
                }
                client.sendMessage(new String(ProtocolHelper.generateFrame(message)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        client.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client.startClient(scanner);
    }
}
