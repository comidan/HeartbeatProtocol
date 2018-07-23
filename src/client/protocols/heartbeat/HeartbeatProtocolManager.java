package client.protocols.heartbeat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatProtocolManager {

    private static final long DELAY  = 1000L;
    private static final long PERIOD = 1000L;
    private HeartbeatProtocol heartbeatProtocol;
    private ScheduledExecutorService executor;

    public HeartbeatProtocolManager(String host, int port, String payload) throws IOException {
        heartbeatProtocol = new HeartbeatProtocol(host, port, payload);
        executor = Executors.newSingleThreadScheduledExecutor();
        start();
    }

    /**
     * @apiNote send an heartbeat
     */
    private void start() {
        executor.scheduleAtFixedRate(heartbeatProtocol, DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * @apiNote kill protocol execution
     */
    public void kill() {
        executor.shutdownNow();
    }

    public static void sendBurstUDP(String address, int port, String payload) throws UnknownHostException {
        UDPHeartbeatSender udpSender = new UDPHeartbeatSender();
        for(int i = 0; i < (int) Math.log10(DELAY); i++)
            udpSender.sendHeartBeat(InetAddress.getByName(address), port, payload);
    }

}
