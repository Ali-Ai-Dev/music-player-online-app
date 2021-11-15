package com.app.ali_bozorgzad.music_player;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class WebHttpConnection extends AsyncTask<String, Integer, String> {
    private URL url = null;
    private Uri.Builder builder = null;
    private String method;

    WebHttpConnection(String urlConnect, String sendMethod, Uri.Builder builderConnect) {
        try {
            url = new URL(urlConnect);
            method = sendMethod;

            if (builderConnect != null) {
                builder = builderConnect;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... sUrl) {
        try {
            if (builder != null && method.equals("GET")) {
                url = new URL(url.toString() + builder.toString());
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(1300);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoInput(true);

            if (builder != null && method.equals("POST")) {
                httpURLConnection.setDoOutput(true);
                String query = builder.build().getEncodedQuery();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                outputStream.close();
            }

            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            return convertInputStreamToString(inputStream);
        } catch (IOException e) {
            Log.e("error method", " " + e.getMessage());
        }

        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}