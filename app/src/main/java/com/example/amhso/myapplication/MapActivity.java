package com.example.amhso.myapplication;



import android.Manifest;
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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,

        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker  mCurrLocationMarker;
    LocationRequest mLocationRequest;


    public LatLng latlng [];

    public int index=0;
    private int once=0;
    public JSONArray contacts;



    public static LatLng LatlngCenter;



    @Override
    protected void onStop()
    {
        super.onStop();


//        finishAffinity();
//
//        System.exit(0);


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

        setContentView(R.layout.activity_map);



        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y;





        Button Button = (Button) this.findViewById(R.id.maporder);
        Button.setHeight(height/7);





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = 6*(height/7);
        mapFragment.getView().setLayoutParams(params);




        mapFragment.getMapAsync(this);



        latlng =new LatLng[100];


        if(isInternetOn()) {


            SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

            String user = sharedpreferences.getString(G.username, "");
            String pass = sharedpreferences.getString(G.password, "");

            String id_user = sharedpreferences.getString(G.id_user, "");
//

            new FetchListForVisitor(G.FetchVisitoreListUrl+"?id_user="+id_user, "data", user, pass).execute();

        }
        else{
            finish();
        }

//        Button button = findViewById(R.id.shift);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//
//                else{
//                   Toast.makeText(MapActivity.this, "دیگر نقاطی تعیین نشده است", Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });





        Button button2 = findViewById(R.id.maporder);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isInternetOn()) {

                    if(latlng[0]!=null){
                        Intent myIntent = new Intent(MapActivity.this,
                                MapviewActivity.class);
                        startActivity(myIntent);
                    }
                    else{
                        Toast.makeText(MapActivity.this,"برای امروز هیچ مشتری برای ویزیت ندارید" , Toast.LENGTH_LONG).show();
                    }


                }
                else{
                    Toast.makeText(MapActivity.this,"اینترنت را وصل کنید باتشکر" , Toast.LENGTH_LONG).show();

                }
            }
        });






    }







    public void moveCameraToLatLng(LatLng latLng){
        MapActivity.LatlngCenter=latLng;
        CameraPosition position = CameraPosition.builder()
                .target( latLng )
                .zoom( 15f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );
    }





//
//    @Override
//    public void onBackPressed()
//    {
//
//        finishAffinity();
//        System.exit(0);
//
//    }




    public void setMarkerCenter(LatLng latLng,String name,String code){


            String strAdd = "";
           Geocoder geocoder = new Geocoder(MapActivity.this,new Locale("fa"));
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


//    public void sendLocation(){
//
//
//       if(once==1){
//           SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);
//
//           String user = sharedpreferences.getString(G.username, "");
//           String pass = sharedpreferences.getString(G.password, "");
//
//
//
//
//
//           String strAdd = "";
//           Geocoder geocoder = new Geocoder(MapActivity.this,new Locale("fa"));
//           try {
//               List<Address> addresses = geocoder.getFromLocation(mCurrLocationMarker.getPosition().latitude,mCurrLocationMarker.getPosition().longitude, 1);
//               if (addresses != null) {
//                   Address returnedAddress = addresses.get(0);
//                   StringBuilder strReturnedAddress = new StringBuilder("");
//
//                   for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                       strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("-");
//                   }
//                   strAdd = strReturnedAddress.toString();
//
//
//               } else {
//                   strAdd="";
//               }
//           } catch (Exception e) {
//               e.printStackTrace();
//
//           }
//
//
//
////           new sendhttp(G.UrlLocation + "?lat=" + mCurrLocationMarker.getPosition().latitude + "&log=" + mCurrLocationMarker.getPosition().longitude , "data", user, pass,MapActivity.this).execute();
//
//       }
//
//       else{
//           Toast.makeText(MapActivity.this,"مقصد راننده را تعیین کنید با تشکر" , Toast.LENGTH_LONG).show();
//
//       }
//    }




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

//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
                .zoom( 15f )
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




    public void next_customer(int i,int j){

        try {

            for (; i < j && i < contacts.length(); i++) {


                JSONObject c = contacts.getJSONObject(i);
                String id = c.getString("id");
                String name = c.getString("name");
                String lat = c.getString("lat");
                String lon = c.getString("long");
                String phone = c.getString("phone");

                String code = c.getString("code");


                latlng[i] = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                    setMarkerCenter(latlng[i], name, code);
                    moveCameraToLatLng(latlng[j]);



            }

        }
        catch (Exception e){

        }
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

            progressDialog = ProgressDialog.show(MapActivity.this,
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
                    contacts = jsonObj.getJSONArray("contacts");


                    for (int i = 0; i < contacts.length(); i++) {



                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                       String name = c.getString("name");
                        String lat = c.getString("lat");
                        String lon = c.getString("long");
                        String phone = c.getString("phone");

                        String code = c.getString("code");





                        latlng[i] = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));



                            setMarkerCenter(latlng[i],name,code);

                            moveCameraToLatLng(latlng[i]);





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