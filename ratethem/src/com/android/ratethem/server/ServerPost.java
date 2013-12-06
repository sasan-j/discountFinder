package com.android.ratethem.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

/**
 * Class to push data to server.
 */
public class ServerPost {
	
	private static final String TAG = "ServerPost";

	public void pushDataToServer(String serverUrl, JSONObject jsonObject) {
		HttpPost post = new HttpPost(serverUrl);
		HttpResponse response = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();

		if (jsonObject != null) {
			try {
				StringEntity strEntity = new StringEntity(jsonObject.toString());
				strEntity.setContentType("application/json;charset=UTF-8");
				post.setHeader("Accept", "application/json");
				post.setEntity(strEntity);
				HttpConnectionParams.setConnectionTimeout(
						httpClient.getParams(), 10000);
				response = httpClient.execute(post);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException se) {
				se.printStackTrace();
			} catch (ClientProtocolException cp) {
				// TODO Auto-generated catch block
				cp.printStackTrace();
			} catch (IOException io) {
				// TODO Auto-generated catch block
				io.printStackTrace();
			}
		}
	}

}
