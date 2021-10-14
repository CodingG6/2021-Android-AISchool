package com.example.dbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * json파싱을 위한 선언부
     **/
    // 다운로드받은 문자열을 저장할 변수를 선언
    String json;
    // 파싱한 결과가 여러개 이므로 List선언
    List<Item> itemList;


    /**
     * View를 위한 선언부
     **/
    // 목록을 출력할 ListView
    ListView listView;

    // 데이터와 뷰를 이어줄 Adapter 변수
    // ArrayAdapter<Item> itemAdapter;
    ItemAdapter itemAdapter;

    // 진행 상황을 출력하 프로그래스 바
    ProgressBar downloadView;

    // 화면 갱신을 위한 Handler 객체생성
    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            // 이 곳에 화면 갱신 내용을 작성

            // 어댑터가 연결된 View에게 데이터가 갱신되었음을 알리고
            // 다시 출력하라고 하는 코드
            itemAdapter.notifyDataSetChanged();
            downloadView.setVisibility(View.GONE);
        }
    };

    // 다운로드 받을 Thread 클래스
    class ItemThread extends Thread {
        public void run(){
            try {
                URL url = new URL("http://172.30.1.54:5000/item");
                // 자바는 객체지향의 추상클래스를 이해한다면, 형변환을 해야합니다.
                // url.openConnection()는 URLConnection라는 공통된 추상 클래스를 반환하는데
                // 이를 HttpURLConnection으로 형변환 해줘야 합니다.
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                // 옵션 설정
                // cache 를 사용하겠다고 설정하면 이전 다운로드 받은 데이터를 재활용
                con.setUseCaches(false);
                // 연결 제한 시간 설정
                con.setConnectTimeout(30000);
                // 연결과 스트림은 다른것
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                // 변할 수 있는 문자열을 저장하는 객체
                // String 클래스는 변할 수 없는 문자열을 저장했었습니다.
                // 기존 내용에 새로운 내용을 추가하면 메모를 다시 할당받아서
                // 메모리 낭비가 생기기 때문에 이 방법을 사용합니다.
                // 사실 1.7버전부터 String도 StringBuilder처럼 작동하므로 이렇게 하지 않아도 됩니다.
                StringBuilder sb = new StringBuilder();
                while(true){
                    // 한 줄 읽어오기
                    // 읽으면 읽은 내용을 line에 저장하고 읽  은게 없으면 null
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }

                    // 읽은 내용을 sb에 추가
                    // 대소문자변환이나 좌우공백제거(trim)를 해주는 것이 좋습니다.
                    // 대소문자는 대문자, 소문자 다른 처리를 하게 될 수도 있기 때문에
                    // 대문자 혹은 소문자로 변환하여 처리하는 것이 좋습니다.
                    sb.append(line.trim());
                }

                // 읽어온 문자열을 저장
                json = sb.toString();

                Log.e("받아온 데이터", json);

                // 데이터를 받고 나면, 연결을 유지할 필요가 없으므로
                // 스트림을 닫고 연결을 해제
                br.close();
                con.disconnect();


                // json 파싱 : 데이터에 대한 이해가 필요
                // 자바에서 json파싱에는 JSONArray 와 JSONObject 를 이용
                // 파이썬은 바로 변환이 가능합니다.
                // {} -> dict, [] -> list로 자동 변환 가능

                // 데이터 전체를 객체로 변환
                JSONObject obj = new JSONObject(json);

                // 객체 내에서 data라는 키의 배열을 추출
                JSONArray ar = obj.getJSONArray("data");

                // 배열을 순회
                int i = 0;
                while(i < ar.length()){
                    // 배열의 요소 가져오기
                    // 객체는 key로 요소를 가져오지만, 배열은 인덱스로 요소를 가져옵니다.
                    JSONObject object = ar.getJSONObject(i);
                    // DTO 클래스의 객체를 생성
                    Item item = new Item();
                    item.setItemid(object.getInt("item"));
                    item.setItemname(object.getString("itemname"));
                    item.setPrice(object.getInt("price"));
                    item.setImgurl(object.getString("imgurl"));
                    item.setDescription(object.getString("description"));
                    Log.e("item", object.getInt("item")+ "");

                    // list에 추가
                    itemList.add(item);
                    i = i + 1;
                }
                Log.e("파싱 결과", itemList.toString());

                // 핸들러에게 메시지 전송
                handler.sendEmptyMessage(0);

            }catch (Exception e){
                // console창으로 메시지를 확인
                // 태그, getLocalizedMessage()
                Log.e("다운로드 또는 파싱 실패", e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 초기화 작업
        itemList = new ArrayList<>();
        // xml파일에 디자인한 뷰 가져오기
        listView = (ListView)findViewById(R.id.listview);
        downloadView = (ProgressBar)findViewById(R.id.downloadview);

        // this, 행의 모양, 데이터
        itemAdapter = new ItemAdapter(this, itemList, R.layout.item_cell);
        listView.setAdapter(itemAdapter);

        // 색을 만들고 높이를 지정해야 합니다. 이 순서는 바뀌면 안됩니다.
        listView.setDivider(new ColorDrawable(Color.RED)); //가로줄 색 변경
        listView.setDividerHeight(3); // 가로줄 Height 크기 지정

        // Thread를 만들어서 실행
        new ItemThread().start();
    }

}


/*

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    // variable to store downloaded string
    String json;

    // store parsed result
    List<Item> itemList;

    // Create a ListView
    ListView listView;

    // Adapter to connect data and view
    ArrayAdapter<Item> itemArrayAdapter;

    // progress bar for status check
    ProgressBar downloadView;

    // Handler object to refresh page
    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            // description of refreshed page.

            // adapter notifies view of data updates
            // and tells it to refresh.
            itemArrayAdapter.notifyDataSetChanged();
            downloadView.setVisibility(View.GONE);
        }
    };



    // thread class for downloads
    class ItemThread extends Thread {
        public void run(){
            try {
                // create url for download
                URL url = new URL("http://172.30.1.10:5000/item");
                // connect
                // casting. further reading on abstract class recommended.
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // set options
                // if use cache is true, previously downloaded data will be reused.
                // might not be the best option for you depending on what you do.
                con.setUseCaches(false);
                // set timeout
                con.setConnectTimeout(30000);

                // create stringstream
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                // object that stores string which "VARIABLE"
                // String class stores invariable string.
                // if something is added to existing content, memory is re-assigned.
                // SB is used to prevent the waste of resource
                StringBuilder sb = new StringBuilder();

                // read string
                // we don't know how much, so interate using while
                while(true){
                    // read one line
                    // store that line, if none, null.
                    String line = br.readLine();

                    // if line is null, it means we've read everything.
                    if(line == null){
                        break;
                    }

                    // add what's read to sb.
                    sb.append(line.trim());
                }

                // store whatt br reads in json.
                json = sb.toString();

                Log.e("downloaded data", json);

                // JSON parsing - need to understand your data.
                // in java, JSONArray and JSONObject are used to parse JSON.
                // python can converts json itself.
                // automatic conversion: {} -> dict, [] -> list

                // convert the data into an object.
                JSONObject obj = new JSONObject(json);
                // extract the array of keys from the data object.
                JSONArray arr = obj.getJSONArray("data");

                // iterate over the array.
                int i = 0;
                while(i< arr.length()){
                    // get array items
                    JSONObject object = arr.getJSONObject(i);   // locate array items by index.
                    Item item = new Item();
                    item.setItemid(object.getInt("item"));
                    Log.e("item", object.getInt("item") + "");
                    item.setItemname(object.getString("itemname"));
                    Log.e("itemname", object.getString("itemname") + "");
                    item.setPrice(object.getInt("price"));
                    Log.e("price", object.getInt("price") + "");
                    item.setDescription(object.getString("description"));
                    Log.e("description", object.getString("description") + "");
                    item.setImgurl(obj.getString("imgurl"));
                    Log.e("imgurl", object.getString("imgurl") + "");

                    // add to the list
                    itemList.add(item);

                    i = i + 1;

                }

                Log.e("parsing result: ", itemList.toString());

                // send msg to handler
                handler.sendEmptyMessage(0);

                // close stream and disconnect
                br.close();
                con.disconnect();

            } catch (Exception e) {
                // check message on console
                Log.e("Download or parsing failed", e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise
        // itemList = new ArrayList<>();    // itemList object in this thread class was created to check the data. Not needed any longer after everything is set.
        // get view designed in .xml
        ArrayAdapter<Item> itemAdapter;
        listView = (ListView)findViewById(R.id.listview);
        downloadView = (ProgressBar)findViewById(R.id.downloadview);

        // connect listView and itemList
        itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(itemAdapter);

        listView.setDivider(new ColorDrawable(Color.RED));
        listView.setDividerHeight(3);

        // Create and run a thread. the server should be up running.
        new ItemThread().start();

    }

}


 */