package bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by asica on 15.05.18.
 */

public class BluetoothServer {

    //TODO dicoverable

    private static final String TAG = "BluetoothServer";
    private static final String NAME ="Test";
    private static final UUID MY_UUID = UUID.fromString("04E78CB0-1084-11E6-A837-0800200C9A66");

//    private List<ConnectedThread> mConnectedThreads;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    public BluetoothServer(BluetoothAdapter mBluetoothAdapter, Handler handler) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        mHandler = handler;
//        mConnectedThreads = new ArrayList<>();
        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                Log.d(TAG, "I'm listening");
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    Log.d(TAG, "connected");
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    try {
                        manageMyConnectedSocket(socket);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        mmServerSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
//
//    private class ConnectedThread extends Thread {
//
//        private InputStream mIn;
//        private OutputStream mOut;
//
//        @Override
//        public void run() {
//            super.run();
//            //TODO reading writing
//        }
//    }

    private void manageMyConnectedSocket(BluetoothSocket socket) throws UnsupportedEncodingException {
        MyBluetoothManager btManager = new MyBluetoothManager(mHandler);
        Thread connectedThread = btManager.startCommunication(socket);
    }
}
