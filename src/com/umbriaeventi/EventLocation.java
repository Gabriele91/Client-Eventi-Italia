package com.umbriaeventi;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gabriele on 20/07/13.
 */
public class EventLocation {

    //vars declaretion
    private String region=null;
    private boolean onReadRegion=false;
    private LocationManager lManager;
    private LocationListener lListener;
    private String locationProvider;

    //class declaretion
    private class EventLocationListener implements LocationListener {

        private Context context;
        private LocationManager lManager;

        EventLocationListener(Context context,LocationManager lManager){
            this.context=context;
            this.lManager=lManager;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onLocationChanged(Location loc) {
            //GET COORDINATES
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {

                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                //if andress is getted
                if (addresses.size()>0) {
                    //save value
                    region=addresses.get(0).getAdminArea();
                    //delete update
                    lManager.removeUpdates(this);
                }

            } catch (IOException e) {

                Log.e("Gps error;", e.toString());

            } finally{
                //disable wait
                onReadRegion=false;
            }

        }

    }


    //singleton
    private static EventLocation eventLocation=new EventLocation();
    private EventLocation(){}
    public static EventLocation getInstance(){
        return eventLocation;
    }

    //public methos
    public void callOnCreateMainThread(Context appContext,Context baseContex){
        //crate objects
        lManager=(LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
        lListener=new EventLocationListener(baseContex,lManager);
        //start wait
        onReadRegion=true;
        //low quality gps
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        locationProvider = lManager.getBestProvider(criteria, true);
        //set update
        lManager.requestLocationUpdates(locationProvider, 0, 1, lListener);
    }

    public String getRegion(){
    	if(region==null){
            // force get location
            Location location = lManager.getLastKnownLocation(locationProvider);
            if(location!=null) lListener.onLocationChanged(location);
    	}
        return region;
    }

}
