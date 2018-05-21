// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc

package pl.kolis.mobilevoter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import pl.kolis.mobilevoter.utilities.Constants;

// Referenced classes of package android.com.myapplication:
//            Hosting

public class Bluetooth_Hosting
{
    private class AcceptThread extends Thread
    {

        private String mSocketType;
        private BluetoothServerSocket mmServerSocket;
        Bluetooth_Hosting bluetooth_hosting;

        public void cancelServer()
        {
//            Toast.makeText(h.(), "CancelServer", 0).show();
            try
            {
                mmServerSocket.close();
                return;
            }
            catch (IOException ioexception)
            {
                return;
            }
        }

        public void run()
        {
            do
            {
                BluetoothSocket bluetoothsocket;
                do
                {
                    try
                    {
                        bluetoothsocket = mmServerSocket.accept();
                    }
                    catch (IOException ioexception)
                    {
                        return;
                    }
                } while (bluetoothsocket == null);
                connected(bluetoothsocket, bluetoothsocket.getRemoteDevice(), mSocketType);
//                h.NieuweConnectieMet(bluetoothsocket, bluetoothsocket.getRemoteDevice());
            } while (true);
        }

        public AcceptThread(Boolean boolean1)
        {
            Object obj;
//            bh = Bluetooth_Hosting.this;
//            super();
            obj = null;
            String s;
            if (boolean1.booleanValue())
            {
                s = "Secure";
            } else
            {
                s = "Insecure";
            }
            mSocketType = s;
//            if (!boolean1.booleanValue()) goto _L2; else goto _L1
//            _L1:
//            bluetooth_hosting = btAdapter.listenUsingRfcommWithServiceRecord("ProjectSecure ", mUUID);
//            _L4:
//            mmServerSocket = Bluetooth_Hosting.this;
//            return;
//            _L2:
//            try
//            {
//                bluetooth_hosting = btAdapter.listenUsingInsecureRfcommWithServiceRecord("ProjectInSecure", mUUID);
//            }
//            // Misplaced declaration of an exception variable
//            catch (Bluetooth_Hosting bluetooth_hosting)
//            {
//                bluetooth_hosting = obj;
//            }
//            if (true) goto _L4; else goto _L3
//            _L3:
        }
    }

    private class ConnectThread extends Thread
    {

        private String mSocketType;
        private BluetoothDevice mmDevice;
        private BluetoothSocket mmSocket;
        final Bluetooth_Hosting this$0;

        public void cancelClient()
        {
//            Toast.makeText(h.getApplication(), "CancelClient", 0).show();
            try
            {
                mmSocket.close();
                return;
            }
            catch (IOException ioexception)
            {
                return;
            }
        }

        public void run()
        {
            btAdapter.cancelDiscovery();
            try
            {
                mmSocket.connect();
                connected(mmSocket, mmDevice, mSocketType);
                return;
            }
            catch (IOException ioexception) { }
            try
            {
                mmSocket.close();
            }
            catch (IOException ioexception1) { }
//            Toast.makeText(h.getApplicationContext(), "Unable to connect device", 0).show();
        }

        public ConnectThread(BluetoothDevice bluetoothdevice, boolean flag)
        {
            this$0 = Bluetooth_Hosting.this;
//            super();
            mmDevice = bluetoothdevice;
            Object obj = null;
            String s;
            if (flag)
            {
                s = "Secure";
            } else
            {
                s = "Insecure";
            }
            mSocketType = s;
//            if (!flag)
//            {
//                break MISSING_BLOCK_LABEL_57;
//            }
//            try
//            {
//                bluetooth_hosting = bluetoothdevice.createRfcommSocketToServiceRecord(mUUID);
//            }
//            // Misplaced declaration of an exception variable
//            catch (Bluetooth_Hosting bluetooth_hosting)
//            {
//                bluetooth_hosting = obj;
//            }
//            mmSocket = Bluetooth_Hosting.this;
//            return;
//            bluetooth_hosting = bluetoothdevice.createInsecureRfcommSocketToServiceRecord(mUUID);
//            break MISSING_BLOCK_LABEL_44;
        }
    }

    private class ConnectedThread extends Thread
    {

        private  InputStream mmInStream;
        private  OutputStream mmOutStream;
        private BluetoothSocket mmSocket;
        Bluetooth_Hosting this$0;

        public void cancel()
        {
            try
            {
                mmSocket.close();
                return;
            }
            catch (IOException ioexception)
            {
                return;
            }
        }

        public void run()
        {
            byte abyte0[] = new byte[1024];
            do
            {
                try
                {
                    int i = mmInStream.read(abyte0);
                    mHandler.obtainMessage(1, i, -1, abyte0).sendToTarget();
                    abyte0 = new byte[1024];
                }
                catch (IOException ioexception)
                {
                    return;
                }
            } while (true);
        }

