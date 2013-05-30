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

public class uEventUrls {

	public static final String serverCitiesLink="http://umbriaeventi.herokuapp.com/cities";
	public static final String serverCityLink="http://umbriaeventi.herokuapp.com/city?name=";
	
	
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
	    	String pageString=getWebPageString(serverCitiesLink);
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
	
	
	static private HashMap<String,ArrayList<uEventFeed> > hashMapFeed = new HashMap<String,ArrayList<uEventFeed> > ();
	
	static public boolean getNotCityEventsExist(String city){
		return (!hashMapFeed.containsKey(city)||hashMapFeed.get(city)==null);
	}
	
	static public ArrayList<uEventFeed> getEvents(String city){
		
		if(getNotCityEventsExist(city)){
			
			ArrayList<uEventFeed> feeds=new ArrayList<uEventFeed>();
			String linkCity=city.replace(" ", "%20");
			String strJson=getWebPageString(serverCityLink+linkCity);
			if(strJson==null) return feeds;
			
	        JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(strJson);
		        JSONArray array;
		        array = jsonObj.getJSONArray("feeds");
				for(int i=0;i<array.length();++i){
					
					JSONObject jsonEvent=array.getJSONObject(i);
					
					uEventFeed eventObj=new uEventFeed();
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
