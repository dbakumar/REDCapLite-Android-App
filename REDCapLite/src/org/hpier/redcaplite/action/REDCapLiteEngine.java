package org.hpier.redcaplite.action;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hpier.redcaplite.config.REDcapLiteConfig;
import org.hpier.redcaplite.jobs.BuildProjectTask;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * This is gateway engine for building communication between native java code and js
 * @author vijayakumar thirumalai
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class REDCapLiteEngine {
	Context localContext;
	WebView localWebView;
	public REDCapLiteEngine(Context context, WebView webView){
		this.localContext = context;
		this.localWebView = webView;
	}
	
	@JavascriptInterface
	public String buildProject(String url, String apiKey, String passcode){
 		String[] params = {url, apiKey, passcode};
		new BuildProjectTask(this.localContext, this.localWebView).execute(params);
 		return "Done";
	}
	
	@JavascriptInterface
	public String appsToServer(){
 		String[] params = { };
		new AppToServer(this.localContext, this.localWebView).execute(params);
 		return "Done";
	}
	
	
	@JavascriptInterface
	public String getMetaData(){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
	    String METADATA =   settings.getString("METADATA", ""); 
 		return METADATA;
	}
	
	@JavascriptInterface
	public String getFormList(){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
	    String FORMLIST =   settings.getString("FORMLIST", ""); 
 		return FORMLIST;
	}
	
	@JavascriptInterface
	public String getForm(String formName){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		String FORM = "<form name=\"FORMID_"+formName+"\" id=\"FORMID_"+formName+"\"   method=\"post\" action=\"\"  data-ajax=\"false\"> ";
		FORM +=   settings.getString("FORM_"+formName, "");
		FORM += "<select id=\""+ formName  +"_complete\"  name=\"" +formName+"_complete\"><option value=\"\"></option><option value=\"0\" selected=\"\">Incomplete</option><option value=\"1\">Unverified</option><option value=\"2\">Complete</option></select> <input type=\"button\" value=\"Save Record\" onclick=\"saveRecord(\'#FORMID_"+formName+"');\" ></form>";
 		return FORM; 
	}
	
	
	@JavascriptInterface
	public void setCurrentStudyId(String id){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString("currentstudyid", id);
		editor.commit();
		
	}
	
	@JavascriptInterface
	public String saveRecord(String jsonString, String formName) throws JSONException{
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
	    String currentStudyId =   settings.getString("currentstudyid", "");
	    String idList = settings.getString("idList", "");
	    Format formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    if (currentStudyId.equalsIgnoreCase("0")) {
	    	String study_id = null;
	    	JSONObject fieldObject= new JSONObject(jsonString);
	    	study_id = fieldObject.getString("study_id");
	    	if (study_id != null && ! study_id.isEmpty()) {
	    		currentStudyId = study_id;
	    	} else {
	    		currentStudyId = formatter.format(new Date());
	    	}
	    }
	    Editor editor = settings.edit();
		editor.putString(currentStudyId+formName, jsonString);

		int found = 0;
		if (idList != null &&  idList.length() > 0 ) {
			String[] tokens = idList.split("||");
		 
			for (int j = 0; j < tokens.length; j++) {
				String token = tokens[j];
				if (token != null && token.length() > 4 &&  (token.equals(currentStudyId) || token.subSequence(0, 14).equals(currentStudyId.subSequence(0, 14)))) {
					found = 1; break;
				}
			}
		 
		}
		 
		if (found == 0) {
			if (idList.length() == 0) { 
				idList =   currentStudyId+formName;
			} else {
				idList = idList + "||" + currentStudyId+formName;
			}
			editor.putString("idList", idList);
		}
		editor.commit();
 	    return "OK";
	    
	}
	
	
	
	@JavascriptInterface
	public String advancedSaveRecord(String jsonString, String formName){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
	    String currentStudyId =   settings.getString("currentstudyid", "");
	    Set<String> idList = settings.getStringSet("idList", new HashSet<String>());
	    
 	    Format formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    if (currentStudyId.equalsIgnoreCase("0")) {
	    	currentStudyId = formatter.format(new Date());
	    }
	    Editor editor = settings.edit();
		editor.putString(currentStudyId+formName, jsonString);

		idList.add(currentStudyId+formName);
		editor.putStringSet("idList", idList);
		editor.commit();
 	    return "OK";
	    
	}
	
	
	@JavascriptInterface
	public String getIdList(){ 
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		String idList = settings.getString("idList", "");
		return idList;
	}
	
	@JavascriptInterface
	public String getAdvancedIdList(){ 
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		Set<String> idList = settings.getStringSet("idList", new HashSet<String>());
		Set<String> ids =  new HashSet<String>();
		for (String s : idList) {
			ids.add(s.substring(0, 14));
		}
 		return TextUtils.join("||", ids);
	}
	
	@JavascriptInterface
	public String getFormData(String formName){
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME,Context.MODE_PRIVATE);
		String currentStudyId =   settings.getString("currentstudyid", "");
		String formData ="";
		if (!currentStudyId.equalsIgnoreCase("0")) {
			formData = settings.getString(currentStudyId+formName, "");
		}
 		return formData;
	}
	
	
	
	
}
