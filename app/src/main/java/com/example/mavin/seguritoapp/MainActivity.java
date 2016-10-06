package com.example.mavin.seguritoapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mavin.seguritoapp.connect.conection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    final byte delimiter = 33;
    int readBufferPosition = 0;

    conection mConection = new conection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final android.os.Handler handler = new android.os.Handler();

        //final Handler handler = new Handler();

        final TextView myLabel = (TextView) findViewById(R.id.btResult);
        final Button conButton = (Button) findViewById(R.id.conectar);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run(){

                mConection.sendBtMsg(btMsg);

                while (!Thread.currentThread().isInterrupted()){
                    int bytesAvailable;
                    boolean workDone = false;

                    try{
                        final InputStream mmInputStream;
                        mmInputStream = mConection.getMmSocket().getInputStream();
                        bytesAvailable = mmInputStream.available();

                        if (bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("MMFundamenta recv bt","bytes available");
                            byte[] readBuffer = new byte[1024];
                            mmInputStream.read(packetBytes);

                            for (int i=0;i<bytesAvailable;i++){

                                byte b = packetBytes[i];

                                if (b == delimiter){

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            myLabel.setText(data);
                                        }
                                    });

                                    workDone = true;
                                    break;

                                }else{

                                    readBuffer[readBufferPosition++] = b;
                                }

                            }
                            if (workDone == true){
                                mConection.getMmSocket().close();
                                break;
                            }

                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }
        };

        conButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                (new Thread(new workerThread("conectar"))).start();
            }
        });


        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                if (device.getName().equals("raspberrypi")){
                    Log.e("Fundamenta",device.getName());
                    mConection.setMmDevice(device);
                    break;

                }

            }

        }


    }
}
