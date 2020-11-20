package com.example.marco.smart_green_house;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.bluetooth.*;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {


    final int ENABLE_BT_REQ = 1;
    final String BT_TARGET_NAME = "DSD TECH HC-05";
    private static final String APP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID uuid = null;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice targetDevice = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private Set <BluetoothDevice> pairedList;
    private Set <BluetoothDevice> nbDevices = null;
    private ArrayList<String> devices=new ArrayList<String>();
    private ArrayAdapter<String> spinnerAdapter;
    private TextView txtErr;
    private Spinner spinner;
    private String address = null;
    private Button btOn = null;
    private Button btDis = null;
    private ProgressDialog progress;
    private TextView txtUmidita = null;
    private TextView txtIntensity = null;
    private TextView txtVal = null;
    private SeekBar bar = null;
    private Handler handler = new Handler();
    private Boolean stopThread = false;
    private String string = "";

    private final BroadcastReceiver br = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context,Intent intent ){
        BluetoothDevice device = null;
        if( BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            nbDevices.add(device);
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedList = btAdapter.getBondedDevices();
        setContentView(R.layout.activity_main);
        txtErr = (TextView) this.findViewById(R.id.txtErr);
        btDis = (Button) this.findViewById(R.id.Disattiva);
        btOn = (Button)this.findViewById(R.id.Attiva);
        spinner = (Spinner) this.findViewById(R.id.Device);
        txtUmidita = (TextView) this.findViewById(R.id.Humidity);
        txtVal = (TextView) this.findViewById(R.id.txtVal);
        txtIntensity = (TextView) this.findViewById(R.id.Intensity);
        bar = (SeekBar) this.findViewById(R.id.seekBar);
        txtErr.setVisibility(View.INVISIBLE);
        btDis.setVisibility(View.INVISIBLE);
        btOn.setVisibility(View.INVISIBLE);
        txtUmidita.setVisibility(View.INVISIBLE);
        txtVal.setVisibility(View.INVISIBLE);
        txtIntensity.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);


        if( btAdapter == null ) {
            Log.e("smart_green_house", "BT is not available on this device");
            finish();
        }
        if (!btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),ENABLE_BT_REQ);
        } else if(btAdapter.isEnabled()){
            devices.add("Seleziona il dispositivo");

            for (BluetoothDevice dv:pairedList) {
                devices.add(dv.getName());

                if(dv.getName().equals(BT_TARGET_NAME)) {
                    targetDevice = dv;
                    address = targetDevice.getAddress().substring(targetDevice.getAddress().length() - 17);
                }
            }
        }


    }

   @Override
    public void onStart() {
        super.onStart();
        if(btAdapter.isEnabled()) {
            btAdapter.startDiscovery();
            spinnerAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            spinnerAdapter.addAll(devices);
            spinner.setAdapter(spinnerAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (devices.get(position).equals(BT_TARGET_NAME)){
                        uuid = UUID.fromString(APP_UUID);

                        new ConnectBT().execute();
                        txtErr.setVisibility(View.INVISIBLE);
                        btDis.setVisibility(View.VISIBLE);
                        btOn.setVisibility(View.VISIBLE);
                        txtUmidita.setVisibility(View.VISIBLE);
                        txtIntensity.setVisibility(View.VISIBLE);
                        txtVal.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.VISIBLE);
                        spinner.setEnabled(false);
                    } else if (devices.get(position).equals("Seleziona il dispositivo")) {
                    } else {
                        txtErr.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(btSocket != null) {
                sendMessage("o");
            }
            }
        });

       btDis.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           if(btSocket != null) {
               sendMessage("c");
           }
           }
       });


       bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           }
           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
               try
               {
                   btSocket.getOutputStream().write(String.valueOf(seekBar.getProgress()+10).concat(":").getBytes());
               }
               catch (IOException e)
               {

               }
           }
       });
    }

    @Override
    public void onStop () {
        super.onStop();
        btAdapter.cancelDiscovery();
    }

    public void onDestroy () {
        super.onDestroy();
        disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disconnect();
    }

    private void sendMessage(String msg){
        try {
            btSocket.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = btAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(uuid);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }

            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;

                try {
                    leggi(btSocket.getInputStream()).start();
                    btSocket.getOutputStream().write("b".toString().getBytes());
                    msg("start");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            progress.dismiss();
        }
    }

    private Thread leggi(final InputStream in){
        Thread thread = new Thread(new Runnable() {
            public void run(){
                while(!Thread.currentThread().isInterrupted() && !stopThread){
                    try{
                        int byteCount = in.available();
                        if(byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            in.read(rawBytes);
                            string += new String(rawBytes, "UTF-8");
                            if (!string.matches(".*\\d.*")) {
                                handler.post(new Runnable() {
                                    public void run() {

                                        txtVal.setText(string);
                                        string="";

                                    }
                                });

                            }
                        }
                    } catch(IOException e){
                        stopThread = true;
                    }
                }
            }
        });
        return thread;
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    private void disconnect(){
        if(btSocket != null){
            try{
                sendMessage("n");
                isBtConnected = false;
                stopThread = false;
                btSocket.getInputStream().close();
                btSocket.close();
            } catch (IOException e){
                msg(e.getMessage());
            }
        }
        finish();
    }

}
