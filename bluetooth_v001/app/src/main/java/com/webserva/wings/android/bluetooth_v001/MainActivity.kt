package com.webserva.wings.android.bluetooth_v001

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btSwitch = findViewById<Button>(R.id.btSwitch)
        val listener = SwitchListener()
        btSwitch.setOnClickListener(listener)


    }


    private inner class SwitchListener : View.OnClickListener{
        override fun onClick(view: View) {

            var msg :String = ""

            // ON/OFF 表示のビューを獲得
            val disp = findViewById<TextView>(R.id.tvLed)
            // その文字列を獲得
            var inputStr = disp.text.toString()
            // もし、ONならOFFに、逆も然り。
            if (inputStr == "ON"){
                msg = "OFF"
            }else{
                msg = "ON"
            }
            // ONOFFを表示
            disp.text = msg

        }
    }
}