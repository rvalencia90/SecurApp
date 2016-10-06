package com.example.mavin.seguritoapp.connect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by telusm on 06-10-2016.
 */

public class conection {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    public BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    public void setMmSocket(BluetoothSocket mmSocket) {
        this.mmSocket = mmSocket;
    }

    public BluetoothDevice getMmDevice() {
        return mmDevice;
    }

    public void setMmDevice(BluetoothDevice mmDevice) {
        this.mmDevice = mmDevice;
    }


    public void sendBtMsg(String msg2send){

        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //ID Raspberry

        try{
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            if(!mmSocket.isConnected()){
                mmSocket.connect();
            }
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg2send.getBytes());

        }catch (IOException e){
            e.printStackTrace();

        }

    }
}
