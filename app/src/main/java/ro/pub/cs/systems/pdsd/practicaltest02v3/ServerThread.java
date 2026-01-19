package ro.pub.cs.systems.pdsd.practicaltest02v3;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;


    public ServerThread(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "Eroare creare ServerSocket: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER] Astept conexiuni...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER] Client conectat!");

                // Pornim firul de executie dedicat clientului
                new CommunicationThread(this, socket).start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Eroare Server run: " + e.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try { serverSocket.close(); } catch (IOException e) {}
        }
    }
}
