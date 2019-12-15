package server;

import java.util.concurrent.BlockingQueue;
import po.Message;
import po.User;

/**
 * The Message Sender.
 */
@SuppressWarnings({"PMD"})
public class MessageSender implements Runnable {

    private MessageCenter center;
    private BlockingQueue<Message> messages;

    /**
     * The constructor of the Message Sender.
     *
     * @param center the Message Center
     */
    public MessageSender(MessageCenter center) {
        this.center = center;
        this.messages = center.getMsgQue();
    }

    /**
     * Thr run method for sender thread.
     */
    @Override
    public void run() {
        // pick up a message from the queue and send it
        while (true) {
            try {
                Message msg = messages.take();
                User user = center.getUser(msg.getTo());
                if (user != null) {
                    MessageCenter.sendMsg(user.getWriter(), msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
