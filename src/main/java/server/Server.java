package server;

import config.ConfigLoader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import po.SocketConfig;
import po.User;

/**
 * The server.
 */
@SuppressWarnings({"PMD"})
public class Server {

    private MessageCenter msgCenter;

    /**
     * The constructor of the server.
     */
    public Server() {
        this.msgCenter = new MessageCenter();
    }

    /**
     * Stars the server
     */
    public void startServer() {

        Thread msgCT = new Thread(msgCenter);
        msgCT.start();

        System.out.println("Server ON");

        try {
            SocketConfig socketConfig = ConfigLoader.loadIpPort();
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(socketConfig.getPort()));
            while (true) {
                Socket socket = serverSocket.accept();
                User user = new User(socket);
                msgCenter.listen(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main of the server.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
