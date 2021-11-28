package com.example.ex401;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{

    EditText et_url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_url = findViewById(R.id.et_url);
    }

    public void clickOpenURL(View V)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        String link = et_url.getText().toString();
        if (!link.startsWith("https://"))
        {
            link = "https://" + link;
        }
        Uri uri = Uri.parse(link);
        intent.setData(uri);
        startActivity(intent);

    }
}