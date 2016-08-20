package com.hearthhelper.hearthstats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Component;
import rx.Observable;
import rx.Subscriber;

/**
 * Class to provide an interface (through observers) to the hearthstats api.
 */
public class HearthstatsApi {

    private final String userKey;
    private final String authToken;

    public HearthstatsApi(String userKey, String authToken) {
        this.userKey = userKey;
        this.authToken = authToken;
    }

    public Observable.OnSubscribe<List<Deck>> getDecks() {
        return new Observable.OnSubscribe<List<Deck>>() {
            @Override
            public void call(Subscriber<? super List<Deck>> subscriber) {
                try {
                    JSONObject result = HearthstatsHttpFacade.get("/decks", ImmutableMap.of("auth_token", authToken));

                    List<Deck> deckList = new ArrayList<>();
                    JSONArray decksJson = result.getJSONArray("data");
                    for (int i = 0; i < decksJson.length(); i++) {
                        JSONObject deckJson = decksJson.getJSONObject(i);
                        if(deckJson.getBoolean("archived")){
                            continue;
                        }
                        int id = deckJson.getInt("id");
                        int classId = deckJson.getInt("klass_id");
                        String name = deckJson.getString("name");
                        deckList.add(new Deck(id, classId, name));
                    }
                    Collections.sort(deckList, Deck.classThenNameComparator());

                    subscriber.onStart();
                    subscriber.onNext(deckList);
                    subscriber.onCompleted();

                } catch (URISyntaxException | IOException | JSONException e) {
                    subscriber.onError(e);
                }
            }
        };
    }

    public Observable.OnSubscribe<Boolean> recordMatch(final Match match){
        return new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    JSONObject result = HearthstatsHttpFacade.post("/matches",
                            ImmutableMap.of("auth_token", authToken),
                            new ObjectMapper().writeValueAsString(match));
                    subscriber.onStart();
                    subscriber.onNext(Boolean.TRUE);
                    subscriber.onCompleted();
                } catch (URISyntaxException | IOException | JSONException e) {
                    subscriber.onError(e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "HearthstatsApi{" +
                "userKey='" + userKey + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
