package com.umbriaeventi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class EventUrls {

	public static final String serverLink="http://umbriaeventi.herokuapp.com/";

    static private String canonicalName(String name){
        if(name.length()>0){
            String lowerCase=name.toLowerCase();
            String firstCharName=lowerCase.substring(0,1).toUpperCase();
            String lowerName=lowerCase.substring(1,lowerCase.length());
            return (firstCharName+lowerName).replace(" ","%20");
        }
        return "";
    }

    static private String getUrlEvents(String region,String area){
        region= canonicalName(region);
        area= canonicalName(area);
        return serverLink+region+"/"+area;
    }
    static private String getUrlCity(String region){
        region= canonicalName(region);
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
	
	static String[] cities=null;
	
	static public String[] getCities(){			
		if(cities==null){
	    	String pageString=getWebPageString(getUrlCity("Umbria"));
			if(pageString==null) return null;  
			try {
				JSONObject jsonObj = new JSONObject(pageString);
		        JSONArray array=jsonObj.getJSONArray("cities");
		        cities=new String[array.length()];
		        for(int i=0;i<array.length();++i)
		        	cities[i]=array.getString(i);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Error parse json",e.toString());
			}	
		}
    	return cities;
	}
	
	
	static private HashMap<String,ArrayList<EventFeed> > hashMapFeed = new HashMap<String,ArrayList<EventFeed> > ();
	
	static public boolean getNotCityEventsExist(String city){
		return (!hashMapFeed.containsKey(city)||hashMapFeed.get(city)==null);
	}
	
	static public ArrayList<EventFeed> getEvents(String city){
		
		if(getNotCityEventsExist(city)){
			
			ArrayList<EventFeed> feeds=new ArrayList<EventFeed>();
			String strJson=getWebPageString(getUrlEvents("Umbria",city));

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
				hashMapFeed.put(city, feeds);
				return feeds;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Error parse json",e.toString());
			}
		}
		return hashMapFeed.get(city);
	}
	
}
