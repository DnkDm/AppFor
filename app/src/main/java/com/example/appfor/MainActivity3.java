package com.example.appfor;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity3 extends AppCompatActivity {

    private Button buttonReb;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        buttonReb = findViewById(R.id.buttonReb);
        buttonReb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*MainActivity.score = 0;
                MainActivity.kod = "00000";
                MainActivity.editor.putInt("myInt", MainActivity.score);
                MainActivity.editor.apply();
                MainActivity.editor2.putString("myString", MainActivity.kod);
                MainActivity.editor2.apply();*/

                MainActivity.rebutMap(0,"00000");

                onBackPressed();
            }
        });

        MainActivity.score = 0;


        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}