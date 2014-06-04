package com.android.ratethem;


import com.android.ratethem.util.*;
import com.androidquery.AQuery;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class SearchItemView extends Activity {


	private RatingBar discRatingBar;
    //image holder
    private ImageView discImage;
    private TextView discTitle;
    private TextView discPlace;
    private TextView discLocation;
    private TextView userView;
    
    private String mItemName = null;
    private TextView mPlaceInfo;
    private TextView mLocationInfo;
    private TextView mViewsInfo;    
    
    private String mItemID = null;
    private String mItemCategory = null;
    private String mPlaceName = null;
    private String mLocation = null;
    private String mLocLatitude = null;
    private String mLocLongitude = null;
    private String mPicPath = null;
    private String mItemUserViews = null;
	private String mItemImageUrl = null;

    
    private String mPlaceInformation = null;
    private String mRatings = null;
    private String mLocationInformation = null;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_item_view);
		
	    Context mContext = getApplicationContext();

		
		// Get the extras from calling activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mItemID = extras.getString(RateThemUtil.ITEM_ID);
            mItemName = extras.getString(RateThemUtil.ITEM_NAME);
            if(mItemName.length()==0 || mItemName==null )
            	mItemName=getString(R.string.no_info);
	        mPlaceName = extras.getString(RateThemUtil.ITEM_PLACE_NAME);
            if(mPlaceName.length()==0 || mPlaceName==null )
            	mPlaceName=getString(R.string.no_info);
	        mLocation = extras.getString(RateThemUtil.ITEM_LOC);
            if(mLocation.length()==0 || mLocation==null )
            	mLocation=getString(R.string.no_info);
	        mRatings = extras.getString(RateThemUtil.ITEM_RATING);
	        mItemUserViews = extras.getString(RateThemUtil.ITEM_COMMENT);
            if(mItemUserViews.length()==0 || mItemUserViews==null )
            	mItemUserViews=getString(R.string.no_info);
	    	mItemImageUrl = extras.getString(RateThemUtil.ITEM_PIC);
        }     
        //getting details
		//new GetItemDetailsServer().execute();
        
		discImage = (ImageView) findViewById(R.id.discImage);
		
        discRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        discRatingBar.setNumStars(RateThemUtil.NO_STARS);
        
		discTitle = (TextView) findViewById(R.id.discTitle);
		discPlace = (TextView) findViewById(R.id.DiscPlace);
		discLocation = (TextView) findViewById(R.id.discLocation);
		userView = (TextView)findViewById(R.id.userOpinion);
		
		
		discTitle.setText(mItemName);
        discPlace.setText(mPlaceName);
        discLocation.setText(mLocation);
        discRatingBar.setRating(Float.parseFloat(mRatings));
        userView.setText(mItemUserViews);
        
        
		//preset image
		Bitmap presetImage = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.no_image);
					
		//load an image to an ImageView from network, cache image to file and memory
	    AQuery aq = new AQuery(this);
		if(mItemImageUrl != null && mItemImageUrl.length()>1){
		    Drawable d = getResources().getDrawable(R.drawable.no_image);
			aq.id(discImage).image(mItemImageUrl, true, true, d.getIntrinsicWidth()*2, 0, presetImage, AQuery.FADE_IN_NETWORK, AQuery.RATIO_PRESERVE);
		}
			
		// Show the Up button in the action bar.
		//setupActionBar();
        
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_item_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	private class GetItemDetailsServer extends AsyncTask<Void, Void, Void>{
		private JSONArray jArray;
		@Override
		protected Void doInBackground(Void... position) {
			ServerGet serGet = new ServerGet();
			jArray = serGet.getJSONItemDetails(mItemID);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			try {
			for(int i = 0; i < jArray.length(); i++){				
					JSONObject itemName = jArray.getJSONObject(i);
					mItemID = itemName.getString(RateThemUtil.ITEM_ID);				
					mItemCategory = itemName.getString(RateThemUtil.ITEM_CATEGORY);
					mItemName = itemName.getString(RateThemUtil.ITEM_NAME);
					mRatings = itemName.getString(RateThemUtil.ITEM_RATING);					
					mPlaceName = itemName.getString(RateThemUtil.ITEM_PLACE_NAME);
					mLocation = itemName.getString(RateThemUtil.ITEM_LOC);
					mLocLatitude = itemName.getString(RateThemUtil.ITEM_LATITUDE);
					mLocLongitude = itemName.getString(RateThemUtil.ITEM_LONGITUDE);
					//mComments = itemName.getString(RateThemUtil.ITEM_COMMENT);
					mPicPath = itemName.getString(RateThemUtil.ITEM_PIC);
					
					updateFields();
					
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	*/
	
	public void updateFields(){ 
	discTitle.setText(mItemName);
    discPlace.setText(mPlaceName);
    discLocation.setText(mLocation);
    discRatingBar.setRating(Float.parseFloat(mRatings));
    userView.setText(mItemUserViews);
	}

}
