package ro.pub.cs.systems.eim.practicaltest02v10;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (operation)");
            String operation = bufferedReader.readLine();
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] Operation is: " + operation);
            if (operation == null || operation.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (operation)");
                return;
            }

            HashMap<String, DataFormat> data = serverThread.getData();
            DataFormat result = null;
            int opRes = 0;

            if (data.containsKey(operation)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                result = data.get(operation);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Calculating the new operation");
                String[] parts = operation.split(",");
                String operationType = parts[0];
                int number1 = Integer.parseInt(parts[1]);
                int number2 = Integer.parseInt(parts[2]);
                if (Objects.equals(operationType, "add")) {
                    opRes = number1 + number2;
                } else if (Objects.equals(operationType, "mul")) {
                    opRes = number1 * number2;
                    CommunicationThread.sleep(10000);
                }

                result = new DataFormat(String.valueOf(opRes));
                data.put(operation, result);
            }
            if (result == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Operation Information is null!");
                return;
            }

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
