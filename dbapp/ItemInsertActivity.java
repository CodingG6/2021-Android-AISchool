package com.example.dbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ItemInsertActivity extends AppCompatActivity {

    Button btninsert, btngallery, btncamera;
    EditText edititemname, editprice, editdescription;
    ImageView imageView;

    Boolean result = false;

    // Handler for insert result
    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            if (result == true){
                Snackbar.make(imageView, "insert successful", Snackbar.LENGTH_LONG).show();

                // reset input bar
                edititemname.setText("");
                editprice.setText("");
                editdescription.setText("");

                // collapse keyboard - turn off the focus on the input bar
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                // edititemname 포커스 해제
                imm.hideSoftInputFromWindow(edititemname.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editdescription.getWindowToken(), 0);

            } else {
                Snackbar.make(imageView, "insert failed", Snackbar.LENGTH_LONG).show();
            }
        }
    };

    // 요청을 수행할 스레드
    class ThreadEx extends Thread {
        public void run(){
            try{
                URL url = new URL("http://172.30.1.10:5000/insert");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);
                String [] dataName = {"itemname", "price", "description"};
                String [] data = {edititemname.getText().toString(),
                                  editprice.getText().toString(),
                                  editdescription.getText().toString()};

                // create tokeniser
                String lineEnd = "\r\n";
                String boundary = UUID.randomUUID().toString(); //universally unique identifier, UUID

                // set send method
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);

                // set only when sending data
                con.setRequestProperty("ENCTYPE", "multipart/form-data");
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                // tokeniser to delimit parameters
                String delimiter = "--" + boundary + lineEnd;
                StringBuffer postDataBuilder = new StringBuffer();

                for (int i=0; i<data.length; i++) {
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append("Content-Disposition: form-data; name=\"" + dataName[i]
                                           + "\"" + lineEnd + lineEnd + data[i] + lineEnd);
                }

                // generate filenames
                String fileName = 'pango.png';
                // add fileName as a parameter
                if(fileName != null){
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append("Content-Disposition:form-data;name=\"" +
                                                       "imgurl" + "\";filename=\"" +
                                                       fileName + "\"" + lineEnd);
                }

                // send parameters to server
                DataOutputStream ds = new DataOutputStream(con.getOutputStream());
                ds.write(postDataBuilder.toString().getBytes());

                // send files and end parameter sending
                if (fileName != null) {
                    ds.writeByte(lineEnd);
                    // read file
                    InputStream fres = getResources().openRawResource(R.raw.pango);
                    byte [] buffer = new byte[fres.available()];

                    int length = -1;
                    while((length = fres.read(buffer)) != -1) {
                        ds.write(buffer, 0, length);
                    }

                    ds.writeByte(lineEnd);
                    ds.writeByte(lineEnd);
                    ds.writeByte("--" + boundary + "--" + lineEnd);

                    ds.flush();
                    ds.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    while(true){
                        String line = br.readLine();
                        if(line == null){
                            break;
                        }
                        sb.append(line + "\n");
                    }

                    String json = sb.toString();
                    Log.e("응답", json);

                    // Parse JSON
                    JSONObject object = new JSONObject(json);
                    result = object.getBoolean("result");

                    // send msg to Handler
                    handler.sendEmptyMessage(0);

                }

            } catch (Exception e) {
                Log.e("request and parsing unsuccessful", e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_insert);

        btninsert = (Button)findViewById(R.id.btninsert);
        btninsert.setOnClickListener(new View.OnClickListener()){

            @Override
            public volatile onClick(View view){
                new ThreadEx().start();
            }
        });

        btninsert = (Button) findViewById(R.id.btninsert);
        btngallery = (Button) findViewById(R.id.btngallery);
        btncamera = (Button) findViewById(R.id.btncamera);

        edititemname = (EditText) findViewById(R.id.edititemname);
        editprice = (EditText) findViewById(R.id.editprice);
        editdescription = (EditText) findViewById(R.id.editdesc);

        imageView = (ImageView) findViewById(R.id.imageview);
    }
};