package org.hpier.redcaplite.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hpier.redcaplite.config.REDcapLiteConfig;
import org.hpier.redcaplite.service.HTTPJSONService;
 
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.WebView;

/**
 * Used to send data from apps to Server.
 * @author vijayakumar thirumalai
 */

 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AppToServer extends AsyncTask<String, Void, String>{

	
	    private WebView localWebView;
		private ProgressDialog dialog;
		private Context localContext;
		
		public AppToServer(Context context, WebView webView){
			  this.localContext = context;
			  this.localWebView = webView;
			  dialog = new ProgressDialog(context);
			
		}
		
		 @Override
	     protected void onPreExecute(){
			 super.onPreExecute();
	 		 dialog.setMessage("Data Transfer In-Process..Please wait");
	 		 dialog.setIndeterminate(true);
	 		 dialog.setCanceledOnTouchOutside(false);
			 dialog.show();
		 }
		
	 
		@Override
		protected String doInBackground(String... params) {
			 
			HashMap form = new HashMap<String, String>();
			String APIKEY = null;
			String URL = null;
			SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME, localContext.MODE_PRIVATE);
			APIKEY = settings.getString("APIKEY", null);
			URL =  settings.getString("URL", null);
			
		    HTTPJSONService httpJSONService = new HTTPJSONService();
		    List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
		    requestParam.add(new BasicNameValuePair("token", APIKEY));
		    requestParam.add(new BasicNameValuePair("content", "record"));
		    requestParam.add(new BasicNameValuePair("format", "json"));
		    requestParam.add(new BasicNameValuePair("type", "flat"));
		    requestParam.add(new BasicNameValuePair("overwriteBehavior", "normal "));
		    requestParam.add(new BasicNameValuePair("returnFormat", "json"));
		    
			Set<String> idList = settings.getStringSet("idList", new HashSet<String>());
			for (String s : idList) {
				String formData = settings.getString(s, "");
				 
			    requestParam.add(new BasicNameValuePair("data", "[ " +formData + " ]" ));
			    if (URL != null && APIKEY !=null) {
			    	String dataImportReturn =  httpJSONService.connectREDCapServer(URL, "POST", requestParam);
			    }
 			    
			}
			
	 	    return "";
 
		    
		}
 		
		@Override
		 protected void onPostExecute(String result) {
		 
	  	   dialog.dismiss();
		   this.localWebView.loadUrl("javascript:showMsgAfterSendToServer('Successfuly Data Transfered!')"); 
	  	    
		 }
 
} 