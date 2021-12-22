package com.traidev.kaisebhi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import static com.traidev.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class ViewPic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic);
        ImageView img = findViewById(R.id.fullImage);

        Bundle extras = getIntent().getExtras();


        if (extras != null) {

            Glide.with(this).load(BASE_URL + "qimg/"+ extras.getString("photourl")).fitCenter().into(img);

        }

    }
}