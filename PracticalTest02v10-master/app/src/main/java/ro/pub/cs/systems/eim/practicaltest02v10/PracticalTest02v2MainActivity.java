package ro.pub.cs.systems.eim.practicaltest02v10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest02v2MainActivity extends AppCompatActivity {
    // PAS 2: Initializare serverThread, toate EditText-urile, Spinner-ul si TextView-ul

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private EditText serverPortEditText = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText nameEditText = null;
    private TextView weatherForecastTextView = null;


    // PAS 3: Initializare listeneri pentru butoane
    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private final GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();

    // PAS 4: Creare conexiune server
    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    // PAS 5: Creare conexiune client
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
                return;
            }
            String city = nameEditText.getText().toString();
            if (city == null || city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weatherForecastTextView.setText("");
            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, weatherForecastTextView
            );
            clientThread.start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // PAS 2.1: Creare elemente vizuale
        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        nameEditText = (EditText)findViewById(R.id.client_input_edit_text);
//        informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        Button getWeatherForecastButton = (Button) findViewById(R.id.get_info_button);
        getWeatherForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        weatherForecastTextView = (TextView)findViewById(R.id.result_text_view);
    }

    // PAS 6: Oprire serverThread
    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}