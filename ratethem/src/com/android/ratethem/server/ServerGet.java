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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.android.ratethem.util.RateThemUtil;

/**
 * Class to retrieve information from server.
 */
public class ServerGet {

	private InputStream mInStream = null;
	
	String url = RateThemUtil.SERVER_QUERY_URL;


	private JSONArray mJsonArray = null;

	private String mJson = "";

	public ServerGet() {
	}

	public JSONArray getJSONQuery(String url,String _category) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addTextBody(RateThemUtil.ITEM_CATEGORY, _category);
			/*
			multipartEntity.addTextBody("item_name", mItemNameInfo);
			multipartEntity.addTextBody("place_name", mPlaceInformation);
			multipartEntity.addTextBody("rate", mRatings);
			multipartEntity.addTextBody("location_txt", mLocationInformation);
			multipartEntity.addTextBody("latitude", "567587");
			multipartEntity.addTextBody("longitude", "657543");
			*/
			httpPost.setEntity(multipartEntity.build());
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
	
	public JSONArray getJSONQueryByDistance(String _category, String _radius, String lat, String lng) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addTextBody(RateThemUtil.ITEM_CATEGORY, _category);
			/*
			multipartEntity.addTextBody("item_name", mItemNameInfo);
			multipartEntity.addTextBody("place_name", mPlaceInformation);
			multipartEntity.addTextBody("rate", mRatings);
			multipartEntity.addTextBody("location_txt", mLocationInformation);
			*/
			multipartEntity.addTextBody("query_lat", lat);
			multipartEntity.addTextBody("query_lng", lng);
			multipartEntity.addTextBody("query_radius", _radius);

			httpPost.setEntity(multipartEntity.build());
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
	
	public JSONArray getJSONItemDetails(String _item_id) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addTextBody(RateThemUtil.ITEM_ID, _item_id);
			/*
			multipartEntity.addTextBody("item_name", mItemNameInfo);
			multipartEntity.addTextBody("place_name", mPlaceInformation);
			multipartEntity.addTextBody("rate", mRatings);
			multipartEntity.addTextBody("location_txt", mLocationInformation);
			multipartEntity.addTextBody("latitude", "567587");
			multipartEntity.addTextBody("longitude", "657543");
			*/
			httpPost.setEntity(multipartEntity.build());
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
