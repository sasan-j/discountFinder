package com.android.ratethem;

import com.android.ratethem.util.*;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class SearchItemView extends Activity {


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
		
		// Get the extras from calling activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                mItemName = extras.getString(RateThemUtil.ITEM_NAME);
                mCriteria = extras.getString(RateThemUtil.CRITERIA);
        }
        String discPlaceTxt = extras.getString(RateThemUtil.ITEM_PLACE_NAME);
        String discLocationTxt = extras.getString(RateThemUtil.ITEM_LOC);
        String discRating = extras.getString(RateThemUtil.ITEM_RATING);
        String userViewTxt = extras.getString(RateThemUtil.ITEM_COMMENT);
        String discImagePath = extras.getString(RateThemUtil.ITEM_PIC);
        
		discImage = (ImageView) findViewById(R.id.discImage);
		discTitle = (TextView) findViewById(R.id.discTitle);
		discPlace = (TextView) findViewById(R.id.DiscPlace);
		discLocation = (TextView) findViewById(R.id.discLocation);
		userView = (TextView)findViewById(R.id.userOpinion);
		
		discTitle.setText(mItemName);
        discPlace.setText(discPlaceTxt);
        discLocation.setText(discLocationTxt);
        //discRating
        userView.setText(userViewTxt);
        if(discImagePath!=null){
    		Bitmap mImageBitmap = BitmapFactory.decodeFile(discImagePath);
    		//discImage.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, discImage.getWidth(), discImage.getHeight(), false));
    	    Drawable d = getResources().getDrawable(R.drawable.no_image);
    	    discImage.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));
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

}
