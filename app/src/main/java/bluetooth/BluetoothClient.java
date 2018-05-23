package bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BluetoothClient {

    private static final String TAG = BluetoothClient.class.getName();
    private static final String NAME = "Test";
    private static final UUID MY_UUID = UUID.fromString("04E78CB0-1084-11E6-A837-0800200C9A66");

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private ConnectThread mConnectThread;

    public BluetoothClient(BluetoothDevice device, Handler handler) {
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            Log.d(TAG, "connected");
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            try {
                manageMyConnectedSocket();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        private void manageMyConnectedSocket() throws UnsupportedEncodingException {
            MyBluetoothManager btManager = new MyBluetoothManager(mHandler);
            btManager.startCommunication(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public void cancel() {
        mConnectThread.cancel();
    }
}
