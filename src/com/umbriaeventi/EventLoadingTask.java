package com.umbriaeventi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class EventLoadingTask  extends AsyncTask <Object, Void, Object> {

	public interface CallBack{	
		 Object doInBackground(Object... params) ;	
		 void   onPostExecute(Object result);	
	}
	
	private CallBack callback=null;
    private ProgressDialog dialog;
    private String title;
    private String message;
    private Context context;
    
	public EventLoadingTask(Context context,String title,String message,CallBack callback){
		this.context=context;
		this.title=title;
		this.message=message;
		this.callback=callback;
	}

    @Override
    protected void onPreExecute()
    {
    	dialog = ProgressDialog.show(context, title, message,  true);
    }

    @Override
    protected void onPostExecute(Object result){
    	dialog.dismiss();
    	callback.onPostExecute(result);
    }
    
	@Override
	protected Object doInBackground(Object... params) {
		return callback.doInBackground(params);
	}
	
}
