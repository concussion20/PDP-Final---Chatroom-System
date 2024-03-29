package po;

/**
 * Stands for a Socket Config obj.
 */
@SuppressWarnings({"PMD"})
public class SocketConfig {
  private String ip;
  private String port;

  public SocketConfig(final String ip, final String port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }
}
