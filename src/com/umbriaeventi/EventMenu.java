package com.umbriaeventi;

/**
 * Created by Gabriele on 18/07/13.
 */

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class EventMenu {

    //var declaration
    private String regionSelected="Umbria";//to do read from file
    
    //gps
    /*
    private boolean isGettedRegion=false;
    private String region=null;
    private void getCurrentLocation() {
        
        lManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        lListener = new LocationListener() {
        	
        	
             
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
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;                
				try {
					
					addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	                if (addresses.size()>0) {
	                	region=addresses.get(0).getAdminArea();	    
	                	//delete update
	                	lManager.removeUpdates(lListener);
	                }
	                
				} catch (IOException e) {
					
					Log.e("Gps error;", e.toString());
					
				} finally{
	                //is getted
	                isGettedRegion=true;					
				}
				
            }
        };
        //low quality gps
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_LOW);        
        String locationProvider = lManager.getBestProvider(criteria, true);
        //set update
        lManager.requestLocationUpdates(locationProvider, 0, 1, lListener);
        
    }
    private void getRegion(){
    	if(region==null){
	    	//get region
	    	getCurrentLocation();
    	}
    }
    */
    
    //singleton
    private static EventMenu eventMenu=new EventMenu();
    private EventMenu(){}
    public static EventMenu getInstance(){
        return eventMenu;
    }
    //init event
    boolean initGraphicsMenu(Menu menu){
        //get regions list
        String [] regions=EventUrls.getRegions();
        //can't create a regions menu
        if(regions==null || regions.length<=0) return false;
        //default is selected auto
        MenuItem toenable=menu.add(0,0,0,regions[0]);
        //add all regions
        for (int i=1;i<regions.length;++i){//java 7
            if(regions[i].equals(regionSelect()))
                toenable=menu.add(0,0,0,regions[i]);
            else
                menu.add(0,0,0,regions[i]);
        }
        //checkable menu
        menu.setGroupCheckable(0, true, true);
        //enable object selected
        toenable.setChecked(true);
        //dune!
        return true;
    }
    String regionSelect(){
        return regionSelected;
    }
    //event
    public boolean optionsSelected(MenuItem item) {
        //todo
        item.setChecked(true);
        regionSelected=item.getTitle().toString();
        // Handle item selection
        return true;
    }
}
