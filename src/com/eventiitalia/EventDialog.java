package com.eventiitalia;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Gabriele on 21/07/13.
 */
public class EventDialog {

    static private ProgressDialog dialog=null;

    static public void show(Context activity, String title, String desc){
        dialog=ProgressDialog.show(activity, title, desc,  true);
    }
    static public void hide(){
        if (dialog != null) {
            dialog.dismiss();
            dialog=null;
        }
    }

}
