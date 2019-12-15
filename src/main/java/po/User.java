package po;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Represents a user.
 */
@SuppressWarnings({"PMD"})
public class User {

    private String name;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    /**
     * The constructor of the user.
     *
     * @param socket the socket (won't check)
     */
    public User(Socket socket) {
        this.socket = socket;
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.US_ASCII));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.US_ASCII));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of the user.
     *
     * @param name   the user name (won't check)
     * @param socket the socket (won't check)
     */
    public User(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.US_ASCII));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.US_ASCII));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the user name.
     *
     * @param name the user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the writer of the user.
     *
     * @return the writer
     */
    public BufferedWriter getWriter() {
        return writer;
    }

    /**
     * Gets the reader of the user.
     *
     * @return the reader
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * Closes everything of this user.
     */
    public void offLine() {
        try {
            this.reader.close();
            this.writer.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
