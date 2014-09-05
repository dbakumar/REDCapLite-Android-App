package org.hpier.redcaplite.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;



public class WebAppInterfaceTest {
	

	
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterfaceTest(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
    
   
    
    
}