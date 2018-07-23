package protocol.server.protocols.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class HeartbeatProtocol implements Runnable, Observable<HeartbeatState, HeartbeatEvent> {

    private static final int TIME_INTERVAL = 1000;
    private static final int TIME_LIFE_FRAMES = 5;
    private static final int TIME_LOSS_COMMUNICATION_FRAME = 3;
    private static final int UDP_VALID_PACKET_SIZE  = 1024;

    private DatagramSocket datagramSocket;
    private int port;
    private ExecutorService executor;
    private String data;
    private String expectedPayload;
    private Observer<HeartbeatState, HeartbeatEvent> observer;
    private Thread runnableWorkerThread;
    private boolean isDead = false;

    HeartbeatProtocol(int port, Observer observer, String expectedPayload) throws IOException{
        executor = Executors.newSingleThreadExecutor();
        this.port = port;
        datagramSocket = new DatagramSocket(port);
        this.observer = observer;
        this.expectedPayload = expectedPayload;
        runnableWorkerThread = null;
    }

    void kill() {
        executor.shutdownNow();
        isDead = true;
        runnableWorkerThread.interrupt();
    }

    public int getPort() {
        return port;
    }

    /**
     * @return received heartbeat
     */
    private byte[] receiveHeartbeat() throws IOException{
        byte[] payload = receiveData(datagramSocket);
        data = new String(payload);
        return payload;
    }

    /**
     * @apiNote listen for one host heartbeat and monitor it, launching events
     */
    @Override
    public void run() {
        Future asynchronousHeartbeat = executor.submit(this::receiveHeartbeat);
        boolean lossCommsAlreadyNotified = false;
        int timeElapsed = 0;
        HeartbeatEvent event;
        runnableWorkerThread = Thread.currentThread();
        while (!isDead && !executor.isShutdown()) {
            while (!asynchronousHeartbeat.isDone()) {
                try {
                    Thread.sleep(TIME_INTERVAL);
                } catch (InterruptedException exc) {
                    if(data.equals(expectedPayload)) {
                        event = new HeartbeatEvent(data, timeElapsed, new Date().getTime());
                        notify(HeartbeatState.HOST_OFFLINE, event);
                        asynchronousHeartbeat.cancel(true);
                        Thread.currentThread().interrupt();
                        isDead = true;
                    }
                }
                timeElapsed += TIME_INTERVAL;
                if (timeElapsed > TIME_LOSS_COMMUNICATION_FRAME * TIME_INTERVAL && !lossCommsAlreadyNotified)
                    lossCommsAlreadyNotified = notifyLostComm(timeElapsed);

                if (timeElapsed > TIME_LIFE_FRAMES * TIME_INTERVAL)
                    isDead = notifyDeath(timeElapsed, asynchronousHeartbeat);
            }
            if(!isDead && data.equals(expectedPayload)) {
                notifyHeartbeat(lossCommsAlreadyNotified, timeElapsed);
                lossCommsAlreadyNotified = false;
                isDead = false;
                timeElapsed = 0;
            }
            asynchronousHeartbeat = executor.submit(this::receiveHeartbeat);
        }
        asynchronousHeartbeat.cancel(true);
    }

    private boolean notifyLostComm(int timeElapsed) {
        HeartbeatEvent event = new HeartbeatEvent(expectedPayload, timeElapsed, new Date().getTime());
        notify(HeartbeatState.COMMUNICATION_LOST, event);
        return true;
    }

    private boolean notifyDeath(int timeElapsed, Future asynchronousHeartbeat) {
        HeartbeatEvent event = new HeartbeatEvent(expectedPayload, timeElapsed, new Date().getTime());
        asynchronousHeartbeat.cancel(true);
        notify(HeartbeatState.HOST_OFFLINE, event);
        return true;
    }

    private void notifyHeartbeat(boolean lossCommsAlreadyNotified, int timeElapsed) {
        HeartbeatEvent event = new HeartbeatEvent(data, timeElapsed, new Date().getTime());
        if (lossCommsAlreadyNotified)
            notify(HeartbeatState.HOST_ONLINE, event);
        else
            notify(HeartbeatState.HEARTBEAT_RECEIVED, event);
    }

    @Override
    public void notify(HeartbeatState heartbeatState, HeartbeatEvent event) {
        observer.update(heartbeatState, event);
    }

    /**
     * @param datagramSocket socket used to receive from sent data
     * @return received data
     */
    private byte[] receiveData(DatagramSocket datagramSocket) throws IOException {
        byte[] receiveData = new byte[UDP_VALID_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        datagramSocket.receive(receivePacket);
        receiveData = receivePacket.getData();
        byte[] data = new byte[receivePacket.getLength()];
        System.arraycopy(receiveData, receivePacket.getOffset(), data, 0, receivePacket.getLength());
        return data;
    }
}

