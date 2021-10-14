package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int value = 0;
    TextView txt;

    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            if(msg.what==0){
                txt.setText("Value: " + value);
            }
        }
    };

    class BackThread extends Thread {
        public void run() {
            while (value < 20){
                value++;
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e){ return; }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.idTVtxt);
        Button increase = (Button) findViewById(R.id.idBtnUpdate);

        increase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Thread th = new BackThread();
                th.setDaemon(true);
                th.start();
            }

        });

    }
}