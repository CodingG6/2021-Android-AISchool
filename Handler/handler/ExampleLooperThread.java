package com.example.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class ExampleLooperThread extends Thread {
    private static final String TAG = "ExampleLooperThread";

    public Looper looper;   // 2) declare a new public Looper.
    public Handler handler;

    @Override
    public void run() {

        // This call adds a Looper to the background Thread, and automatically creates a MessageQueue.
        Looper.prepare();

        looper = Looper.myLooper(); //  3) this returns the Looper of the current thread.

        // like this, you will get an error message:
        // "Can't create handler inside thread Thread[Thread-6,5,main] that has not called Looper.prepare()"
        handler = new ExampleHandler();

        Looper.loop();   // 1) We will expose this Looper to the outside.

        Log.d(TAG, "End of run()");

    }
}