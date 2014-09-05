package org.hpier.redcaplite.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * This service class used to communicate with remote server
 * @author vxthirumalai
 *
 */
public class HTTPJSONService {

		public HTTPJSONService(){
			
		}
	
		
		public String connectREDCapServer(String url, String method, List<NameValuePair> params){
			
			if (url != null) {
				try { 
					HttpParams httpParameters = new BasicHttpParams();
					//HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
					//HttpConnectionParams.setSoTimeout(httpParameters, 3000);
					
					 
					
					HttpClient httpClient = new DefaultHttpClient(httpParameters);
					 
		            HttpEntity httpEntity = null;
		            HttpResponse httpResponse = null;
		            
		            if (method.equalsIgnoreCase("POST")) {
		            	HttpPost httpPost = new HttpPost(url);
		            	httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		            	if (params != null) {
 								httpPost.setEntity(new UrlEncodedFormEntity(params));
 		            	}
		            	
						httpResponse = httpClient.execute(httpPost);
						
		            	
		            } else if (method.equalsIgnoreCase("GET")){
		            	HttpGet httpGet = new HttpGet(url);
		            	if (params != null) {
		            		url = url+"?" + URLEncodedUtils.format(params, "utf-8");
		            	}
		            	
		            	httpResponse = httpClient.execute(httpGet);
		            }
		            
		            httpEntity = httpResponse.getEntity();
		            return EntityUtils.toString(httpEntity);
		            
		        } catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
            
			return null;
		}
		
		
	
}
