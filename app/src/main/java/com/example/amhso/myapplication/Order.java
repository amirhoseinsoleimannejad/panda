package com.example.amhso.myapplication;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by amhso on 16/10/2017.
 */

public class Order {

    private int id_order;
    private Marker m;
    private String phone;
    private String  address;
    private String name;
    public Boolean changestate;



    public Order(int id_order,Marker m ,String address,String phone,String name){

        this.id_order=id_order;
        this.m=m;
        this.address=address;
        this.phone=phone;
        this.name=name;
        changestate=true;
    }

    public int getId_order(){
        return id_order;
    }


    public Marker getMarker(){
        return this.m;
    }




    public Boolean getChangestate(){
        return changestate;
    }

    public void noChangeState(){
        this.changestate=false;
    }

    public String getPhone(){
        return phone;
    }


    public String getAddress(){
        return address;
    }


    public String getName(){
        return this.name;
    }


}
