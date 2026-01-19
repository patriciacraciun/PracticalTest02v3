package ro.pub.cs.systems.pdsd.practicaltest02v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class PracticalTest02MainActivityv3 extends AppCompatActivity {

    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText wordText;
    private Button connectButton, getDefinitionButton;
    private TextView definitionTextView;
    private ServerThread serverThread;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String def = intent.getStringExtra("definition");
            definitionTextView.setText(def);
            Toast.makeText(context, "Definiție primită!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v3_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        wordText = findViewById(R.id.word_text);
        connectButton = findViewById(R.id.connect_button);
        getDefinitionButton = findViewById(R.id.get_definition_button);
        definitionTextView = findViewById(R.id.definition_text_view);

        // 1. Pornire Server
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String port = serverPortEditText.getText().toString();
                if (port.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Port lipsa!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(port));
                serverThread.start();
                Toast.makeText(getApplicationContext(), "Server pornit!", Toast.LENGTH_SHORT).show();
            }
        });

        getDefinitionButton.setOnClickListener(v -> {
            String word = wordText.getText().toString();
            if (word.isEmpty()) return;

            String port = serverPortEditText.getText().toString();
            int intPort = Integer.parseInt(port);

            // Pornim Clientul care vorbește cu Serverul
            new ClientThread(Constants.SERVER_HOST, intPort, word, definitionTextView).start();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_EXPORTED);

    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) serverThread.stopThread();
        super.onDestroy();
    }
}