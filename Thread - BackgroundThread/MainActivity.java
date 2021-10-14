package com.example.backgroundthread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button startBtn, stopBtn;

    private Handler mainHandler = new Handler();  // Android-specific class

    private volatile boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.idBTNstart_thread);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                stopThread = false;
                /*
                ExampleRunnable runnable = new ExampleRunnable(10);
                runnable.run();  // we can still make the main thread work this way.
                                 // or run the work on the current thread.
                new Thread(runnable).start();

               // ExampleThread thread = new ExampleThread(20);
               // thread.start();
                 */

                // Approach 4.
                // Instead of creating a separate class that implements Runnable,
                // we can also use an annonymous inner class.
                ExampleRunnable runnable = new ExampleRunnable(15);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }).start();

            }
        });

        stopBtn = findViewById(R.id.idBTNstop_thread);
        stopBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                stopThread = true;
                startBtn.setText("START");
            }
        });

    }

    class ExampleThread extends Thread {
        int seconds;

        ExampleThread(int seconds){
            this.seconds = seconds;
        }

        @Override
        public void run(){
            for (int i = 0; i < seconds; i++) {
                Log.d(TAG, "thread class" + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ExampleRunnable implements Runnable {
        int seconds;

        ExampleRunnable(int seconds){
            this.seconds = seconds;
        }

        ExampleThread buttonStartThread = new ExampleThread(15);

        @Override
        public void run(){
            for (int i = 0; i < seconds; i++) {

                if (stopThread)
                    return;

                /* Approach 1.
                Handler threadHandler = new Handler();  // this handler is tied to the background Thread. Let's associate it with the main (UI) thread.
                                                           // we can achieve it by passing the UI Looper to the constructor.
                Handler threadHandler = new Handler(Looper.getMainLooper()); // now associated with the main (UI) Looper.

                // startBtn.setText("50%");
                int finalI = i;
                threadHandler.post(new Runnable() {
                @Override
                public void run() {
                    startBtn.setText(finalI * 10 + "%");
                    }
                }); */

                /* Approach 2.
                int finalI = i;

                startBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        startBtn.setText(finalI *10 + "%");
                    }
                });     */

                // Approach 3.
                int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startBtn.setText(finalI *10 + "%");
                    }
                });

                Log.d(TAG, "thread with Runnable: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    startBtn.setText("START");
                }
            });
        }
    }
}
