package po;

import java.util.List;

/**
 * Message obj used in Protocol communications.
 */
@SuppressWarnings({"PMD"})
public class Message {

    private String from;
    private String to;
    private MessageType type;
    private String msg;
    private boolean success;
    private List<String> userList;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public MessageType getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
