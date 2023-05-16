package com.example.appfor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    TextView textView3;
    Button button;
    LinearLayout linerL;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textView2);
        imageView = findViewById(R.id.image2);
        textView3 = findViewById(R.id.textView3);
        button = findViewById(R.id.backButton);
        linerL = findViewById(R.id.Liner);

        Intent intent = getIntent();
        String string = intent.getStringExtra("STRING");
        String stringOp = intent.getStringExtra("STRING2");
        int imageInt = intent.getIntExtra("IMAGE",0);

        if (string.equals("УРГЭУ"))
            linerL.setBackgroundResource(R.drawable.yrt);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageInt);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

// Закругление краев
        float roundPx = (float) bitmap.getWidth() * 0.06f; // меняйте 0.06f на нужный вам радиус скругления
        roundedBitmapDrawable.setCornerRadius(roundPx);


        textView.setText(string);
        textView3.setText(stringOp);
        imageView.setImageDrawable(roundedBitmapDrawable);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}