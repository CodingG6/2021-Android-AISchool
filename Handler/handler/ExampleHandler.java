package com.example.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ExampleHandler extends Handler {

    public static final String TAG = "ExampleHandler";

    // instead of hard-coding the numbers, it is more common to create constants for what.
    public static final int TASK_A = 1;
    public static final int TASK_B = 2;

    @Override
    public void handleMessage(Message msg) {
        // super.handleMessage(msg); // by default this method doesn't do anything.

        switch (msg.what) {
            case TASK_A:
                Log.d(TAG, "Task A executed");
                break;
            case TASK_B:
                Log.d(TAG, "Task B executed");
                break;
        }
    }
}
