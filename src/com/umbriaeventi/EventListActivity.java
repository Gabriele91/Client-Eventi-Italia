package com.umbriaeventi;


import com.umbriaeventi.dummy.CityContent;

import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.ConnectivityManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


/**
 * An activity representing a list of uEvents. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link EventListFragment} and the item details
 * (if present) is a {@link EventDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link EventListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class EventListActivity extends FragmentActivity
        implements EventListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    static private String[] cities=null;
    

    public void showDialogErrorMessage(String message){
    	
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	    dialog.setIcon(R.drawable.ic_launcher);
	    dialog.setTitle("Errore");
	    dialog.setMessage(message);
	    dialog.setPositiveButton("chiudi", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
            finish();
            System.exit(0);
        }
       });
	    AlertDialog alert=dialog.create();
	    alert.show();
    	
    }
    
    public boolean addCity(){
    	//
    	CityContent.clear();
    	//download cities (downloaded only first call)
    	cities=EventUrls.getCities(EventMenu.getInstance().regionSelect());
    	//no cities, exit from app
    	if(cities==null) return false;    	
    	//add cities
    	for(int i=0;i<cities.length;++i){
    		CityContent.addItem(
    				new CityContent.CityItem(
    						Integer.toString(i),
                            EventMenu.getInstance().regionSelect(),
                            cities[i]
    						));
    	}
    	return true;
    }
    private void setLayout(){

    	//set layout
	    setContentView(R.layout.activity_uevent_list);
	
	    if (findViewById(R.id.uevent_detail_container) != null) {
	        // The detail container view will be present only in the
	        // large-screen layouts (res/values-large and
	        // res/values-sw600dp). If this view is present, then the
	        // activity should be in two-pane mode.
	        mTwoPane = true;
	
	        // In two-pane mode, list items should be given the
	        // 'activated' state when touched.
	        ((EventListFragment) getSupportFragmentManager()
	                .findFragmentById(R.id.uevent_list))
	                .setActivateOnItemClick(true);
	    }
	    //set serch menu events
	    SearchView srchCity=(SearchView) findViewById(R.id.searchCity);
	    srchCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
            	searchCity(newText);
                return true;
            }
        });
    }
    
    private void searchCity(String name){
    	EventListFragment elf=((EventListFragment) getSupportFragmentManager() .findFragmentById(R.id.uevent_list));
    	elf.setFilterList(name);
    }
    private void checkTheInternetConnection(){
	    ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);	
	    NetworkInfo[] ninfo=  conMgr.getAllNetworkInfo();
	    for(int i=0;i<ninfo.length;++i)
	    	if(ninfo[i].getState()==State.DISCONNECTED)
        		showDialogErrorMessage("Errore di conessione, bisogna avere una connessione ad internet per utilizzare questa applicazione");	
	    		
   }
    private void reloadList(){
        //change layout
        EventListFragment elf=((EventListFragment) getSupportFragmentManager() .findFragmentById(R.id.uevent_list));
        elf.loadListItems();
    }
    
    private class DownloadCitiesTask extends AsyncTask<String, Void, Object> {
    	
    	 boolean connesionExt=true;
    	
        protected Object doInBackground(String... args) {
        	long start = System.currentTimeMillis();
            //add cities
            connesionExt=addCity();
            //get end time
        	long end = System.currentTimeMillis();
        	//calc screen time
        	long timepass=start-end;
        	//sleep
        	if(timepass<1000){
				try {
					Thread.sleep(1000-timepass);
				}
				catch (InterruptedException e) {
					Log.e("task error",e.toString());
				}
        	}
        	//
            return null;
        }

        protected void onPostExecute(Object result) {
        	//set layout
        	setLayout();
            //show dialog errors
            if(!connesionExt) 
        		showDialogErrorMessage("Errore di conessione, non vi sono citta' con eventi");
	        //force selection first item   
            if(mTwoPane){
		        Thread thread = new Thread(){        	
		        	public void run(){
						try {
							Thread.sleep(100);//lol...
			                onItemSelected("0");
						}
						catch (InterruptedException e) {
							Log.e("task error",e.toString());
						}     		
		        	}
		        };
		        thread.start();
            }
            //now can show menu
            showOptionMenu=true;
            invalidateOptionsMenu();
            //enable rotation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
   }    

    //show option menu?
    private boolean showOptionMenu=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showOptionMenu) {
            menu.clear();
            return EventMenu.getInstance().initGraphicsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);//EventMenu.getInstance().initGraphicsMenu(menu);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //save old region
        String oldRegion=EventMenu.getInstance().regionSelect();
        //selected?
        if( EventMenu.getInstance().optionsSelected(item) ){
            if(!oldRegion.equals(EventMenu.getInstance().regionSelect())){
            	if(EventUrls.getNotCitiesExist(EventMenu.getInstance().regionSelect())){
                    new EventLoadingTask(this,
				            			 "Attendi",
				            			 "Sto scaricando le citta'",
				            			 new EventLoadingTask.CallBack() {						
											 @Override
											 public Object doInBackground(Object... params) {
											     addCity();
												 return null;
											 }					
											 @Override
											 public void onPostExecute(Object result) {
							            		reloadList();
                                                 if(mTwoPane) onItemSelected("0");
                                             }
											
										 }).execute();
	                }
            		else{
            	        addCity();
            			reloadList();
                        if(mTwoPane) onItemSelected("0");
            		}
            }
            return true;
        }
        else
           return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
    	//connection policy 
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);	
        //save state
        super.onCreate(savedInstanceState);
        //only at start
        if(cities==null){
            //disable rotation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            //get connetion state
        	checkTheInternetConnection();
            //create location objects
            EventLocation.getInstance().callOnCreateMainThread(this.getApplicationContext(),
                                                               this.getBaseContext());
	        //set splash screen
	        setContentView(R.layout.activity_splash_screen);
	        //create interface
	        new DownloadCitiesTask().execute();
        }
        else{
        	//no download
        	addCity();
        	//set layout
        	setLayout();
            //show menu
            showOptionMenu=true;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        EventDialog.hide();
    }

    /**
     * Callback method from {@link EventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(EventDetailFragment.ARG_ITEM_ID, id);
            EventDetailFragment fragment = new EventDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.uevent_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, EventDetailActivity.class);
            detailIntent.putExtra(EventDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
