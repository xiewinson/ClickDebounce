package io.github.xiewinson.clickdebounce.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.github.xiewinson.clickdebounce.ClickHelper;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "demo";

    Button mBtn0;
    Button mBtn1;
    Button mBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn0 = findViewById(R.id.btn0);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);

        mBtn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickHelper.debounce()) {
                    Log.i(TAG, "click 过滤");

                    return;
                }
                Log.i(TAG, "click button0");
            }
        });

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickHelper.debounce()) {
                    return;
                }
                Log.i(TAG, "click button1");
            }
        });

        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickHelper.debounce()) {
                    return;
                }
                Log.i(TAG, "click button2");
            }
        });
    }
}
