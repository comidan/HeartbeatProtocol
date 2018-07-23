package client;

import client.protocols.heartbeat.HeartbeatProtocolManager;

import java.io.IOException;

public class ClientStart1 {
    public static void main(String[] args) throws IOException {
        new HeartbeatProtocolManager(System.getProperty("myapplication.ip"), 43211, "Test1");
    }
}
