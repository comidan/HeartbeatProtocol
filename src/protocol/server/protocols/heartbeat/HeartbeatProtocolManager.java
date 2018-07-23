package protocol.server.protocols.heartbeat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatProtocolManager implements Observer<HeartbeatState, HeartbeatEvent> {

    private static final Logger LOGGER = Logger.getLogger(HeartbeatProtocolManager.class.getName());

    private HeartbeatListener listener;
    private ExecutorService executor;
    private Map<String, HeartbeatProtocol> monitoredHosts;

    public HeartbeatProtocolManager(HeartbeatListener listener) {
        this.listener = listener;
        monitoredHosts = new HashMap<>();
        executor = Executors.newCachedThreadPool();
    }

    public void kill() {
        executor.shutdown();
    }

    public void addHost(String hostId, int port) {
        try {
            if (monitoredHosts.get(hostId) == null) {
                HeartbeatProtocol heartbeatProtocol = new HeartbeatProtocol(port, this, hostId);
                monitoredHosts.put(hostId, heartbeatProtocol);
                executor.submit(heartbeatProtocol);
                listener.onAcquiredCommunication(new HeartbeatEvent(hostId, 0, new Date().getTime()));
            }
        }
        catch (IOException exc) {
            LOGGER.log(Level.SEVERE, exc::getMessage);
        }
    }

    public boolean removeFromMonitoredHost(String identifier) {
        HeartbeatProtocol monitor = monitoredHosts.remove(identifier);
        if(monitor != null) {
            monitor.kill();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void update(HeartbeatState heartbeatState, HeartbeatEvent event) {
        switch (heartbeatState) {

            case HOST_OFFLINE: listener.onDeath(event);
                               removeFromMonitoredHost(event.getSource());
                               break;
            case HEARTBEAT_RECEIVED: listener.onHeartbeat(event); break;
            case HOST_ONLINE: listener.onReacquiredCommunication(event); break;
            case COMMUNICATION_LOST: listener.onLossCommunication(event); break;
            default: break;
        }
    }
}
