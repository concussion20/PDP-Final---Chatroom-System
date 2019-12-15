1. The code coverage is significantly relying on the network timing, please run Gradle several times, and the coverage will go up.
2. The input example is described in the test, please check it.
3. The Client has 2 critical threads to send messages to and receive messages from Sever respectively. So as Server.
4. The ip and port can be configured using ip_port.cfg.
5. MessageType lists all types as enums.
6. All Socket stream processing processes got solved by ProtocolHelper.
