package com.example.emailintentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextTo;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextTo = findViewById(R.id.idETto);
        mEditTextSubject = findViewById(R.id.idETsubject);
        mEditTextMessage = findViewById(R.id.idETmessage);

        // As for the button, we will access it only inside this onCreate method.
        // So it doesn't need to be a member variable of this Activity.

        Button btnSend = findViewById(R.id.idBTNsend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });
    }

    // We need to get the text from our EditText,
    // and hand it over to the email client.
    // 'to' section should be able to have multiple email addresses divided by a comma.
    private void sendMail() {
        String recepientList = mEditTextTo.getText().toString();
        String[] recipients = recepientList.split(",");

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients); // only takes a String array.
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");  // this line will take care of only opening clients.
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}