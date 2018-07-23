package client.protocols.heartbeat;

import java.io.IOException;
import java.net.InetAddress;

class HeartbeatProtocol implements Runnable {

    private InetAddress serverAddress;
    private UDPHeartbeatSender udpHeartbeatSender;
    private int port;
    private String payload;

    HeartbeatProtocol(String serverAddress, int port, String payload) throws IOException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        udpHeartbeatSender = new UDPHeartbeatSender();
        this.port = port;
        this.payload = payload;
    }

    /**
     * @apiNote send an heartbeat
     */
    @Override
    public void run() {
        udpHeartbeatSender.sendHeartBeat(serverAddress, port, payload);
    }
}
