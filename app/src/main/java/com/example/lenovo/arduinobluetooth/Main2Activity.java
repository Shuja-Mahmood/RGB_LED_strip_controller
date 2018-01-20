package com.example.lenovo.arduinobluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
//import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity {

    //region Variable Declaration
    ToggleButton ledControl;
    SeekBar brightness;
    String macAddress;
    BluetoothDevice blueDevice;
    BluetoothAdapter blueAdapter;
    BluetoothSocket blueSocket;
    AlertDialog.Builder dialog;
    //private InputStream inStream;
    private OutputStream outStream;
    boolean stopWorker;
    byte delimiter;
    int readBufferPosition;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //region Variable Assignment
        stopWorker = false;
        delimiter = 10;
        readBufferPosition = 0;
        macAddress = null;
        blueAdapter = BluetoothAdapter.getDefaultAdapter();

        brightness = findViewById(R.id.brightness);
        ledControl = findViewById(R.id.ledControl);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            macAddress = null;
            Toast.makeText(this, "MAC address not found", Toast.LENGTH_SHORT).show();
        } else {
            macAddress = extras.getString("ADDRESS");
        }
        //endregion

        Connect();

        //region UI
        ledControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    LedOn();
                } else {
                    LedOff();
                }
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sendData(Integer.toString(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        //endregion
    }

    public void Connect() {
        blueDevice = blueAdapter.getRemoteDevice(macAddress);
        Log.d("", "Connecting to ... " + blueDevice);
        blueAdapter.cancelDiscovery();

        try {
            blueSocket = blueDevice.createRfcommSocketToServiceRecord(uuid);
            blueSocket.connect();
            Toast.makeText(this, "Successfully Connected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            try {
                blueSocket.close();
                Toast.makeText(this, "Unable to Create RF Communication Socket", Toast.LENGTH_SHORT).show();
            } catch (IOException e2) {
                Toast.makeText(this, "Unable to Connect or Disconnect", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(i);
        }
        try {
            //inStream = blueSocket.getInputStream();
            outStream = blueSocket.getOutputStream();
        }catch (IOException e) {
            Toast.makeText(this, "Unable to set Input and Output Stream", Toast.LENGTH_SHORT).show();
        }
    }

    private void LedOn() { sendData("T"); }

    private void LedOff() { sendData("F"); }

    @Override
    public void onBackPressed()
    {
        dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setTitle("Disconnect")
                .setMessage("Are you sure you want to disconnect and return ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            blueSocket.close();
                            Intent i = new Intent(Main2Activity.this, MainActivity.class);
                            startActivity(i);
                        }
                        catch (IOException e)
                        {
                            Toast.makeText(Main2Activity.this, "Unable to disconnect, socket might be busy", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void sendData(String data)
    {
        byte[] buffer = data.getBytes();
        try {
            outStream.write(buffer);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to send data", Toast.LENGTH_LONG).show();
        }
    }
/*
    public void beginListenForData()   {
        try
        {
            inStream = blueSocket.getInputStream();
        }
        catch (IOException e)
        {
            Log.d("Error", "Unable to get Input Stream");
        }

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {

                                            if(Result.getText().toString().equals("..")) {
                                                Result.setText(data);
                                            } else {
                                                Result.append("\n"+data);
                                            }

	                                        	// You also can use Result.setText(data); it won't display multilines


                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
*/
}


