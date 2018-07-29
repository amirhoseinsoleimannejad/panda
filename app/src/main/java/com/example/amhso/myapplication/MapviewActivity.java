package com.example.amhso.myapplication;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class MapviewActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
         LocationListener {


    public LatLng latlng [];

            public WebSocketClient mWebSocketClient;
            private GoogleMap mMap;
            GoogleApiClient mGoogleApiClient;
            Location mLastLocation;
            Marker  mCurrLocationMarker;
            LocationRequest mLocationRequest;
            public View ViewMapView;
        //    LatLng latdriver;
            public boolean check_exist_customer=false;


            public String id_orders;
            public String name;
            public String address;

    public int id_order_cancel;
            public AlertDialog.Builder alertDialog;
            public ProgressDialog progressDialog;
            private int once=0;

            public int id_order;

            public String phone;
            public int conditionConnect=0;
    public double mylocationLat;
    public double mylocationLng;

    public AlertDialog alertDialogComplete;
    public MediaPlayer mPlayer;





//    @Override
//    public void onResume() {
//
//
//        super.onResume();
//
//
//        connectWebSocket();
//
//    }



//    @Override
//    public void onResume() {
//
//
//        super.onResume();
//
//
//
//
//        connectWebSocket();
//
//    }
//
//
//    @Override
//    public void onPause() {
//
//
//        super.onPause();
//
//
//
//
//        connectWebSocket();
//
//
//        String statee=mWebSocketClient.getReadyState().toString();
//        Toast.makeText(getApplicationContext(),statee,Toast.LENGTH_LONG).show();
//
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
//         mPlayer = MediaPlayer.create(MapviewActivity.this, R.raw.air);

        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());






        setContentView(R.layout.activity_mapview);




        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

        String user = sharedpreferences.getString(G.username, "");
        String pass = sharedpreferences.getString(G.password, "");

        String id_user = sharedpreferences.getString(G.id_user, "");

        latlng =new LatLng[100];
//

        new FetchListForVisitor(G.FetchVisitoreListUrl+"?id_user="+id_user, "data", user, pass).execute();



        try{
            Bundle bundle = this.getIntent().getExtras();
            if(bundle!= null)
            {
                name =  this.getIntent().getExtras().getString("name");
                if(name!=null)
                {

                    TextView nameT = (TextView) findViewById(R.id.name);
                    nameT.setVisibility(View.VISIBLE);

                    nameT.setText(name);
                }
            }
        }
        catch (Exception e){

        }




        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y;





        Button Button = (Button) this.findViewById(R.id.send);
        Button.setHeight(height/7);



        Button Button2 = (Button) this.findViewById(R.id.reset);
        Button2.setHeight(height/7);

        Button Button3 = (Button) this.findViewById(R.id.customer);
        Button3.setHeight(height/7);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = 6*(height/7);
        mapFragment.getView().setLayoutParams(params);



        ImageView imageView = (ImageView) findViewById(R.id.pin);
        imageView.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                setMarkerCenter();
            }
        });



        TextView call = (TextView) findViewById(R.id.name);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone)));

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
        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                if(isInternetOn()){

                    try {
                        if(check_exist_customer)
                            RequestDriver();
                        else{
                            Toast.makeText(MapviewActivity.this,"برای امروز هیچ مشتری ندارید" , Toast.LENGTH_LONG).show();

                        }
                    }
                    catch (Exception q){

                    }
                }


                else{
                   Toast.makeText(MapviewActivity.this,"اینترنت را متصل کنید باتشکر" , Toast.LENGTH_LONG).show();

                }
            }
        });




        Button customer = (Button) findViewById(R.id.customer);
        customer.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                if(isInternetOn()){

                    try {
                        Intent myIntent = new Intent(MapviewActivity.this,
                                MapActivity.class);
                        startActivity(myIntent);
                    }
                    catch (Exception q){

                    }
                }


                else{
                    Toast.makeText(MapviewActivity.this,"اینترنت را متصل کنید باتشکر" , Toast.LENGTH_LONG).show();

                }
            }
        });




        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                try{
                    if(once==1){
                        mCurrLocationMarker.remove();
                        once=0;
                    }
                    else if(once==2){

//                    SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
//
//                    String UserId = sharedpreferences.getString(G.id_user, "");
//
//                    mWebSocketClient.send("CancelVisitor+"+UserId+"+"+id_order_cancel);
                        mCurrLocationMarker.remove();
                        once=0;
                    }
                }
                catch (Exception e){

                }


            }
        });



        Button message = (Button) findViewById(R.id.message);

        message.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

