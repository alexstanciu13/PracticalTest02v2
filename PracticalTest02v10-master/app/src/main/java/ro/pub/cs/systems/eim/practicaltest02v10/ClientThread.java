package ro.pub.cs.systems.eim.practicaltest02v10;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String operation;
    private final TextView operationTextView;

    private Socket socket;

    public ClientThread(String address, int port, String operation, TextView operationTextView) {
        this.address = address;
        this.port = port;
        this.operation = operation;
        this.operationTextView = operationTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[CLIENT THREAD] Putting the information from the server...");
            printWriter.println(operation);
            printWriter.flush();
            printWriter.flush();
            String weatherInformation;
            Log.i(Constants.TAG, "[CLIENT THREAD] Getting the information from the server...");
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeatherInformation = weatherInformation;
                operationTextView.post(() -> operationTextView.setText(finalizedWeatherInformation));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
