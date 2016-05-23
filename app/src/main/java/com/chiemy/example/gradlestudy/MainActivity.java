package com.chiemy.example.gradlestudy;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuilder builder = new StringBuilder();
        builder.append("Applicatioin Id = " + BuildConfig.APPLICATION_ID + "\n\n");
        builder.append("Version Name = " + BuildConfig.VERSION_NAME + "\n\n");
        builder.append("Version Code = " + BuildConfig.VERSION_CODE + "\n\n");
        builder.append("Build Type = " + BuildConfig.BUILD_TYPE + "\n\n");
        builder.append("Flavor = " + BuildConfig.FLAVOR + "\n\n");
        builder.append("API Url = " + BuildConfig.API_URL + "\n\n");


        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(builder.toString());
    }


}
