package com.dingqiqi.testadvergif;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private GifView mGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGifView = (GifView) findViewById(R.id.gv_advertising);
        mGifView.setBackgroundColor(Color.GREEN);

        try {
            mGifView.setGifStream(getResources().getAssets().open("b.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
