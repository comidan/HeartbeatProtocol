[![Codacy Badge](https://api.codacy.com/project/badge/Grade/954a0d9afcda4fa08e90bd5de0336b8e)](https://app.codacy.com/app/Daniele-Comi/HeartbeatProtocol?utm_source=github.com&utm_medium=referral&utm_content=Daniele-Comi/HeartbeatProtocol&utm_campaign=Badge_Grade_Settings)
[![Maintainability](https://api.codeclimate.com/v1/badges/1653a270f84252f44179/maintainability)](https://codeclimate.com/github/Daniele-Comi/HeartbeatProtocol/maintainability)

# HeartbeatProtocol
Heartbeat protocol written in Java, working over UDP


# Powered by 
  [![N|Java 8](http://www.winpenpack.com/en/e107_files/downloadthumbs/java_logo.png)](http://www.oracle.com/technetwork/java/index.html)
# Testing tools 
[![N|Sonarqube](http://oomusou.io/images/feature/sonar.png)](https://www.sonarqube.org)[![N|Travis CI](https://cdn.iconscout.com/public/images/icon/free/png-128/travis-ci-company-brand-logo-3ea4b6108b6d19db-128x128.png)](https://travis-ci.com) [![N|Maven](https://www.myget.org/Content/images/packageDefaultIcon_maven.png)](http://maven.apache.org/download.cgi) [![N|JUnit](https://res.cloudinary.com/codetrace/raw/upload/v1518632546/java:junit:junit.png)](https://junit.org/junit5/) [![N|Code Climate](https://images.ctfassets.net/3ouphkrynjol/3dbUrGMAFOuYQasYGWM0SK/f7167126b1ff3e8cc7c895e508c08a13/codeclimate.com.png)](https://codeclimate.com)

### Heartbeat Protocol Description
The heartbeat protocol is structured on a continuous notification of a UDP datagram with a delay of 1000 milliseconds. The payload field contains the identifier ID from the client that can consist of a string such as the MAC address of the network cards in current use by the ping source or an identifier such as a username.
The server will listen to the various ping sorting them according to the identifier and managing the various states that they will define themselves.
Whenever the server will notice that after a delay of 3000 milliseconds has not received any ping from the client will notify through an event the user of this protocol of the
probable loss of connection advising to limit or stop the exchange of data through any other connection-dependent protocol such as TCP.
If within 5,000 milliseconds you will receive with this identifier the client will be connected again, meaning that it is possible to restore the data communication channel.
Otherwise the client will be considered completely offline.
All this will be notified as mentioned above through an event management as shown below:
 
 
 
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
     
 The sample code above shows the capture of various events on the states of a client, it will then be the task of those who implement the listener the decision on what to do when capturing a certain type of event as below :
    
    public class ServerStart implements HeartbeatListener {
        private static final ServerStart thisInstance = new ServerStart();
        
        public static void main(String[] args) {
            HeartbeatProtocolManager heartbeatProtocolManager = new HeartbeatProtocolManager(thisInstance);
            heartbeatProtocolManager.addHost("Test", 43210); //identifier, port
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
  
  
The event generated in addition to containing the payload will also contain the delay passed with respect to the previous one with its own payload identifier and the timestamp of the current reception of the ping.
  
The use of this protocol will allow a better management of the status of the connections between client and server and a greater control of the possible errors.
