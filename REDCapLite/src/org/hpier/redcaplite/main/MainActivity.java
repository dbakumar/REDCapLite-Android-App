package org.hpier.redcaplite.main;

import org.hpier.redcaplite.R;
import org.hpier.redcaplite.action.REDCapLiteEngine;
import org.hpier.redcaplite.config.REDcapLiteConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	WebView localWebView;
	
	/**
	 * Main Method to open browser component inside Andriod App.
	 * @author vijayakumar thirumalai
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        localWebView = (WebView) findViewById(R.id.localWebViewControl);
        
        
        // To set the view client for avoid workflow goes out of App. 
        localWebView.setWebViewClient(new WebViewClient()  
        {  
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		super.onPageFinished(view, url);
        		
        		localWebView.loadUrl("javascript:pageInit()");
        	}
        	
        	/**
        	 * Only allows local resources inside the App. all others links loaded into native browser 
        	 */
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url)  
            {  
                if (url != null && url.startsWith("file://android_asset/"))  
                {  
                	return false;
                	  
                }  
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }  
        });
        
        SharedPreferences settings = getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		
		 //****** Disable on after testing 
	     //editor.clear();
	     //editor.commit();
  
		
		final String APPPASSCODE =  settings.getString("APPPASSCODE", "");
		 if (APPPASSCODE.length() > 0) {
		 	validatePassCode(APPPASSCODE);
		 }
		
    	String APIKey =   settings.getString("APIKEY", "");
        WebSettings webSettings = localWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); 
        localWebView.addJavascriptInterface(new WebAppInterfaceTest(this ), "Android" ); // For Testing only
        localWebView.addJavascriptInterface(new REDCapLiteEngine(this, localWebView), "REDCapLiteEngine" );
        
    	//Load the local asset files 
    	if (APIKey.length() > 0) {
    		 localWebView.loadUrl("file:///android_asset/www/index.html"); 
    	} else {
    		 localWebView.loadUrl("file:///android_asset/www/apisetting.html"); 
    	}
        
        
        
    }
    
    public void validatePassCode(final String PASSCODE){
    	 
    	AlertDialog.Builder prompt = new AlertDialog.Builder(this);
    	prompt.setTitle("Authendication"); //Set Alert dialog title here
    	prompt.setMessage("Enter Your PassCode"); //Message here

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        prompt.setView(input);
        
        
        
        
        
	        prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	             //You will get as string input data in this variable.
	             // here we convert the input to a string and show in a toast.
	             String userPasscode = input.getEditableText().toString();
	             if (PASSCODE.equals(userPasscode)) {
	            	 //Continue with app 
	             } else {
	            	 dialog.cancel();
	            	 moveTaskToBack(true);
	                 android.os.Process.killProcess(android.os.Process.myPid());
	                 System.exit(1);
	             }
	             
	                          
	            }  
	        });  
	        prompt.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int whichButton) {
	                // Canceled.
	                  dialog.cancel();
	             	  moveTaskToBack(true);
	                  android.os.Process.killProcess(android.os.Process.myPid());
	                  System.exit(1);
	              }
	        }); 
            AlertDialog alertDialog = prompt.create();
            alertDialog.show();
            alertDialog.setCancelable(false);
       
     } 

    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && localWebView.canGoBack()) {
        	localWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    
    
}
