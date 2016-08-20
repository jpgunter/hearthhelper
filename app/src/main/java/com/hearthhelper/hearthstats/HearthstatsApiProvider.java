package com.hearthhelper.hearthstats;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.inject.Inject;

import dagger.Component;
import rx.Observable;
import rx.Subscriber;

/**
 * Creates an instance of {@link HearthstatsApi} for the given username and password.
 */
public class HearthstatsApiProvider implements Observable.OnSubscribe<HearthstatsApi> {
    private final String user;
    private final String password;

    public HearthstatsApiProvider(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void call(Subscriber<? super HearthstatsApi> subscriber) {
        try {
            JSONObject userLoginJson = new JSONObject().put("user_login", new JSONObject().put("email", user).put("password", password));

            JSONObject result = HearthstatsHttpFacade.post("/users/sign_in", null,userLoginJson.toString());

            String authToken = result.getString("auth_token");
            String userKey = result.getString("userkey");

            subscriber.onStart();
            subscriber.onNext(new HearthstatsApi(userKey, authToken));
            subscriber.onCompleted();
        } catch (URISyntaxException | IOException | JSONException e) {
            subscriber.onError(e);
        }
    }
}
