package com.example.android.smartlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Created by Michael on 3/11/2017.
 */

public class BtMsger {

    public BtMsger(){

    }

    public String BtReceiveMsg(String msg){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocketWrapper socket = null;
        final BluetoothConnector b;
        BluetoothDevice d = null;
        String server_response = null;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device: pairedDevices){
                if(device.getName().equals("raspberrypi")){
                    d = device;
                    break;
                }
            }
            b = new BluetoothConnector(d, false, mBluetoothAdapter, null);
            try {
                socket = b.connect();
                server_response = sendBtMsg(msg, socket);
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return server_response;
    }

    public String sendBtMsg(String msg2send, BluetoothSocketWrapper socket){
        byte[] buffer = new byte[256];
        int bytes;
        try{
            OutputStream mmOut = socket.getOutputStream();
            mmOut.write(msg2send.getBytes());
            InputStream mmIn = socket.getInputStream();
            DataInputStream in = new DataInputStream(mmIn);
            bytes = in.read(buffer);
            String readMess = new String(buffer, 0, bytes);
            return readMess;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
