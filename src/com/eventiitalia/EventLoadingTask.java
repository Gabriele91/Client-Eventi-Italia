package com.eventiitalia;

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
        EventDialog.show(context, title, message);
    }

    @Override
    protected void onPostExecute(Object result){
    	callback.onPostExecute(result);
        EventDialog.hide();
    }
    
	@Override
	protected Object doInBackground(Object... params) {
		return callback.doInBackground(params);
	}
	
}
