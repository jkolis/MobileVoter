package pl.kolis.mobilevoter.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import bluetooth.BluetoothClient;
import bluetooth.MyBluetoothManager;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.utilities.Constants;

public class ClientVotingActivity extends AppCompatActivity implements Handler.Callback {

    private static final String TAG = ClientVotingActivity.class.getName();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_voting);

        mHandler = new Handler(this);

        Intent i = new Intent(ClientVotingActivity.this, DeviceListActivity.class);
        startActivityForResult(i, Constants.PICK_DEVICE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_DEVICE && resultCode == RESULT_OK) {
            BluetoothDevice device = data.getParcelableExtra(Constants.DEVICE);

            Log.d(TAG, "You choose " + device.getName());
            new BluetoothClient(device, mHandler);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        Log.d(TAG, "message " + message.toString());
        return false;
    }
}