//                mWebSocketClient.close();


                Intent myIntent = new Intent(MapviewActivity.this,
                        ChatVisitoreActivity.class);

                myIntent.putExtra("id_order", id_order);
                startActivity(myIntent);

//                if(conditionConnect==1){
//                    mWebSocketClient.close();
//                }


            }
        });



        alertDialogComplete =  new AlertDialog.Builder(MapviewActivity.this).create();
        LayoutInflater factorycomplete = LayoutInflater.from(MapviewActivity.this);
        final View viewcomplete = factorycomplete.inflate(R.layout.activity_complete, null);
        alertDialogComplete.setView(viewcomplete);



        Button buttonComplete = (Button) findViewById(R.id.complete);
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                alertDialogComplete.show();


            }
        });


        Button completeYes = (Button) viewcomplete.findViewById(R.id.yesC);
        completeYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {



                try{
                    EditText message_cancel=(EditText) viewcomplete.findViewById(R.id.message_Complete);

                    String message_cancelS = message_cancel.getText().toString();

//                Toast.makeText(getApplicationContext(),message_cancelS+"id_order: "+order[IndexView].getId_order()+"index: "+IndexView,Toast.LENGTH_LONG).show();

                    alertDialogComplete.dismiss();


                    SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

                    String UserId = sharedpreferences.getString(G.id_user, "");



                    mWebSocketClient.send("CompleteClient+"+UserId+"+"+id_order+"+"+message_cancelS);
                }
                catch (Exception q){

                }





            }
        });



        Button completeNo = (Button) viewcomplete.findViewById(R.id.noC);
        completeNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try{
                    alertDialogComplete.dismiss();

                }
                catch (Exception q){

                }

            }
        });



        try {
            connectWebSocket();
        }
        catch (Exception e){

        }


