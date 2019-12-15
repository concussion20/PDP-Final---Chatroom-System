package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import po.SocketConfig;

/**
 * Load configs of Socket ip and port.
 */
@SuppressWarnings({"PMD"})
public class ConfigLoader {
  private static final String IP_PORT_FILE_PATH = "config/ip_port.cfg";

  /**
   * Load Socket configs from local config file.
   * @return SocketConfig obj.
   */
  public static SocketConfig loadIpPort() {
    String ip = "";
    String port = "";

    final FileReader fr;
    try {
      fr = new FileReader(IP_PORT_FILE_PATH);
      final BufferedReader bf = new BufferedReader(fr);

      String line1 = bf.readLine();
      ip = line1.substring(line1.indexOf("=") + 2);
      String line2 = bf.readLine();
      port = line2.substring(line2.indexOf("=") + 2);
      bf.close();
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new SocketConfig(ip, port);
  }
}
