package com.umbriaeventi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class EventUrls {
    //vars declaration
    private static String[] regions=null;
    static private HashMap<String /* region */, String[] > hashMapCities=new HashMap< String, String[] >();

    static private HashMap< String /* region */,
                            HashMap<String /* city */  ,
                                    ArrayList<EventFeed> > >
                            hashMapFeed=new HashMap< String /* region */,
                                                      HashMap<String /* city */  ,
                                                      ArrayList<EventFeed> > >() ;

	public static final String serverLink="http://eventitalia.herokuapp.com/";

    //private methos
    static private String canonicalName(String name){
        if(name.length()>0){
            String lowerCase=name.toLowerCase();
            String firstCharName=lowerCase.substring(0,1).toUpperCase();
            String lowerName=lowerCase.substring(1,lowerCase.length());
            return (firstCharName+lowerName);
        }
        return "";
    }
    static private String getUrlEvents(String region,String area){
        //region= canonicalName(region);
        //area= canonicalName(area);
        try {
            region= URLEncoder.encode(region,"UTF-8").replace("+", "%20");
            area= URLEncoder.encode(area,"UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return serverLink+region+"/"+area;
    }
    static private String getUrlRegions(){
        return serverLink+"/list";
    }
    static private String getUrlCity(String region){
        //region= canonicalName(region);
        try {
            region= URLEncoder.encode(region,"UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return serverLink+region+"/list";
    }
    static private String getWebPageString(String strurl){
    	/////////////////////////////////////////
    	String outWebString=null;    	
    	/////////////////////////////////////////
    	try{
    	    URL url = new URL(strurl);
    	    // Read all the text returned by the server
    	    BufferedReader in = new BufferedReader(
    	    					new InputStreamReader(url.openStream())
    	    					);
    	    outWebString="";
    	    String str;
    	    while ((str = in.readLine()) != null) 
    	    { 
    	    	outWebString+=str;
    	    }
    	    in.close();
    	} 
    	catch (Exception e) {
    		outWebString=null; 
    		Log.e("ERROR:", e.toString());
    	}
    	/////////////////////////////////////////
    	return outWebString;
    }

	//public methos
    static public String[] getRegions(){
        if(regions==null){
            String pageString=getWebPageString(getUrlRegions());
            if(pageString==null) return null;
            try {
                JSONObject jsonObj = new JSONObject(pageString);
                JSONArray array=jsonObj.getJSONArray("regions");
                regions=new String[array.length()];
                for(int i=0;i<array.length();++i)
                    regions[i]=array.getString(i);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error parse json",e.toString());
            }
        }
        return regions;
    }
    static public boolean getNotCitiesExist(String region){
        return (!hashMapCities.containsKey(region)||hashMapCities.get(region)==null);
    }
	static public String[] getCities(String region){
		if( getNotCitiesExist(region)){
	    	String pageString=getWebPageString(getUrlCity(region));
			if(pageString==null) return null;  
			try {
				JSONObject jsonObj = new JSONObject(pageString);
		        JSONArray array=jsonObj.getJSONArray("cities");
                //new array
                String [] cities=new String[array.length()];
                hashMapCities.put(region,cities);
                //add all cities
		        for(int i=0;i<array.length();++i)
                    cities[i]=array.getString(i);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Error parse json",e.toString());
			}	
		}
    	return hashMapCities.get(region);
	}

    static public boolean getNotRegionEventsExist(String region){
        return (!hashMapFeed.containsKey(region)||hashMapFeed.get(region)==null);
    }
	static public boolean getNotCityEventsExist(String region,String city){
        if(!getNotRegionEventsExist(region))
            return (!hashMapFeed.get(region).containsKey(city)||hashMapFeed.get(region).get(city)==null);
        return true;
	}

	static public ArrayList<EventFeed> getEvents(String region,String city){
        //if not exist region event list
		if(getNotRegionEventsExist(region)) //crate a region event list
            hashMapFeed.put(region,new HashMap<String  , ArrayList<EventFeed> >());

        //if not exist city event list...
		if(getNotCityEventsExist(region,city)){
			//create a region event list
			ArrayList<EventFeed> feeds=new ArrayList<EventFeed>();
			String strJson=getWebPageString(getUrlEvents(region,city));

			if(strJson==null) return feeds;
			
	        JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(strJson);
		        JSONArray array;
		        array = jsonObj.getJSONArray("feeds");
				for(int i=0;i<array.length();++i){
					
					JSONObject jsonEvent=array.getJSONObject(i);
					
					EventFeed eventObj=new EventFeed();
					if(jsonEvent.has("link"))
						eventObj.linkPage=jsonEvent.getString("link");
					else
						eventObj.linkPage="";
					eventObj.linkImage=jsonEvent.getString("img");
					eventObj.title=jsonEvent.getString("title");
					eventObj.data=jsonEvent.getString("period");
					
					feeds.add(eventObj);
				}
				hashMapFeed.get(region).put(city, feeds);
				return feeds;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Error parse json",e.toString());
			}
		}
		return hashMapFeed.get(region).get(city);
	}
	
}
