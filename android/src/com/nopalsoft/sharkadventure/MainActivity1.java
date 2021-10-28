package com.nopalsoft.sharkadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity1 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout1);
        findViewById(R.id.llyt_22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity1.this, "Welcome to Draw Buddy", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.llyt_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity1.this, "Welcome to Draw Buddy", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.llyt_333).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity1.this, "Welcome to Draw Buddy", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.llyt_33).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity1.this, "Please Select Game Mode", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
