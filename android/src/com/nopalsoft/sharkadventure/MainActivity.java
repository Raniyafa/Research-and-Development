package com.nopalsoft.sharkadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText et_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        et_name=findViewById(R.id.et_name);

        findViewById(R.id.llyt_11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_name.getText().toString().trim())){
                    Toast.makeText(MainActivity.this, "Please Enter Your UserName", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(MainActivity.this,MainActivity3.class));
                }

               
            }
        });
    }
}
