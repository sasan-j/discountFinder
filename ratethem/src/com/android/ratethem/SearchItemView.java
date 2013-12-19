package com.android.ratethem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.ratethem.server.ServerGet;
import com.android.ratethem.util.*;
import com.androidquery.AQuery;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
    private TextView userView;;
    
    private String mItemName = null;
    static TextView imageDetails = null;
    private TextView mPlaceInfo;
    private TextView mLocationInfo;
    private TextView mViewsInfo;
    private Bitmap mPhoto = null;
    
    
    private String mItemID = null;
    private String mItemCategory = null;
    private String mPlaceName = null;
    private String mLocation = null;
    private String mLocLatitude = null;
    private String mLocLongitude = null;
    private String mPicPath = null;
    private String mItemUserViews = null;
	private String mItemImageUrl = null;

    
    
    private String mCriteria = null;
    private String mSearch = null;
    private String mPublish = null;
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
            mCriteria = extras.getString(RateThemUtil.CRITERIA);

	        mPlaceName = extras.getString(RateThemUtil.ITEM_PLACE_NAME);
	        mLocation = extras.getString(RateThemUtil.ITEM_LOC);
	        mRatings = extras.getString(RateThemUtil.ITEM_RATING);
	        mItemUserViews = extras.getString(RateThemUtil.ITEM_COMMENT);
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
			
		/*	
        if(discImagePath!=null && discImagePath.length()>1){
    		Bitmap mImageBitmap = BitmapFactory.decodeFile(discImagePath);
    		//discImage.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, discImage.getWidth(), discImage.getHeight(), false));
    	    Drawable d = getResources().getDrawable(R.drawable.no_image);
    	    discImage.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));
        }
		*/
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
	
	public void updateFields(){ 
	discTitle.setText(mItemName);
    discPlace.setText(mPlaceName);
    discLocation.setText(mLocation);
    //discRating
    //userView.setText(mComments);
	}

}
