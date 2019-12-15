package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import po.Message;
import po.User;
import protocol.ProtocolHelper;

/**
 * Represents a Message Center.
 */
@SuppressWarnings({"PMD"})
public class MessageCenter implements Runnable {

    private Map<String, User> userMap;
    private BlockingQueue<Message> msgQue;
    private ExecutorService sndExecutor;
    private volatile Boolean frozen;  // frozen due to broadcast
    private static final int CONCURRENT_LEVEL = 10;

    /**
     * The constructor of the Message Center.
     */
    public MessageCenter() {
        this.userMap = new ConcurrentHashMap<>();
        this.frozen = Boolean.FALSE;
        this.msgQue = new ArrayBlockingQueue<>(CONCURRENT_LEVEL * CONCURRENT_LEVEL);
        this.sndExecutor = Executors.newFixedThreadPool(CONCURRENT_LEVEL);
    }

    /**
     * The run method for every listener thread.
     */
    @Override
    public void run() {
        // start message handlers

        for (int i = 0; i < CONCURRENT_LEVEL; i++) {
            sndExecutor.execute(new MessageSender(this));
        }

        sndExecutor.shutdown();
    }

    /**
     * Joins a new user to the user pool.
     *
     * @param user a new user
     * @return whether successful
     */
    public synchronized boolean signIn(User user) {
        // register this user
        if(this.userNum()>9){
            return false;  // full
        }

        this.userMap.put(user.getName(), user);
        return true;
    }

    /**
     * Create a new listener thread for this user.
     *
     * @param user the user
     */
    public void listen(User user) {
        new Thread(new MessageReceiver(this, user)).start();
    }

    /**
     * Detaches a user from the user pool.
     *
     * @param user a exist user
     * @return whether successful
     */
    public synchronized boolean signOff(User user) {
        while (frozen) {
            try {
                Thread.sleep(1000);  // wait for broadcast
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        this.userMap.remove(user.getName());  // remove from the user pool
        return true;
    }

    /**
     * Whether this user is exist.
     *
     * @param name the user name
     * @return whether is an exist user
     */
    public boolean hasUser(String name) {
        return this.userMap.containsKey(name);
    }

    /**
     * Gets the number of users.
     *
     * @return the number of users
     */
    public int userNum() {
        return this.userMap.size();
    }

    /**
     * Broadcasts a msg to the public.
     *
     * @param msg the public msg
     */
    public synchronized void broadcast(Message msg) {

        frozen = true; // blocking sign off
        // generate the public msg steam
        char[] msgStream = ProtocolHelper.generateFrame(msg);
        // broadcast
        for (User user : userMap.values()) {
            sendMsgStream(user.getWriter(), msgStream);
        }

        frozen = false;
    }

    /**
     * Sends a msg stream.
     *
     * @param writer    the writer of a user
     * @param msgStream the stream of msg
     */
    public static void sendMsgStream(BufferedWriter writer, char[] msgStream) {
        try {
            writer.write(msgStream);
            writer.flush();
        } catch (IOException e) {
            // just let it go
            System.out.println("MSG Discard: " + String.copyValueOf(msgStream));
        }
    }

    /**
     * Generates a msg stream and sends it.
     *
     * @param writer the writer of a user
     * @param msg    the msg obj
     */
    public static void sendMsg(BufferedWriter writer, Message msg) {
        try {
            writer.write(ProtocolHelper.generateFrame(msg));
            writer.flush();
        } catch (IOException e) {
            // just let it go
            System.out.println("MSG Discard: FROM->" + msg.getFrom() + " TO->" + msg.getTo()
                + " MSG->" + msg.getMsg());
        }
    }

    /**
     * Gets the user.
     *
     * @param name the user name
     * @return the user obj
     */
    public User getUser(String name) {
        return this.userMap.getOrDefault(name, null);
    }

    /**
     * Gets all users' names.
     *
     * @return the name list of users
     */
    public List<String> getUserList() {
        return new ArrayList<>(this.userMap.keySet());
    }

    /**
     * Gets the queue of unsent msg.
     *
     * @return the queue of msg
     */
    public BlockingQueue<Message> getMsgQue() {
        return msgQue;
    }
}
