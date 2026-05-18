package com.avaricious.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class ApiClient {

    private static ApiClient instance;

    public static ApiClient I() {
        return instance == null ? new ApiClient() : instance;
    }

    private final String baseUrl = "https://excusably-stoppage-astonish.ngrok-free.dev";
    private final Json json = new Json();

    private ApiClient() {
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void sendScore(int round, int score) {
        String body = json.toJson(new ScoreEntry("Yuuki", round, score));

        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(baseUrl + "/api/score")
            .header("Content-Type", "application/json")
            .content(body)
            .build();

        Gdx.app.log("NETWORK", "JSON body: " + body);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();

                if (status >= 200 & status < 300) {
                    Gdx.app.log("NETWORK", "Score sent successfully");
                } else {
                    Gdx.app.error("NETWORK", "Server error: " + status);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("NETWORK", "Request failed", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("NETWORK", "Request cancelled");
            }
        });
    }

    public void checkOpponentScore(int round, OpponentScoreCallback callback) {
        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(baseUrl + "api/score/" + round + "/" + "Yuuki")
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();

                if (status < 200 || status >= 300) {
                    Gdx.app.error("NETWORK", "HTTP error: " + status);
                    return;
                }

                OpponentScoreResult result = json.fromJson(OpponentScoreResult.class, httpResponse.getResultAsString());
                if (result.found) {
                    callback.onResult(result.entry);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("NETWORK", "Request failed", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("NETWORK", "Request cancelled");
            }
        });
    }

}