        public void write(byte abyte0[])
        {
            try
            {
                mmOutStream.write(abyte0);
                return;
            }
            // Misplaced declaration of an exception variable
             catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ConnectedThread(BluetoothSocket bluetoothsocket, String s)
        {
//            super();
//            mmSocket = bluetoothsocket;
//            s = bluetoothsocket.getInputStream();
//            bluetooth_hosting = s;
//            bluetoothsocket = bluetoothsocket.getOutputStream();
//            bluetooth_hosting = s;
//            _L2:
//            mmInStream = Bluetooth_Hosting.this;
//            mmOutStream = bluetoothsocket;
//            return;
//            bluetoothsocket;
//            bluetoothsocket = obj;
//            if (true) goto _L2; else goto _L1
//            _L1:
        }
    }


    BroadcastReceiver bluetoothState;
    private BluetoothAdapter btAdapter;
    private ArrayList discoveredDevicesFound;
    BroadcastReceiver discoveryResult;
    Intent getVisible;
//    private Hosting h;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ArrayList mConnectedThread;
    private Handler mHandler;
    UUID mUUID;

    public void Bluetooth_Hosting(Handler handler)
    {
        discoveredDevicesFound = new ArrayList();
        bluetoothState = new BroadcastReceiver() {

            Bluetooth_Hosting this$0;

            public void onReceive(Context context, Intent intent)
            {
                if ("android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(intent.getAction()))
                {
                    if (intent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", 0x80000000) == 21)
                    {
//                        Toast.makeText(h.getApplicationContext(), h.getResources().getString(0x7f060054), 1).show();
                    }
                    return;
                }
                switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1))
                {
//                    default:
//                        return;
//
//                    case 10: // '\n'
//                        context.getString(0x7f06004d);
//                        return;
//
//                    case 11: // '\013'
//                        context.getString(0x7f06004c);
//                        return;
//
//                    case 12: // '\f'
//                        context.getString(0x7f06004e);
//                        return;
//
//                    case 13: // '\r'
//                        context.getString(0x7f06004b);
//                        break;
                }
            }


            {
//                super();
//                this$0 = Bluetooth_Hosting.this;
            }
        };
        discoveryResult = new BroadcastReceiver() {

            final Bluetooth_Hosting this$0;

            public void onReceive(Context context, Intent intent)
            {
                intent.getStringExtra("android.bluetooth.device.extra.NAME");
//                context = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                discoveredDevicesFound.add(context);
            }


            {
                this$0 = Bluetooth_Hosting.this;
//                super();
            }
        };
        mHandler = handler;
//        h = hosting;
        mConnectedThread = new ArrayList();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mUUID = Constants.MY_UUID;
//        hosting.registerReceiver(bluetoothState, new IntentFilter("android.bluetooth.adapter.action.SCAN_MODE_CHANGED"));
    }

    public void AcceptBluetoothRequest(boolean flag)
    {
        if (flag)
        {
            mAcceptThread = new AcceptThread(Boolean.valueOf(false));
            mAcceptThread.start();
        } else
        if (mAcceptThread != null)
        {
            mAcceptThread.cancelServer();
            return;
        }
    }

    public void OnDestroy()
    {
        Iterator iterator;
        try
        {
            mConnectThread.cancelClient();
        }
        catch (Exception exception1) { }
        for (iterator = mConnectedThread.iterator(); iterator.hasNext();)
        {
            ConnectedThread connectedthread = (ConnectedThread)iterator.next();
            try
            {
                connectedthread.cancel();
            }
            catch (Exception exception2) { }
        }

        try
        {
//            h.unregisterReceiver(discoveryResult);
            return;
        }
        catch (Exception exception)
        {
            return;
        }
    }

    public void RemoveConnection(int i)
    {
        mConnectedThread.remove(i);
    }

    public void Send(String s)
    {
//        s = s.getBytes();
        int i = 0;
        do
        {
            if (i >= mConnectedThread.size())
            {
                break;
            }
            try
            {
//                ((ConnectedThread)mConnectedThread.get(i)).write(s);
            }
            catch (Exception exception)
            {
                mConnectedThread.remove(i);
//                h.RemoveConnection(i, "");
            }
            i++;
        } while (true);
    }

    public void connected(BluetoothSocket bluetoothsocket, BluetoothDevice bluetoothdevice, String s)
    {
//        this;
//        JVM INSTR monitorenter ;
        mConnectedThread.add(new ConnectedThread(bluetoothsocket, s));
        ((ConnectedThread)mConnectedThread.get(mConnectedThread.size() - 1)).start();
//        this;
//        JVM INSTR monitorexit ;
        return;
//        bluetoothsocket;
//        throw bluetoothsocket;
    }

    public void visible()
    {
        getVisible = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
        getVisible.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 600);
//        h.startActivityForResult(getVisible, 10);
    }




}