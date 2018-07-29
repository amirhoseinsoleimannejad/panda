package com.example.amhso.myapplication;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

//import tech.gusavila92.websocketclient.WebSocketClient;


public class DriverActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static LatLng MycurrentLocation;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mLocation;
    Marker mCurrLocationMarker;
    Marker mCurrLocationMarker2;
    public static String connect = "";

    LocationRequest mLocationRequest;
    public WebSocketClient mWebSocketClient;
    public WebSocketClient mWebSocketClient2;
    private int once = -1;
    double currentLatitude;
    double currentLongitude;
    public AlertDialog alertDialog;
    public int conditionConnect = 0;
    public int conditionConnection2 = 0;
    public int id_order[] = new int[20];
    Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    LocationManager locationManager;
    public String text = "";
    public Order order[] = new Order[100];
    public int index = -1;
    public int IndexView = -1;
    public int height;
    public SupportMapFragment mapFragment;
    public static LatLng LatlngCenter;

    public AlertDialog alertDialogCancel;
    public AlertDialog alertDialogComplete;
    public int answer=0;
    public MediaPlayer mPlayer;








    @Override
    public void onBackPressed() {

        finishAffinity();
        System.exit(0);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_driver);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapd);
        mapFragment.getMapAsync(this);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        height = size.y;



        alertDialogCancel =  new AlertDialog.Builder(DriverActivity.this).create();
        LayoutInflater factory = LayoutInflater.from(DriverActivity.this);
        final View viewcancel = factory.inflate(R.layout.activity_cancel, null);
        alertDialogCancel.setView(viewcancel);



        alertDialogComplete =  new AlertDialog.Builder(DriverActivity.this).create();
        LayoutInflater factorycomplete = LayoutInflater.from(DriverActivity.this);
        final View viewcomplete = factorycomplete.inflate(R.layout.activity_complete, null);
        alertDialogComplete.setView(viewcomplete);






        Button OrderButton = (Button) findViewById(R.id.message);
        OrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                if(order[IndexView].getChangestate()) {


                    Intent myIntent = new Intent(DriverActivity.this,
                            ChatDriverActivity.class);

                    myIntent.putExtra("id_order", order[IndexView].getId_order());

                    startActivity(myIntent);
                }

            }
        });



        Button buttonComplete = (Button) findViewById(R.id.complete);
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                if(order[IndexView].getChangestate()) {

                    alertDialogComplete.show();
                }


            }
        });


        Button completeYes = (Button) viewcomplete.findViewById(R.id.yesC);
        completeYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                if(order[IndexView].getChangestate()) {

                    try {
                        EditText message_cancel = (EditText) viewcomplete.findViewById(R.id.message_Complete);

                        String message_cancelS = message_cancel.getText().toString();

//                Toast.makeText(getApplicationContext(),message_cancelS+"id_order: "+order[IndexView].getId_order()+"index: "+IndexView,Toast.LENGTH_LONG).show();

                        alertDialogComplete.dismiss();


                        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

                        String UserId = sharedpreferences.getString(G.id_user, "");


                        mWebSocketClient2.send("CompleteDriver+" + UserId + "+" + order[IndexView].getId_order() + "+" + IndexView + "+" + message_cancelS);
                    } catch (Exception s) {

                    }
                }



            }
        });



        Button completeNo = (Button) viewcomplete.findViewById(R.id.noC);
        completeNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    alertDialogComplete.dismiss();
                }
                catch (Exception s){

                }
            }
        });






        Button prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                if (IndexView > 0) {
                    IndexView--;
                    moveCameraToLatLng(order[IndexView].getMarker().getPosition());

                    TextView des = (TextView) findViewById(R.id.des);

                    String desc = "نام: ";
                    desc += order[IndexView].getName();

                    desc += "به شماره تلفن: ";
                    desc += order[IndexView].getPhone();

                    des.setText(desc);

//                    Toast.makeText(DriverActivity.this, "IndexView:" + IndexView +"-index: "+index, Toast.LENGTH_LONG).show();

                }

            }
        });


        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                if (IndexView < index) {
                    IndexView++;
                    moveCameraToLatLng(order[IndexView].getMarker().getPosition());

                    TextView des = (TextView) findViewById(R.id.des);

                    String desc = "نام: ";
                    desc += order[IndexView].getName();

                    desc += "به شماره تلفن: ";
                    desc += order[IndexView].getPhone();

                    des.setText(desc);
//                    Toast.makeText(DriverActivity.this, "IndexView:" + IndexView +"-index: "+index, Toast.LENGTH_LONG).show();

                }

            }
        });






        Button cancel = (Button) DriverActivity.this.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                if(order[IndexView].getChangestate()) {

                    alertDialogCancel.show();
                }

            }
        });





        Button cancelYes = (Button) viewcancel.findViewById(R.id.yes);
        cancelYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                if(order[IndexView].getChangestate()) {


                    try {
                        EditText message_cancel = (EditText) viewcancel.findViewById(R.id.message_cancel);

                        String message_cancelS = message_cancel.getText().toString();

//                Toast.makeText(getApplicationContext(),message_cancelS+"id_order: "+order[IndexView].getId_order()+"index: "+IndexView,Toast.LENGTH_LONG).show();

                        alertDialogCancel.dismiss();


                        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

                        String UserId = sharedpreferences.getString(G.id_user, "");


                        mWebSocketClient2.send("CancelOrder+" + UserId + "+" + order[IndexView].getId_order() + "+" + IndexView + "+" + message_cancelS);

                    } catch (Exception s) {

                    }
                }

            }
        });



        Button cancelNo = (Button) viewcancel.findViewById(R.id.no);
        cancelNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                try {
                    alertDialogCancel.dismiss();
                }
                catch (Exception s){

                }

            }
        });




