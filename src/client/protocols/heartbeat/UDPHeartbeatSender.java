package client.protocols.heartbeat;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class UDPHeartbeatSender {

    private static final Logger LOGGER = Logger.getLogger(UDPHeartbeatSender.class.getName());

    /**
     * @param ipAddress ip address to send UDP datagram
     * @param port port number to associate to UDP datagram
     */
    void sendHeartBeat(InetAddress ipAddress, int port, String payload) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            byte[] sendData = payload.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
            clientSocket.send(sendPacket);
        }
        catch (SocketException exc) {
            LOGGER.log(Level.SEVERE, "Socket error\n" + exc.getMessage());
        }
        catch (UnknownHostException exc) {
            LOGGER.log(Level.SEVERE, "Unknown host error\n" + exc.getMessage());
        }
        catch (IOException exc) {
            LOGGER.log(Level.SEVERE, "IO error\n" + exc.getMessage());
        }
    }
}
