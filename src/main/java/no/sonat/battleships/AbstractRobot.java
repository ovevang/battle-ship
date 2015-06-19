package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.broadcast.GameIsStarted;
import game.broadcast.NextPlayerTurn;
import game.messages.GameMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lars on 16.06.15.
 */
public abstract class AbstractRobot {

    protected final String gameId;
    protected final int player;

    protected WebSocketClient wsClient;

    public AbstractRobot(String gameId, int player) throws Exception {
        this.gameId = gameId;
        this.player = player;

    }

    public abstract void placeShips();

    public abstract void onGameStart(GameIsStarted msg);

    public abstract void shoot();


    public void initiate() throws URISyntaxException {

        ObjectMapper json = new ObjectMapper();

        this.wsClient = new WebSocketClient(new URI("ws://localhost:9000/game/" + gameId + "/socket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("onOpen");

                placeShips();
            }

            @Override
            public void onMessage(String s) {

                GameMessage msg;
                try {
                    msg = json.readValue(s, GameMessage.class);
                } catch (IOException e) {
                    throw new RuntimeException("Error unmarshalling game message");
                }

                if (msg instanceof GameIsStarted) {
                    onGameStart((GameIsStarted)msg);
                } else if (msg instanceof NextPlayerTurn && ((NextPlayerTurn)msg).playerTurn == player) {
                    shoot();
                }

                System.out.println("onMessage");
                System.out.println(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("onclose");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("onError");
                System.out.println(e.getMessage());
            }
        };

        wsClient.connect();
    }
}
