package com.avaricious.network;

import com.avaricious.utility.GameContext;
import com.badlogic.gdx.Gdx;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketClient {

    private Socket socket;
    private final String socketUrl = GameContext.I().deviceInfo.isEmulator()
        ? "http://10.0.2.2:3000"
        : "http://127.0.0.1:3000";

    public void connect() {
        try {
            socket = IO.socket(socketUrl);

            socket.on(Socket.EVENT_CONNECT, args -> {
                Gdx.app.log("SOCKET", "Connected: " + socket.id());
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Gdx.app.log("SOCKET", "Connection error: " + args[0]);
            });

            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void emit(String eventName) {
        if (!isConnected()) {
            Gdx.app.error("SOCKET", "Cannot emit, socket not connected: " + eventName);
            return;
        }

        socket.emit(eventName);
    }

    public void emitJson(String eventName, JsonPayloadBuilder builder) {
        if (!isConnected()) {
            Gdx.app.error("SOCKET", "Cannot emit, socket not connected: " + eventName);
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            builder.build(payload);

            socket.emit(eventName, payload);

        } catch (JSONException e) {
            Gdx.app.error("SOCKET", "JSON error while creating payload: " + eventName, e);
        } catch (Exception e) {
            Gdx.app.error("SOCKET", "Unexpected error in event: " + eventName, e);
        }
    }

    public void on(String eventName, Emitter.Listener listener) {
        socket.on(eventName, listener);
    }

    public void onJson(String eventName, JsonHandler handler) {
        socket.on(eventName, socketListener(eventName, handler));
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public String getSocketId() {
        return socket != null ? socket.id() : null;
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
            socket = null;
        }
    }

    private Emitter.Listener socketListener(String eventName, JsonHandler handler) {
        return args -> {
            try {
                if (args.length == 0 || !(args[0] instanceof JSONObject)) {
                    Gdx.app.error("SOCKET", "Invalid payload for event: " + eventName);
                    return;
                }

                handler.handle((JSONObject) args[0]);

            } catch (JSONException e) {
                Gdx.app.error("SOCKET", "JSON error in event: " + eventName, e);
            } catch (Exception e) {
                Gdx.app.error("SOCKET", "Unexpected error in event: " + eventName, e);
            }
        };
    }

    @FunctionalInterface
    public interface JsonHandler {
        void handle(JSONObject data) throws JSONException;
    }

    @FunctionalInterface
    public interface JsonPayloadBuilder {
        void build(JSONObject payload) throws JSONException;
    }

}
