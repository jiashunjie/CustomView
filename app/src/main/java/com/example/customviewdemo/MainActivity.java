package com.example.customviewdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.customviewdemo.widget.ProgressView;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
private ProgressView pv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    //设置修改状态栏
                    window.setStatusBarColor(getResources().getColor(R.color.silver));
                }
            }
        });

        pv = findViewById(R.id.pv);
        pv.setMaxCount(100);
        pv.setCurrentCount(55);
        pv.setCrrentLevel("安全");
        pv.setScore(87);
    }
}
