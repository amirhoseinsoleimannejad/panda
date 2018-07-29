package com.example.amhso.myapplication;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;


public class HelpActivity extends AppCompatActivity {



    private Button button;
    private String username;
    private String password;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        G.activity = HelpActivity.this;

    }



















}
