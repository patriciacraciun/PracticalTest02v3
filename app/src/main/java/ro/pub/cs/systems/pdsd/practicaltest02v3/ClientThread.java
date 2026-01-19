package ro.pub.cs.systems.pdsd.practicaltest02v3;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String word;

    private TextView resultTextView;

    public ClientThread(String address, int port, String word, TextView resultTextView) {

        this.address = address;
        this.port = port;
        this.word = word;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            // 1. Conectare la propriul Server
            Socket socket = new Socket(address, port);
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            // 2. Trimite cuvântul
            writer.println(word);
            writer.flush();

            // 3. Citeste definitia
            String definition = reader.readLine();

            // --- CERINTA 3c: Broadcast ---
            Intent intent = new Intent(Constants.BROADCAST_ACTION);
            intent.putExtra("definition", definition);


            // Update UI direct (backup)
            if (resultTextView != null) {
                resultTextView.post(() -> resultTextView.setText(definition));
            }
            socket.close();
        } catch (Exception e) {
            Log.e(Constants.TAG, "Eroare Client: " + e.getMessage());
        }
    }
}