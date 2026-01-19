package ro.pub.cs.systems.pdsd.practicaltest02v3;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            // 1. Citim cuvântul de la client
            String word = reader.readLine();
            if (word == null || word.isEmpty()) return;

            Log.i(Constants.TAG, "[COMM] Caut definitie pentru: " + word);

            // 2. Request HTTP GET la Dictionary API
            HttpClient httpClient = new DefaultHttpClient();
            String url = Constants.DICTIONARY_WEB_SERVICE_ADDRESS + word;
            HttpGet httpGet = new HttpGet(url);
            String pageSourceCode = httpClient.execute(httpGet, new BasicResponseHandler());

            Log.d(Constants.TAG, "FULL JSON: " + pageSourceCode);

            // 3. Parsare JSON (Specific DictionaryAPI)
            // Structura: [{ "meanings": [ { "definitions": [ { "definition": "TEXT" } ] } ] }]
            JSONArray result = new JSONArray(pageSourceCode);
            JSONObject firstEntry = result.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject firstMeaning = meanings.getJSONObject(0);
            JSONArray definitions = firstMeaning.getJSONArray("definitions");
            JSONObject firstDefinitionObj = definitions.getJSONObject(0);

            String definition = firstDefinitionObj.getString("definition");

            Log.d(Constants.TAG, "Parsed Definition: " + definition);

            // 4. Trimitem rezultatul inapoi la Client
            writer.println(definition);
            socket.close();

        } catch (Exception e) {
            Log.e(Constants.TAG, "Eroare Comm: " + e.getMessage());
        }
    }
}