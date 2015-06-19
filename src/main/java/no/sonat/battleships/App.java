package no.sonat.battleships;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lars on 16.06.15.
 */
public class App {

    public static void main(String[] args) throws Exception {

        String gameId = args[0];
        int player = Integer.parseInt(args[1]);


        AbstractRobot robot = new VeryDumbRobot(gameId,player );

        robot.initiate();
    }
}