// after click tell cal to visitore

        TextView call = (TextView) findViewById(R.id.des);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                Log.i("test", "onClick: "+"test github");


                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(order[IndexView].getPhone())));

//                if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
                startActivity(intent);




            }
        });




        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

        String UserId = sharedpreferences.getString(G.id_user, "");


        try {
            new fetchpoint(G.fetchpoint + "?id_user=" + UserId, "", "sss", "dddd", this).execute();
        }
        catch (Exception e){

        }

        try {
            connectWebSocket2(UserId);
        }
        catch (Exception s){

        }

    }






    public class myRunnable2 implements Runnable {
        public void run() {




                try {


                    Thread.sleep(20000);


                    SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
                    String UserId = sharedpreferences.getString(G.id_user, "");



                    if( alertDialog.isShowing()){
                        alertDialog.dismiss();
                        mWebSocketClient2.send("CancelOrder+" + UserId + "+" + order[IndexView].getId_order() + "+"+IndexView+"+"+ "دیر کرد در جواب در خواست ویزیتور");

                    }





                } catch (Exception e) {

                }





        }
    }





    public class myRunnable implements Runnable {
        public void run() {




            while(true) {
                try {

                    getLocation();

                    SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
                    String UserIdl = sharedpreferences.getString(G.id_user, "");


                    mWebSocketClient2.send("LoopLocation+" + UserIdl + "+" + currentLatitude + "+" + currentLongitude);





                    Thread.sleep(15000);



//            Toast.makeText(getApplicationContext(),
//                                "ddsd",
//                                Toast.LENGTH_LONG).show();

                } catch (Exception e) {

                }

            }



        }
    }






    private void connectWebSocket2(String id_user) {
        URI uri;

        final String idUser=id_user;



        try {
            uri = new URI(G.urlsocket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient2 = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                try {
                    Log.i("Websocket", "Open");
                    getLocation();
                    conditionConnection2 = 1;
                    mWebSocketClient2.send("FetchOrder+" + idUser + "+" + currentLatitude + "+" + currentLongitude);

                    Thread myThread = new Thread(new myRunnable());
                    myThread.start();
                }
                catch (Exception s){

                }
            }

            @Override
            public void onMessage(String s) {
                final String message = s;







                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();


                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                        mp.start();

                        String mmm[]=message.split("#");

                        if(mmm[0].equals("CancelDriver")){


                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cancle_marker);

                            int indexview=Integer.parseInt(mmm[1]);


                            if(order[indexview].getChangestate()){
                                order[indexview].getMarker().setIcon(icon);
                                order[indexview].noChangeState();

                            }


                            Toast.makeText(getApplicationContext(),"در خواست انصراف به درستی انجام شد.",Toast.LENGTH_LONG).show();
                        }



                        if(mmm[0].equals("CompleteDriver")){




                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.complete_marker);


                            int indexview=Integer.parseInt(mmm[1]);

                            if(order[indexview].getChangestate()){
                                order[indexview].getMarker().setIcon(icon);
                                order[indexview].noChangeState();

                            }




                            Toast.makeText(getApplicationContext(),"تشکر از زحمات شما",Toast.LENGTH_LONG).show();
                        }


                        else if(!message.equals("0")) {

                            try {
                                JSONObject jsonObj = new JSONObject(message);

                                // Getting JSON Array node
                                JSONArray contacts = jsonObj.getJSONArray("contacts");

                                // looping through All Contacts
                                for (int i = 0; i < contacts.length(); i++) {


                                    JSONObject c = contacts.getJSONObject(i);
                                    String id = c.getString("id");
                                    final String name=c.getString("name");
                                    final int id_order= Integer.parseInt(id);

                                    final String lat = c.getString("lat");
                                    final String lon = c.getString("long");

                                    final String phone= c.getString("phone");

                                    text = "از آدرس ";
                                    final LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                                    text += getAddress(latlng);

                                    text += " برای شما در خواست حمل بار دارند آیا شما تایید می کنید؟";



                                    alertDialog =  new AlertDialog.Builder(DriverActivity.this).create();
                                    LayoutInflater factory = LayoutInflater.from(DriverActivity.this);
                                    final View view = factory.inflate(R.layout.message_driver_visitore, null);
                                    alertDialog.setView(view);

                                    TextView nameView = (TextView) view.findViewById(R.id.name_driver);
                                    nameView.setText(text);

                                    index++;
                                    IndexView=index;
                                    final Marker m=setMarkerCenter(latlng);
                                    order[index]=new Order(id_order,m,text,phone,name);
                                    moveCameraToLatLng(latlng);


                                    Button verify = (Button) view.findViewById(R.id.verify);
                                    verify.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View arg0) {

                                            alertDialog.dismiss();

                                            mWebSocketClient2.send("ValidOrder+"+idUser+"+"+order[index].getId_order()+"+"+currentLongitude);


                                            if(true){


                                                ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                                                params.height = 6*(height/7);
                                                mapFragment.getView().setLayoutParams(params);

                                                LinearLayout l= (LinearLayout) findViewById(R.id.Driver_controller_layout);

                                                l.setVisibility(View.VISIBLE);

                                                Button buttn1 = (Button) findViewById(R.id.next);
                                                buttn1.setVisibility(View.VISIBLE);
                                                buttn1.setHeight(height/7);

                                                buttn1 = (Button) findViewById(R.id.message);
                                                buttn1.setVisibility(View.VISIBLE);
                                                buttn1.setHeight(height/7);
                                                buttn1 = (Button) findViewById(R.id.cancel);
                                                buttn1.setVisibility(View.VISIBLE);
                                                buttn1.setHeight(height/7);
                                                buttn1 = (Button) findViewById(R.id.prev);
                                                buttn1.setVisibility(View.VISIBLE);
                                                buttn1.setHeight(height/7);
                                                buttn1 = (Button) findViewById(R.id.complete);
                                                buttn1.setVisibility(View.VISIBLE);
                                                buttn1.setHeight(height/7);

                                            }

                                            TextView des = (TextView) findViewById(R.id.des);
                                            des.setVisibility(View.VISIBLE);

                                            String desc="نام: ";
                                                desc += order[IndexView].getName();

                                                desc += " به شماره تلفن: ";
                                                desc +=order[IndexView].getPhone();

                                                desc +=" برای شماره گیری روی شماره بزنید. ";

                                            des.setText(desc);


                                        }
                                    });



                                    Button DontVerify = (Button) view.findViewById(R.id.dont_verify);
                                    DontVerify.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View arg0) {

                                            alertDialog.dismiss();

//                                            mWebSocketClient2.send("DontValidOrder+"+idUser+"+"+order[IndexView].getId_order()+"راننده به علت مشکلاتی به درخواست جواب رد داده اند");



                                            SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

                                            String UserId = sharedpreferences.getString(G.id_user, "");

                                            EditText t=(EditText) view.findViewById(R.id.message_Complete);

                                            String message=t.getText().toString();


                                            mWebSocketClient2.send("CancelOrder+" + UserId + "+" + order[IndexView].getId_order() + "+"+IndexView+"+" + message);





                                        }
                                    });



                                    alertDialog.show();



                                    Thread myThread = new Thread(new myRunnable2());
                                    myThread.start();








                                }
                            } catch (final JSONException e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Toast.makeText(getApplicationContext(),"از طرف ویزیتور پیامی به شما ارسال شده است     .",
//                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }
                        else{
//                            Toast.makeText(getApplicationContext(),"از طرف ویزیتور پیامی به شما ارسال شده است.",
//                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                conditionConnection2=0;
                Log.i("Websocket", "Closed " + s);
//                mWebSocketClient2.connect();

            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };


        try {
            mWebSocketClient2.connect();

        }catch (Exception s){

        }



    }

    public void moveCameraToLatLng(LatLng latLng){


        for (int i=0;order[i]!=null;i++){

            order[i].getMarker().hideInfoWindow();
        }
//
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_zoom);

        order[IndexView].getMarker().setTitle(order[IndexView].getName());

        order[IndexView].getMarker().showInfoWindow();

        MapActivity.LatlngCenter=latLng;
        CameraPosition position = CameraPosition.builder()
                .target( latLng )
                .zoom( 18f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );
    }





    public Marker setMarkerCenter(LatLng latLng){


        String strAdd = "";
        Geocoder geocoder = new Geocoder(DriverActivity.this,new Locale("fa"));
        try {
            List<Address> addresses = geocoder.getFromLocation(mCurrLocationMarker.getPosition().latitude,mCurrLocationMarker.getPosition().longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("-");
                }
                strAdd = strReturnedAddress.toString();


            } else {
                strAdd="";
            }
        } catch (Exception e) {
            e.printStackTrace();

        }



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(strAdd);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_driver);

        markerOptions.icon(icon);

        Marker m=mMap.addMarker(markerOptions);





        return m;


    }

    public String getAddress(LatLng latLng){



        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

        String user = sharedpreferences.getString(G.username, "");
        String pass = sharedpreferences.getString(G.password, "");





        String strAdd = "";
        Geocoder geocoder = new Geocoder(DriverActivity.this,new Locale("fa"));
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("-");
                }
                strAdd = strReturnedAddress.toString();


            } else {
                strAdd="";
            }
        } catch (Exception e) {
            e.printStackTrace();

        }



        return strAdd;



    }







    public void setorderlocation(LatLng latlong){

        CameraPosition position = CameraPosition.builder()
                .target( latlong )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlong));
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );


        LatLng latLng = new LatLng(latlong.latitude, latlong.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("مبدا");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)

                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }





    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        else{

        }

