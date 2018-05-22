package bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import pl.kolis.mobilevoter.activities.VotingActivity;
import pl.kolis.mobilevoter.utilities.Constants;

/**
 * Created by asica on 15.05.18.
 */

public class BluetoothServer {

    //TODO dicoverable

    private static final String TAG = "BluetoothServer";
    private static final String NAME ="Test";
    private static final UUID MY_UUID = UUID.fromString("04E78CB0-1084-11E6-A837-0800200C9A66");

    private List<ConnectedThread> mConnectedThreads;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private VotingActivity mActivity;
    private String mMessage;

    public BluetoothServer(BluetoothAdapter mBluetoothAdapter, Handler handler, VotingActivity activity, String s) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        mHandler = handler;
        mActivity = activity;
//        mConnectedThreads = new ArrayList<>();
        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
        mMessage = s;
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
                        Log.d(TAG, "Starting to manage connection");
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
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            Constants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        Constants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(Constants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

//    public MyBluetoothManager.ConnectedThread startCommunication(BluetoothSocket socket) throws UnsupportedEncodingException {
//        MyBluetoothManager.ConnectedThread connectedThread = new MyBluetoothManager.ConnectedThread(socket);
//        connectedThread.start();
//        connectedThread.write("hello".getBytes("UTF-8"));
//        return connectedThread;
//    }

    private void manageMyConnectedSocket(BluetoothSocket socket) throws UnsupportedEncodingException {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "New voter", Toast.LENGTH_LONG).show();
            }
        });
        mActivity.onNewVoter();
        ConnectedThread connectedThread = new ConnectedThread(socket);
        connectedThread.start();
//        connectedThread.write("hello".getBytes("UTF-8"), Constants.QUESTION_MSG);
        connectedThread.write(mMessage.getBytes("UTF-8"));
        Log.d(TAG, "Poll sent");

    }

    public void send(String text, int code) {

    }
}
