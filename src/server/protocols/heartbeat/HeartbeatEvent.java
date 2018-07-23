package server.protocols.heartbeat;

public class HeartbeatEvent  {

    private String source;
    private int timeElapsedPreviousBeat;
    private long timeStamp;

    HeartbeatEvent(String source, int timeElapsedPreviousBeat, long timeStamp) {
        this.source = source;
        this.timeElapsedPreviousBeat = timeElapsedPreviousBeat;
        this.timeStamp = timeStamp;
    }

    public String getSource() {
        return source;
    }

    public int getTimeFromPreviousBeat() {
        return timeElapsedPreviousBeat;
    }

    public long getBeatTimeStamp() {
        return timeStamp;
    }
}
