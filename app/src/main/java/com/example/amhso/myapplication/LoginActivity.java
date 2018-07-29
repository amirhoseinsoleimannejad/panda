package com.example.amhso.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {



    private Button button;
    private String username;
    private String password;






    @Override
    public void onBackPressed() {

        finishAffinity();
        System.exit(0);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        Button SendButton = (Button) findViewById(R.id.send);




        Bundle bundle = this.getIntent().getExtras();
        if(bundle!= null)
        {
            boolean isActivityToBeFinish =  this.getIntent().getExtras().getBoolean("finishstatus");
            if(isActivityToBeFinish)
            {

                this.finish();
            }
        }

        // Capture button clicks
        SendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {



                EditText Eusername=(EditText) findViewById(R.id.user);
                EditText Epassword=(EditText) findViewById(R.id.pass);




                username=Eusername.getText().toString();
                password=Epassword.getText().toString();


                SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(G.username,username);
                editor.putString(G.password,password);
                editor.commit();



                new sendhttp(G.CheckLoginUrl+"?user="+username+"&pass="+password,"aaa",username,password,LoginActivity.this).execute();


            }
        });




    }




}
