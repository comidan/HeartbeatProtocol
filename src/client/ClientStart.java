package client;

import client.protocols.heartbeat.HeartbeatProtocolManager;

import java.io.IOException;

public class ClientStart {

    public static void main(String[] args) throws IOException {
        new HeartbeatProtocolManager(System.getProperty("myapplication.ip"), 43210, "Test");
    }
}
