package com.example.dbapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

public class ItemAdapter extends BaseAdapter{
    // Context needed to display View - pass in Activity
    Context context;
    //data for ListView
    List<Item> data;
    // variable to store cell id
    int layout;
    // variable that converts xml content into View class
    LayoutInflater inflater;

    // constructor
    public ItemAdapter(Context context, List<Item> data, int layout){
        this.context = context;
        this.data = data;
        this.layout = layout;

        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    // set row number. = iteration frequency
    @Override
    public int getCount(){
        return data.size();
    }

    // set string
    @Override
    public Object getItem(int i){
        return data.get(i).getItemid();
    }

    @Override
    public long getItemId(int i){
        return (long) i;
    }

    ImageView imageView;
    Bitmap bit;

    // handler that displays downloaded image
    Handler imageHandler = new Handler(Looper.getMainLooper()) {
        // sends Map to msg obj
        // sends to map: {imageview: ImageView, bit: Bitmap}

        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.obj;
            ImageView imageView = (ImageView) map.get("imageview");
            Bitmap bit = (Bitmap) map.get("bit");
            imageView.setImageBitmap(bit);
        }
    };

    // thread for downloads
    class ImageThread extends Thread{
        String imagename;
        ImageView imageView;

        public void run(){
            try {
                // create a stream for image downloads
                InputStream inputStream = new URL("http://172.30.1.10:5000/imagedownload/" + imagename).openStream();
                Bitmap bit = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                Message msg = new Message();
                Map<String, Object> map = new HashMap<String, Object>();

                map.put("bit", bit);
                map.put("imageview", imageView);

                msg.obj = map;
                // call Handler along with Message
                imageHandler.sendMessage(msg);

            } catch (Exception e) {
                Log.e("Download image", "failed");
            }
        }
    };

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        // 출력할 셀 생성?
        // 먼저 출력할 셀 가지고 생성?
        View returnView = view;
        // 출력한 적이 없다면 직접 생성?
        if(returnView == null){
            returnView = inflater.inflate(layout, viewGroup, false);
        }

        // i = index
        TextView itemname = (TextView) returnView.findViewById(R.id.itemname);
        itemname.setText(data.get(i).getItemname());

        TextView price = (TextView) returnView.findViewById(R.id.price);
        price.setText(data.get(i).getPrice() + "won");

        TextView description = (TextView) returnView.findViewById(R.id.desc);
        description.setText(data.get(i).getDescription());

        ImageView imageView = (ImageView) returnView.findViewById(R.id.itemimage);
        ImageThread th = new ImageThread();
        th.imagename = data.get(i).getImgurl();
        th.imageView = imageView;
        th.start();

        return returnView;
    }
}