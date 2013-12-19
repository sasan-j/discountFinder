package com.android.ratethem;

//import com.android.ratethem.R;
import com.android.ratethem.util.RateThemUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class CommonMenu extends Activity {

	private Button food, clothing, electronics, homecare;
	// private Button movies, places, books, healthcare, others;
	protected static final String TAG = "ratethem";

    // String to get the criteria Publish  or Search.
	private String mCriteria = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_menu);
		// Get the extras from calling activity.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCriteria = extras.getString("criteria");
			Log.d(TAG, "Button Name: " + mCriteria);
		}
		addListenerOnButton();
	}

	public void addListenerOnButton() {
		// Display button Food
		food = (Button) findViewById(R.id.Food);
		// Display button click listener
		food.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startInsertData(v, RateThemUtil.FOOD);		
			}
		});;

		/*
		 * // Display button Movies movies = (Button) findViewById(R.id.Movies);
		 * // Display button click listener
		 * movies.setOnClickListener((OnClickListener) CommonListner);
		 * 
		 * // Display button Places places = (Button) findViewById(R.id.Places);
		 * // Display button click listener
		 * places.setOnClickListener((OnClickListener) CommonListner);
		 * 
		 * // Display button Books books = (Button) findViewById(R.id.Books); //
		 * Display button click listener
		 * books.setOnClickListener((OnClickListener) CommonListner);
		 * 
		 * // Display button Healthcare healthcare = (Button)
		 * findViewById(R.id.HealthCare); // Display button click listener
		 * healthcare.setOnClickListener((OnClickListener) CommonListner);
		 */

		// Display button Electronics
		electronics = (Button) findViewById(R.id.Electronics);
		// Display button click listener
		electronics.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startInsertData(v, RateThemUtil.ELECTRONICS);
			}
		});;

		// Display button Clothing
		clothing = (Button) findViewById(R.id.Clothing);
		// Display button click listener
		clothing.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startInsertData(v, RateThemUtil.CLOTHING);
				
			}
		});

		// Display button Homecare
		homecare = (Button) findViewById(R.id.HomeCare);
		// Display button click listener
		homecare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startInsertData(v, RateThemUtil.HOMECARE);				
			}
		});

		/*
		 * // Display button Others others = (Button) findViewById(R.id.Others);
		 * // Display button click listener
		 * others.setOnClickListener((OnClickListener) OthersListner);
		 */
	}

	/**
	 * insert the item information requested and start next activity.
	 * @param view
	 * @param itemName
	 */
	private void startInsertData(View view, String categoryName){
		Intent intent = new Intent(view.getContext(), InsertData.class);
		intent.putExtra(RateThemUtil.CRITERIA, mCriteria);
		intent.putExtra(RateThemUtil.ITEM_CATEGORY, categoryName);
		startActivity(intent);
		finish();
	}

}
