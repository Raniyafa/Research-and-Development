package com.nopalsoft.sharkadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity3 extends Activity {
    private EditText et_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout3);
        et_name=findViewById(R.id.et_name);
        findViewById(R.id.llyt_22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity3.this,MainActivity1.class));
            }
        });
        findViewById(R.id.llyt_33).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity3.this,MainActivity2.class));
            }
        });
        findViewById(R.id.llyt_11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_name.getText().toString().trim())){
                    Toast.makeText(MainActivity3.this, "Please Enter Your Pin Code", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity3.this, "Welcome to Draw Buddy", Toast.LENGTH_SHORT).show();
                }

               
            }
        });
    }
}