//        connectWebSocket();
    }






    public class myRunnable implements Runnable {
        public void run() {




            while(true) {
                try {

                    getLocation();

                    SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
                    String UserIdl = sharedpreferences.getString(G.id_user, "");


                    mWebSocketClient.send("LoopLocation+" + UserIdl + "+" + mylocationLat + "+" + mylocationLng);





                    Thread.sleep(15000);



//            Toast.makeText(getApplicationContext(),
//                                "ddsd",
//                                Toast.LENGTH_LONG).show();

                } catch (Exception e) {

                }

            }



        }
    }







    private void connectWebSocket() {
        URI uri;




        try {
            uri = new URI(G.urlsocket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Open" );
                conditionConnect=1;


                Thread myThread = new Thread(new MapviewActivity.myRunnable());
                myThread.start();


            }

            @Override
            public void onMessage(String s) {
                final String message = s;

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {




//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                        mp.start();

                        if(!message.equals("0")) {


                            if (message.equals("CancelDriver")) {
                                Toast.makeText(getApplicationContext(),
                                        "راننده به علت مشکلی درخواست انصراف از درخواست شما داده است , راننده ای در حال حاضر وجود ندارد.",
                                        Toast.LENGTH_LONG).show();



                                Intent myIntent = new Intent(MapviewActivity.this,
                                        MapviewActivity.class);
                                startActivity(myIntent);

                                MapviewActivity.this.finish();
                            }
                            else if(message.equals("CompleteClient")){

                                Toast.makeText(getApplicationContext(),
                                        "به امید رضایت شما",
                                        Toast.LENGTH_LONG).show();


                                Intent myIntent = new Intent(MapviewActivity.this,
                                        MapviewActivity.class);
                                startActivity(myIntent);

                                MapviewActivity.this.finish();
                            }
                            else {

                                try {



                                    JSONObject jsonObj = new JSONObject(message);

                                    // Getting JSON Array node
                                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                                    // looping through All Contacts
                                    for (int i = 0; i < contacts.length(); i++) {


                                        name = "اقای ";
                                        JSONObject c = contacts.getJSONObject(i);
                                        String id = c.getString("id");
                                        name += c.getString("name");
                                        String lat = c.getString("lat");
                                        String lon = c.getString("long");
                                        id_orders=c.getString("id_order");
                                        phone=c.getString("phone");

                                        id_order=Integer.parseInt(id_orders);

                                        name += " به شماره تلفن: ";
                                        name += phone;

                                        name +=" برای شماره گیری روی شماره بزنید. ";


                                        address = "";
                                        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//                                        address += getAddress(latlng);
//
//                                        address += " در حال انجام درخواست شما می باشد. ";

                                        name += address;

                                        setMarkerCenter(latlng);
                                        moveCameraToLatLng(latlng);


                                        Button b = (Button) findViewById(R.id.reset);
                                        b.setVisibility(View.GONE);

                                        Button b2 = (Button) findViewById(R.id.send);
                                        b2.setVisibility(View.GONE);


                                        Button b3 = (Button) findViewById(R.id.customer);
                                        b3.setVisibility(View.GONE);


                                        ImageView I1 = (ImageView) findViewById(R.id.pin);
                                        I1.setVisibility(View.GONE);


                                        TextView nameT = (TextView) findViewById(R.id.name);
                                        nameT.setVisibility(View.VISIBLE);

                                        nameT.setText(name);


                                        Button message = (Button) findViewById(R.id.message);
                                        message.setVisibility(View.VISIBLE);


                                        Button complete = (Button) findViewById(R.id.complete);
                                        complete.setVisibility(View.VISIBLE);

//                                        mWebSocketClient.close();
                                        once=2;


                                    }
                                } catch (final JSONException e) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            id_order_cancel=Integer.parseInt(message);


                                            Toast.makeText(getApplicationContext(),
                                                    "پیامی از طرف راننده ارسال شده است.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }






                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                conditionConnect=0;
                Log.i("Websocket", "Closed " + s);
//                mWebSocketClient.connect();
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };



        try{
            mWebSocketClient.connect();

        }
        catch (Exception e){

        }


    }




    @Override
    public void onBackPressed()
    {

        try{
            mWebSocketClient.close();
            finishAffinity();
            System.exit(0);
        }
        catch (Exception e){

        }

    }




    public void setMarkerCenter(){


        if(once==0) {

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_new);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(MapActivity.LatlngCenter);
            markerOptions.title("Current Position");
            markerOptions.icon(icon);
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            once = 1;
        }
    }






    public void RequestDriver(){


       if(once==1){

           SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

           String UserId = sharedpreferences.getString(G.id_user, "");


           final LatLng mlatlng=mCurrLocationMarker.getPosition();


        try{
            getLocation();
            mWebSocketClient.send("RequestDriver+"+UserId+"+"+mlatlng.latitude+"+"+mlatlng.longitude+"+"+mylocationLat+"+"+mylocationLng);
            once=2;

                progressDialog = ProgressDialog.show(MapviewActivity.this,
                        "لطفاً منتظر بمانید",
                        "در حال جستجو برای نزدیک ترین راننده هستیم.");

                Thread myThread = new Thread(new MapviewActivity.myRunnable2(MapviewActivity.this));
                myThread.start();




        }
        catch (Exception e){

        }



       }


       else if(once==0){
           Toast.makeText(MapviewActivity.this,"مقصد راننده را تعیین کنید با تشکر" , Toast.LENGTH_LONG).show();

       }
       else if(once==2){
           Toast.makeText(MapviewActivity.this,"ابتدا از درخواست خود انصراف داده و بعد در خواست جدید بدهید با تشکر" , Toast.LENGTH_LONG).show();

       }
    }




    public class myRunnable2 implements Runnable {

        public Activity a;

        public myRunnable2(Activity a){
            this.a=a;
        }
        public void run() {



            try {


                Thread.sleep(15000);



                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    Toast.makeText(a,"اینترنت شما ضعیف است در خواست خود را بعدا ارسال کنید" , Toast.LENGTH_LONG).show();
                    once=1;
                }


            } catch (Exception e) {

            }





        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {

                MapActivity.LatlngCenter=arg0.target;


            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void moveCameraToLatLng(LatLng latLng){
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



    public void setMarkerCenter(LatLng latLng){


        String strAdd = "";
        Geocoder geocoder = new Geocoder(MapviewActivity.this,new Locale("fa"));
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

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_new_visit);

        markerOptions.icon(icon);
        mMap.addMarker(markerOptions);

    }




//    public void getLocation() {
//
//        LocationManager locationManager;
//
//
//       // check if we have permission to access device location
//        if (ActivityCompat.checkSelfPermission( MapviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission( MapviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//
//
//
//        locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        Location location = locationManager.getLastKnownLocation(locationManager
//                .getBestProvider(criteria, false));
//        mylocationLat = location.getLatitude();
//        mylocationLng = location.getLongitude();
//
//    }






    public void getLocation() {

        try {
            LocationManager locationManager = (LocationManager) MapviewActivity.this.getSystemService(Context.LOCATION_SERVICE);


            //check if we have permission to access device location
            if (ActivityCompat.checkSelfPermission(MapviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                mylocationLat = myLocation.getLatitude();
                mylocationLng = myLocation.getLongitude();
            }
        }
        catch (Exception s){

        }


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



        MapActivity.LatlngCenter=latLng;
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





    public String getAddress(LatLng latLng){



        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

        String user = sharedpreferences.getString(G.username, "");
        String pass = sharedpreferences.getString(G.password, "");





        String strAdd = "";
        Geocoder geocoder = new Geocoder(MapviewActivity.this,new Locale("fa"));
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





    public void setMarkerCenter2(LatLng latLng,String name,String code){


        String strAdd = "";
        Geocoder geocoder = new Geocoder(MapviewActivity.this,new Locale("fa"));
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
        markerOptions.title(name + "\n"+code);


        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_new);

        markerOptions.icon(icon);
        Marker m=mMap.addMarker(markerOptions);

//            m.showInfoWindow();





    }







    public class FetchListForVisitor extends AsyncTask<String, String, String> {
        public String responce;
        public String  data;
        public String urlstring;
        public String user;
        public String pass;
        public Context context;
        public ProgressDialog progressDialog;






        public FetchListForVisitor(String urlstring,String data,String user,String pass){


            this.data=data;
            this.urlstring=urlstring;
            this.user=user;
            this.pass=pass;




        }


        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(MapviewActivity.this,
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


                this.responce = sb.toString();

                conn.disconnect();





            }
            catch (Exception e){

                this.responce=e.toString();

            }



            return this.responce;
        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            this.responce=result;
            progressDialog.dismiss();




            if(!this.responce.equals("0")) {

                try {
                    JSONObject jsonObj = new JSONObject(this.responce);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < 1; i++) {

                        check_exist_customer=true;


//                        JSONObject c = contacts.getJSONObject(i);
//                        String id = c.getString("id");
//                        String name = c.getString("name");
//                        String lat = c.getString("lat");
//                        String lon = c.getString("long");
//                        String phone = c.getString("phone");
//
//                        String code = c.getString("code");
//
//
//
//
//
//                        latlng[i] = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//
//
//                        for(int j=0;latlng[j]!=null;j++){
//                            setMarkerCenter2(latlng[j],name,code);
//
//                            moveCameraToLatLng(latlng[j]);
//
//                        }



                    }
                } catch (final JSONException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"برای امروز هیچ مشتری برای ویزیت ندارید",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }





//                Toast.makeText(MapActivity.this,latlng[1], Toast.LENGTH_LONG).show();
            }



        }


    }




}