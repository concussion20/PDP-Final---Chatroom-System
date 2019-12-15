package server;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import po.Message;
import po.MessageType;
import po.User;
import protocol.ProtocolHelper;

/**
 * The Message Receiver.
 */
@SuppressWarnings({"PMD"})
public class MessageReceiver implements Runnable {

    private User user;
    private BlockingQueue<Message> messages;
    private MessageCenter center;

    /**
     * The constructor of the Message Receiver.
     *
     * @param center the Message Center
     * @param user   the user
     */
    public MessageReceiver(MessageCenter center, User user) {
        this.center = center;
        this.messages = center.getMsgQue();
        this.user = user;
    }

    /**
     * The run method for the listener thread of this receiver.
     */
    @Override
    public void run() {
        // read from this.user, and handle msg
        boolean running = true;
        while (running) {
            // may block here
            // deal with msg stream
            Message msg = ProtocolHelper.generateMessageObj(user.getReader());
            if (msg.getType() == null) {
                continue;
            }

            switch (msg.getType()) {
                case BROADCAST_MESSAGE: // public msg -> call back the center
                    this.center.broadcast(msg);
                    break;
                case DIRECT_MESSAGE: // direct msg -> drop into the msg queue
                case SEND_INSULT:
                    if (this.center.hasUser(msg.getTo())) {
                        this.messages.add(msg);
                    } else {
                        userNotFound(msg);
                    }
                    break;
                case QUERY_CONNECTED_USERS: // self msg -> handle it
                    sendQuery(msg);
                    break;
                case CONNECT_MESSAGE:
                    onLine(msg);
                    break;
                case DISCONNECT_MESSAGE: // system msg -> handle it
                    offLine(msg);
                    running = false; // end
                    break;
            }
        }
    }

    private void onLine(Message msg) {
        String name = msg.getFrom();
        Message rsp = new Message();
        rsp.setTo(msg.getFrom());
        rsp.setType(MessageType.CONNECT_RESPONSE);
        if (this.center.hasUser(name)) {
            rsp.setSuccess(false);
            rsp.setMsg("User Name: " + name + " is exist!");
        } else {
            user.setName(name);
            if (this.center.signIn(user)) {
                rsp.setSuccess(true);
                rsp.setMsg(
                    "There are " + (this.center.userNum() - 1) + " other connected clients.");
                System.out.println("User: " + name + " login");
            } else {
                rsp.setSuccess(false);
                rsp.setMsg(
                    "There are already" + this.center.userNum() + " other connected clients!");
            }
        }

        MessageCenter.sendMsg(this.user.getWriter(), rsp);
    }

    private void userNotFound(Message msg) {
        Message rsp = new Message();
        rsp.setTo(msg.getFrom());
        rsp.setType(MessageType.FAILED_MESSAGE);
        rsp.setMsg("User: " + msg.getTo() + " Not Found!");

        MessageCenter.sendMsg(this.user.getWriter(), rsp);
    }

    private void sendQuery(Message msg) {
        Message rsp = new Message();
        rsp.setTo(msg.getFrom());
        rsp.setType(MessageType.QUERY_USER_RESPONSE);
        List<String> userList = this.center.getUserList();
        userList.remove(this.user.getName());  // exclude self
        rsp.setUserList(userList);

        MessageCenter.sendMsg(this.user.getWriter(), rsp);
    }

    private void offLine(Message msg) {
        Message rsp = new Message();
        if (this.center.signOff(user)) {
            rsp.setTo(msg.getFrom());
            rsp.setType(MessageType.DISCONNECT_RESPONSE);
            rsp.setMsg("You are no longer connected.");
            MessageCenter.sendMsg(this.user.getWriter(), rsp);

            this.user.offLine();
        } else {
            rsp.setTo(msg.getFrom());
            rsp.setType(MessageType.FAILED_MESSAGE);
            rsp.setMsg("Disconnect Failed!");
            MessageCenter.sendMsg(this.user.getWriter(), rsp);
        }

    }
}
