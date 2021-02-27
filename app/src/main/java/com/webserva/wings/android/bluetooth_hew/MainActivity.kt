package com.webserva.wings.android.bluetooth_hew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent
import android.widget.TextView

val REQUEST_ENABLE_BT = 1


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val tvValid = findViewById<TextView>(R.id.tvValid)

        // device support
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            tvMessage.setText("Device doesn't support Bluetooth")
        }else{
            tvMessage.setText("Device support Bluetooth")
        }

        // valid setting
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            tvValid.setText("有効にしました。")
        }else{
            tvValid.setText("既に有効です。")
        }
    }








}












