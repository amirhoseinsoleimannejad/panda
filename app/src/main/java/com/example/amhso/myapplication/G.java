package com.example.amhso.myapplication;

/**
 * Created by amhso on 17/09/2017.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;


public class G extends Application {

    public static Context context;
    public static Activity activity;

    public static String urlserver="http://grs-panda.com/backend/web/android/";
    public static String urlsocket="ws://199.127.101.137:5059";
    public static String urlsocket2="ws://199.127.101.137:9096";



//    public static String urlserver="http://192.168.1.9/backend/web/android/";
//    public static String urlsocket="ws://192.168.1.9:5055";
//    public static String urlsocket2="ws://192.168.1.9:9095";


    //    public static String urlsocketLoopDriver="ws://192.168.1.3:9000";
    public SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String login = "login";
    public static final String username = "username";
    public static final String password = "password";
    public static final String id_user= "id_user";
    public static final String FetchVisitoreListUrl= G.urlserver+"fetchlistvisitore";
    public static final String CheckLoginUrl= G.urlserver+"auth";
    public static final String RequestDriver= G.urlserver+"find_near_driver";
    public static final String RequestVisitoreUrl= G.urlserver+"requestvisitore";
    public static final String urlFetch= G.urlserver+"fetch";
    public static final String urlconnect= G.urlserver+"socketconnect";
    public static final String urldisconnect= G.urlserver+"socketdisconnect";
    public static final String setlocation= G.urlserver+"setlocation";
    public static final String fetchpoint= G.urlserver+"fetchpoint";








    @Override
    public void onCreate() {

        context = getApplicationContext();
        super.onCreate();




    }



    public void setSharelogin(){

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean(login,true);


    }

    public void oi(){
        setSharelogin();
    }



//
//    public final boolean isInternetOn() {
//
//        // get Connectivity Manager object to check connection
//        ConnectivityManager connec =
//                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
//
//        // Check for network connections
//        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
//                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
//                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
//                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
//
//            // if connected with internet
//
////            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
//            return true;
//
//        } else if (
//                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
//                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
//
//
//            return false;
//        }
//        return false;
//    }



}