//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition arg0) {
//
//                MapActivity.LatlngCenter=arg0.target;
//
//
//            }
//        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {


//        Toast.makeText(this, "" + location.toString(), Toast.LENGTH_LONG).show();

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

        DriverActivity.MycurrentLocation=latLng;
        DriverActivity.LatlngCenter=latLng;
        CameraPosition position = CameraPosition.builder()
                .target( latLng )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );







        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }






    public void getLocation() {

        try {
            LocationManager locationManager = (LocationManager) DriverActivity.this.getSystemService(Context.LOCATION_SERVICE);


            //check if we have permission to access device location
            if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            //get last known location to start with
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (myLocation != null) {
                currentLatitude = myLocation.getLatitude();
                currentLongitude = myLocation.getLongitude();
            }
        }
        catch (Exception s){

        }


    }





    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
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


















    public class fetchpoint extends AsyncTask<String, String, String>{
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



        public fetchpoint(String urlstring,String data,String user,String pass,Activity activity){


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

            progressDialog.dismiss();

            try {
                JSONObject jsonObj = new JSONObject(this.text);

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("contacts");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {


                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString("id");
                    final String name=c.getString("name");
                    final int id_order= Integer.parseInt(id);

                    final String lat = c.getString("lat");
                    final String lon = c.getString("long");

                    final String phone= c.getString("phone");


                    final LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));




                    index++;
                    IndexView=index;
                    final Marker m=setMarkerCenter(latlng);
                    order[index]=new Order(id_order,m,text,phone,name);

                    moveCameraToLatLng(latlng);




                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = 6*(height/7);
                    mapFragment.getView().setLayoutParams(params);

                    LinearLayout l= (LinearLayout) findViewById(R.id.Driver_controller_layout);

                    l.setVisibility(View.VISIBLE);

                    Button buttn1 = (Button) findViewById(R.id.next);
                    buttn1.setVisibility(View.VISIBLE);
                    buttn1.setHeight(height/7);

                    buttn1 = (Button) findViewById(R.id.message);
                    buttn1.setVisibility(View.VISIBLE);
                    buttn1.setHeight(height/7);
                    buttn1 = (Button) findViewById(R.id.cancel);
                    buttn1.setVisibility(View.VISIBLE);
                    buttn1.setHeight(height/7);
                    buttn1 = (Button) findViewById(R.id.prev);
                    buttn1.setVisibility(View.VISIBLE);
                    buttn1.setHeight(height/7);
                    buttn1 = (Button) findViewById(R.id.complete);
                    buttn1.setVisibility(View.VISIBLE);
                    buttn1.setHeight(height/7);


                    TextView des = (TextView) findViewById(R.id.des);
                    des.setVisibility(View.VISIBLE);

                    String desc="نام: ";
                    desc += order[IndexView].getName();

                    desc += " به شماره تلفن: ";
                    desc +=order[IndexView].getPhone();

                    desc +=" برای شماره گیری روی شماره بزنید. ";

                    des.setText(desc);



                }
            } catch (final JSONException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                        Toast.makeText(getApplicationContext(),"از طرف ویزیتور پیامی به شما ارسال شده است     .",
//                                                Toast.LENGTH_LONG).show();
                    }
                });

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




            try {

                URL url = new URL(this.urlstring);



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




}
