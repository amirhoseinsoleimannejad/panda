package com.example.amhso.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by amhso on 21/09/2017.
 */

public class sendhttp extends AsyncTask<String, String, String>{
    public String text;
    public String  data;
    public String urlstring;
    public String user;
    public String pass;
    public Boolean backpost=false;
    public Context context;
    public LoginActivity loginActivity;

    public ProgressDialog progressDialog;


    private Activity activity;



    public sendhttp(String urlstring,String data,String user,String pass,Activity activity){


        this.data=data;
        this.urlstring=urlstring;
        this.user=user;
        this.pass=pass;
        this.activity=activity;




    }




    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
        this.text=result;

        String text2[];
        text2=this.text.split(":");
//
//        Toast.makeText(activity,this.text, Toast.LENGTH_LONG).show();

        progressDialog.dismiss();

        if(text2[0].equals("1")){//visitore


            SharedPreferences sharedpreferences = this.activity.getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(G.login,1);
            editor.putString(G.id_user,text2[1]);
            editor.commit();


            Intent myIntent = new Intent(this.activity,MapviewActivity.class);
            this.activity.startActivity(myIntent);
        }




        else if(text2[0].equals("2")){//driver

            SharedPreferences sharedpreferences = this.activity.getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(G.login,2);
            editor.putString(G.id_user,text2[1]);
            editor.commit();


            Intent myIntent = new Intent(this.activity,DriverActivity.class);
            this.activity.startActivity(myIntent);

        }


        else{
            Toast.makeText(this.activity,"مشخصات وارد شده اشتباه است.", Toast.LENGTH_LONG).show();

        }





    }

    @Override
    protected void onPreExecute() {

        progressDialog = ProgressDialog.show(activity,
                "لطفاً منتظر بمانید",
                "با تشکر");
    }

    @Override
    protected String doInBackground(String... params) {


//        ProgressDialog.show(this.activity,
//                "در حال تایید ",
//                "چند دقیقه منتظر بمانید");



//        Toast.makeText(this.activity,this.urlstring, Toast.LENGTH_LONG).show();

        try {

           URL url = new URL(this.urlstring);





            // Send POST data request

            HttpURLConnection conn = null;

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = null;

            conn.setRequestProperty(
                    "Authorization",
                    "Basic " + Base64.encodeToString((this.user+":"+this.pass).getBytes(), Base64.NO_WRAP));
            wr = new OutputStreamWriter(conn.getOutputStream());


            wr.write(this.data);


            wr.flush();


            // Get the server response

            BufferedReader reader = null;

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response

            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line);

            }


            this.text = sb.toString();

            conn.disconnect();





        }
        catch (Exception e){

           this.text=e.toString();

        }



        return this.text;
    }





}
