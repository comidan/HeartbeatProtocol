package example.server;

import protocol.server.protocols.heartbeat.HeartbeatEvent;
import protocol.server.protocols.heartbeat.HeartbeatListener;
import protocol.server.protocols.heartbeat.HeartbeatProtocolManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStart implements HeartbeatListener {

    private static final Logger LOGGER = Logger.getLogger(ServerStart.class.getName());

    public static void main(String[] args) {
        HeartbeatProtocolManager heartbeatProtocolManager = new HeartbeatProtocolManager(new ServerStart());
        heartbeatProtocolManager.addHost("Test", 43210);
        heartbeatProtocolManager.addHost("Test1", 43211);
    }

    @Override
    public void onHeartbeat(HeartbeatEvent event) {
        LOGGER.log(Level.INFO, () -> "Received heartbeat from " + event.getSource() + " in " + event.getTimeFromPreviousBeat() + " at " + event.getBeatTimeStamp());
    }

    @Override
    public void onDeath(HeartbeatEvent event) {
        LOGGER.log(Level.INFO, () -> event.getSource() + " died after " + event.getTimeFromPreviousBeat() + " at " + event.getBeatTimeStamp());
    }

    @Override
    public void onLossCommunication(HeartbeatEvent event) {
        LOGGER.log(Level.INFO, () -> "Communication lost of " + event.getSource() + " in " + event.getTimeFromPreviousBeat() + " at " + event.getBeatTimeStamp());
    }

    @Override
    public void onReacquiredCommunication(HeartbeatEvent event) {
        LOGGER.log(Level.INFO, () -> "Communication reacquired of " + event.getSource() + " in " + event.getTimeFromPreviousBeat() + " at " + event.getBeatTimeStamp());
    }

    @Override
    public void onAcquiredCommunication(HeartbeatEvent event) {
        LOGGER.log(Level.INFO, () -> event.getSource() + " connected at " + event.getBeatTimeStamp());
    }
}
