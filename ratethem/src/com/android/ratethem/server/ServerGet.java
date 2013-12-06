package com.android.ratethem.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to retrieve information from server.
 */
public class ServerGet {

	private InputStream mInStream = null;

	private JSONArray mJsonArray = null;

	private String mJson = "";

	public ServerGet() {
	}

	public JSONArray getJSONUrl(String url) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			mInStream = httpEntity.getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(mInStream, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			mInStream.close();
			mJson = sb.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mJsonArray = new JSONArray(mJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mJsonArray;
	}

}
