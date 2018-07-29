package com.example.amhso.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity {




    private String username;
    private String password;
    private SharedPreferences sharedpreferences;
    private int checklogin;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.activity = MainActivity.this;
        setContentView(R.layout.activity_main);



        sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
//        sharedpreferences.edit().remove(G.login).commit();
        checklogin = sharedpreferences.getInt(G.login ,0);




        // Start NewActivity.class
        if(isInternetOn() && checklogin==0) {
            Intent myIntent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(myIntent);
        }


        else if(isInternetOn() && checklogin==1){
            Intent myIntent = new Intent(MainActivity.this,
                    MapviewActivity.class);
            startActivity(myIntent);
        }
        else if(isInternetOn() && checklogin==2){
            Intent myIntent = new Intent(MainActivity.this,
                    DriverActivity.class);
            startActivity(myIntent);
        }
        else{
            Toast.makeText(MainActivity.this, "ابتدا اینترنت را وصل کنید باتشکر", Toast.LENGTH_LONG).show();

            this.finish();
        }









    }






    


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

//            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {


            return false;
        }
        return false;
    }



















}
