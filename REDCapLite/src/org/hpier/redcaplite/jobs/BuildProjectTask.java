package org.hpier.redcaplite.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hpier.redcaplite.R;
import org.hpier.redcaplite.config.REDcapLiteConfig;
import org.hpier.redcaplite.main.MainActivity;
import org.hpier.redcaplite.service.HTTPJSONService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * Used to build the dynamic jquery mobile form(s)
 * @author vijayakumar thirumalai
 *
 */

public class BuildProjectTask extends AsyncTask<String, Void, String>{
    private WebView localWebView;
	private ProgressDialog dialog;
	private Context localContext;
	
	public BuildProjectTask(Context context, WebView webView){
		  this.localContext = context;
		  this.localWebView = webView;
		  dialog = new ProgressDialog(context);
		
	}
	
	 @Override
     protected void onPreExecute(){
		 super.onPreExecute();
 		 dialog.setMessage("Project Building In-Process....");
 		 dialog.setIndeterminate(true);
 		 dialog.setCanceledOnTouchOutside(false);
		 dialog.show();
		  
	 }
	
 
	@Override
	protected String doInBackground(String... params) {
		 
		HashMap form = new HashMap<String, String>();
		
	    HTTPJSONService httpJSONService = new HTTPJSONService();
	    List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
	    requestParam.add(new BasicNameValuePair("token", params[1]));
	    requestParam.add(new BasicNameValuePair("content", "metadata"));
	    requestParam.add(new BasicNameValuePair("format", "json"));
	    String dataDictionary =  httpJSONService.connectREDCapServer(params[0], "POST", requestParam );
	    Set<String> formNames = new HashSet<String>();
	    
	    
	   
	    try {
	    	JSONArray jArray = new JSONArray(dataDictionary);
	    	for (int i =0 ; i < jArray.length() ; i++) {
	    		JSONObject fieldObject = jArray.getJSONObject(i);
	    		String formName = fieldObject.getString("form_name");
	    		formNames.add(formName);
	    		buildForm(formName , fieldObject, form);
	    		
	    	}
 			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    
		SharedPreferences settings = this.localContext.getSharedPreferences(REDcapLiteConfig.PRE_APP_SETTING_NAME, localContext.MODE_PRIVATE);
		Editor editor = settings.edit();
		
		editor.putString("APPPASSCODE", params[2]);
		editor.putString("APIKEY", params[1]);
		editor.putString("URL", params[0]);
		editor.putString("METADATA",dataDictionary);
		editor.putString("FORMLIST", buildFormList(formNames));
		
		for (String formName : formNames){
			String formHTML = (String)  form.get(formName);
			editor.putString("FORM_"+formName,formHTML); 
    	}
		
		editor.commit();
		//settings.edit().putString("APIKEY", params[1]).commit();  
		//settings.edit().putString("URL", params[0]).commit();  
	    //settings.edit().putString("METADATA",dataDictionary).commit();  
	    
	    return dataDictionary;
		//return "Communicated to Server";
	    
	}
	
 
	
	@Override
	 protected void onPostExecute(String result) {
  	   dialog.dismiss();
	   //this.localWebView.loadUrl("javascript:completedMessage()");  	  
	   this.localWebView.loadUrl("file:///android_asset/www/index.html"); 
  	    
	 }
	
	public String buildFormList(Set<String> formNames){
		 StringBuffer sfFormNames = new StringBuffer("<ul data-role=\"listview\" data-inset=\"true\" data-filter=\"true\">");
		 
		for (String formName : formNames){
			sfFormNames.append("<li><a href='#select-item?form="+formName+"' >" + formName + "</a></li>");
    	}
		sfFormNames.append("</ul>");
		return sfFormNames.toString();
	}
	
	
	
	public void buildForm (String formName, JSONObject fieldObject, HashMap form){
		String fieldType;
		String fieldName;
		String fieldLable;
		String choicesOrCalculations;
		String htmlFormFields = "";
		
		String formFields = (String)  form.get(formName);
		htmlFormFields = formFields!=null? formFields :  htmlFormFields;
		
		try {
			fieldType = fieldObject.getString("field_type");
			fieldName = fieldObject.getString("field_name");
			fieldLable = fieldObject.getString("field_label");
			choicesOrCalculations  = fieldObject.getString("select_choices_or_calculations");
			if (fieldType != null){
			   if (fieldType.equalsIgnoreCase("text")) {
				   htmlFormFields += "<label for=\"" +  fieldName  +"\" class=\"\"> " +   fieldLable +" :</label>" 
						   			+ "<input type=\"text\" name=\""+  fieldName   +"\" id=\""+  fieldName    +"\" value=\"\" class=\"ui-input-text ui-body-c ui-corner-all ui-shadow-inset\">"	;
			   } 	
			   
			   if (fieldType.equalsIgnoreCase("notes")) {
				   htmlFormFields += "<label for=\"" +  fieldName  +"\" class=\"\"> " + fieldLable +" :</label>" 
						    
						   			+ "<textarea  name=\""+  fieldName   +"\" id=\""+  fieldName    +"\" value=\"\" row=\"4\" class=\"ui-input-text ui-body-c ui-corner-all ui-shadow-inset\"/>"	;
			   } 	
			   
			   if (fieldType.equalsIgnoreCase("radio")) {
				   
				   if (choicesOrCalculations != null && choicesOrCalculations.length() > 0 )  {
					  htmlFormFields += "<label for=\"" +  fieldName  +"\" class=\"\"> " + fieldLable +" :</label>" 
						   			+ "<input type=\"hidden\" name=\""+  fieldName   +"\" id=\""+  fieldName    +"\" value=\"\" class=\"ui-input-text ui-body-c ui-corner-all ui-shadow-inset\">"	 ;
					  
					  htmlFormFields += " <fieldset data-role=\"controlgroup\" data-mini=\"true\"> ";
					  //StringTokenizer tokens = new StringTokenizer(choicesOrCalculations, "\\\\n");
					 // choicesOrCalculations = choicesOrCalculations.replaceAll("\\\\n", ":#:");
				     // String [] choices = choicesOrCalculations.split(":#:");
					  
					  //StringTokenizer tokens = new StringTokenizer(choicesOrCalculations, "|");
					  // choicesOrCalculations = choicesOrCalculations.replaceAll("|", ":#:");
					   String [] choices = choicesOrCalculations.split("\\|");
				      
				      for (int i =0; i< choices.length; i++) {
				    	  String  option = choices[i];
				    	  String [] optionKeyValue = option.split(",");
				    	  htmlFormFields += "<input type=\"radio\" name=\""+  fieldName   +"__radio\" value=\""+ optionKeyValue[0] +"\"  onclick=\"$('#"+ fieldName+"').val(this.value);\" id=\""+ fieldName + "_" + i  +"\"  /> " + "<label for=\""+ fieldName  + "_" + i +"\">" + optionKeyValue[1] + "</label>";
				    			  			
				      }
				      
				      htmlFormFields += "</fieldset>";
 			      
				   }
			   } 
			   
			   
			   if (fieldType.equalsIgnoreCase("checkbox")) {
				   
				   if (choicesOrCalculations != null && choicesOrCalculations.length() > 0 )  {
					  htmlFormFields += "<label for=\"" +  fieldName  +"\" class=\"\"> " + fieldLable +" :</label>" ;
						   			
					  
					  htmlFormFields += " <fieldset data-role=\"controlgroup\" data-mini=\"true\"> ";
					  //StringTokenizer tokens = new StringTokenizer(choicesOrCalculations, "\\\\n");
					 // choicesOrCalculations = choicesOrCalculations.replaceAll("\\\\n", ":#:");
				     // String [] choices = choicesOrCalculations.split(":#:");
					  
					  //StringTokenizer tokens = new StringTokenizer(choicesOrCalculations, "|");
					  // choicesOrCalculations = choicesOrCalculations.replaceAll("|", ":#:");
					   String [] choices = choicesOrCalculations.split("\\|");
				      
				      for (int i =0; i< choices.length; i++) {
				    	  String  option = choices[i];
				    	  String [] optionKeyValue = option.split(",");
				    	  htmlFormFields += "<input type=\"checkbox\" name=\"chkn__"+  fieldName   +"\" value=\""+ optionKeyValue[0].trim() +"\"  onclick=\"$('#"+ fieldName +"___" + optionKeyValue[0].trim() +"').val((this.checked)?'1':'0');\" id=\""+ fieldName + "_" + i  +"\"  /> " + "<label for=\""+ fieldName  + "_" + i +"\">" + optionKeyValue[1] + "</label>"
				    	  					  + "<input type=\"hidden\" name=\""+  fieldName  +"___"+ optionKeyValue[0].trim() +"\" id=\""+  fieldName +"___"+ optionKeyValue[0].trim() +"\" value=\"\"  class=\"ui-input-text ui-body-c ui-corner-all ui-shadow-inset\">"	 ;	
				      }
				      
				      htmlFormFields += "</fieldset>";
 			      
				   }
			   } 
			   
			   
			   if (fieldType.equalsIgnoreCase("dropdown")) {
				   htmlFormFields += "<label for=\"" +  fieldName  +"\" class=\"\"> " + fieldLable +" :</label>" ;
				   htmlFormFields += "<select name=\"" +fieldName+"\" id=\""+ fieldName + "\">";
				   String [] choices = choicesOrCalculations.split("\\|");
				   for (int i =0; i< choices.length; i++) {
				    	  String  option = choices[i];
				    	  String [] optionKeyValue = option.split(",");
				    	  htmlFormFields +=   "<option value=\""+optionKeyValue[0].trim()+"\">" + optionKeyValue[1] +"</option>";  
				    			  			
				   }
				   htmlFormFields += "</select>";
				   
			   }
			   
			   
			}
			
		 form.put(formName, htmlFormFields);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 
	}
	
	

}
