package com.hearthhelper.hearthstats;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Facade for interacting with the hearthstats http json api.
 */
public class HearthstatsHttpFacade {
    private static final String URL_BASE = "http://hearthstats.net/api/v3";

    public static JSONObject get(String path, Map<String, String> parameters) throws IOException, JSONException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(URL_BASE + path);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            uriBuilder.addParameter(parameter.getKey(), parameter.getValue());
        }

        HttpURLConnection decksConnection = (HttpURLConnection) uriBuilder.build().toURL().openConnection();

        decksConnection.setRequestMethod("GET");

        BufferedInputStream in = new BufferedInputStream(decksConnection.getInputStream());
        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(in, stringWriter);
        JSONObject result = new JSONObject(stringWriter.toString());
        return result;
    }

    public static JSONObject post(String path, Map<String, String> parameters, String postData) throws IOException, JSONException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(URL_BASE + path);
        if (parameters != null) {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                uriBuilder.addParameter(parameter.getKey(), parameter.getValue());
            }
        }
        HttpURLConnection connection = (HttpURLConnection) uriBuilder.build().toURL().openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
        outputStream.write(postData.getBytes(Charset.forName("UTF8")));
        outputStream.close();

        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(in, stringWriter);
        JSONObject result = new JSONObject(stringWriter.toString());
        return result;
    }
}
