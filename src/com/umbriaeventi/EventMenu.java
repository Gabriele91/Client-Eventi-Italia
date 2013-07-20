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
    private String regionSelected=null;
    
    //gps

    
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
        if(regionSelected==null){
            //get location from gps/internet
            regionSelected=EventLocation.getInstance().getRegion();
            //if not get
            if(regionSelected==null)
                regionSelected=EventUrls.getRegions()[0]; //return first region
            else{
                //if gps location isn't valid...
                for(String vars:EventUrls.getRegions())
                    if(vars.equals(regionSelected))
                        return regionSelected;
                //return first region
                regionSelected=EventUrls.getRegions()[0];
            }
        }
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
