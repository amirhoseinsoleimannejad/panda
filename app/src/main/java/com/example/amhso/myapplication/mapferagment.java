package com.example.amhso.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by jornal on 9/8/2017.
 */

public class mapferagment extends Fragment  implements OnMapReadyCallback {
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    MapView mMapView;
    public mapferagment(){

    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map);

        if(mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

        View v = inflater.inflate(R.layout.xml_mapfragment, null, false);
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(G.context);
        map = googleMap;
        map.setMapType(map.MAP_TYPE_NORMAL);
        map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("set shod").snippet("snip"));
        CameraPosition lib=CameraPosition.builder().target(new LatLng(40.689247,-74.044502)).zoom(16).bearing(8).tilt(45).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(lib));
    }
}
