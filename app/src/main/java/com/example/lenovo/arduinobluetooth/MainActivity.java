package com.example.lenovo.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //region Variable Declaration
    Switch btOnOff;
    ListView allDevices;
    String macAddress;
    BluetoothAdapter blueAdapter;
    Set<BluetoothDevice> pairedDevices;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Variable Assignment
        btOnOff = findViewById(R.id.btOnOff);
        allDevices = findViewById(R.id.allDevices);
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        //endregion

        checkBluetooth();
        //region UI
        btOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    if (blueAdapter == null)
                    {
                        Toast.makeText(MainActivity.this, "This Device Does Not Support Bluetooth", Toast.LENGTH_LONG).show();
                    }
                    else if (!blueAdapter.isEnabled())
                    {
                        Intent turnBlueOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnBlueOn, 0);
                    }
                }
                else if (blueAdapter.isEnabled())
                {
                    blueAdapter.disable();
                    showNoDevices();
                }
            }
        });
        //endregion
    }
    private void checkBluetooth()
    {
        if (blueAdapter.isEnabled())
        {
            pairedDevicesList();
            btOnOff.setChecked(true);
        }
        else
        {
            showNoDevices();
        }
    }
    private void showNoDevices()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("Turn on bluetooth to see paired devices");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        allDevices.setAdapter(adapter);

    }
    private void pairedDevicesList()
    {
        pairedDevices = blueAdapter.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();
        if (pairedDevices.size() > 0)
        {
            for(BluetoothDevice bt: pairedDevices)
            {
                list.add(bt.getName().trim() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(this, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        allDevices.setAdapter(adapter);
        allDevices.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String info = ((TextView) view).getText().toString();
                macAddress = info.substring(info.length() - 17);

                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                i.putExtra("ADDRESS", macAddress);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT).show();
            pairedDevicesList();
        }
        if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            btOnOff.setChecked(false);
        }
    }
}
