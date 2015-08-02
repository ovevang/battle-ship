package no.sonat.battleships;

import net.relativt.battlecodeships.bot.BattleCodeShipsServer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lars on 16.06.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        final BattleCodeShipsServer server = BattleCodeShipsServer.builder()
                .withUrl("ws://www.battlecodeships.com/ws")
                .withBotName("QuiteSmartRobot")
                .withGameFactory((gameId) -> new VerySmartRobot())
                .build();

        server.start();
    }
}
