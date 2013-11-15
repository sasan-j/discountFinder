package com.example.ratethem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CommonMenu extends Activity{
	
	private Button food, clothing, electronics, homecare ;
	//private Button movies, places, books, healthcare, others;
	protected static final String TAG = "DiscountFinder_S2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_menu);
		addListenerOnButton();
	}
	
	public void addListenerOnButton(){
		// Display button Food
		food = (Button) findViewById(R.id.Food);
		// Display button click listener
		food.setOnClickListener((OnClickListener) CommonListner);
		
		/*
		// Display button Movies
		movies = (Button) findViewById(R.id.Movies);
		// Display button click listener
		movies.setOnClickListener((OnClickListener) CommonListner);
		
		// Display button Places
		places = (Button) findViewById(R.id.Places);
		// Display button click listener
		places.setOnClickListener((OnClickListener) CommonListner);
				
		// Display button Books
		books = (Button) findViewById(R.id.Books);
		// Display button click listener
		books.setOnClickListener((OnClickListener) CommonListner);
		
		// Display button Healthcare
		healthcare = (Button) findViewById(R.id.HealthCare);
		// Display button click listener
		healthcare.setOnClickListener((OnClickListener) CommonListner);
		*
		*/
		
		// Display button Electronics
		electronics = (Button) findViewById(R.id.Electronics);
		// Display button click listener
		electronics.setOnClickListener((OnClickListener) CommonListner);
				
		// Display button Clothing
		clothing = (Button) findViewById(R.id.Clothing);
		// Display button click listener
		clothing.setOnClickListener((OnClickListener) CommonListner);
						
		// Display button Homecare
		homecare = (Button) findViewById(R.id.HomeCare);
		// Display button click listener
		homecare.setOnClickListener((OnClickListener) CommonListner);
				
		
		/*
		// Display button Others
		others = (Button) findViewById(R.id.Others);
		// Display button click listener
		others.setOnClickListener((OnClickListener) OthersListner);
		*/
}
	
	private View.OnClickListener CommonListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Log.d(TAG, "Button Pressed For Entering Data");
			Intent intent = new Intent(v.getContext(), InsertData.class);
			
            startActivity(intent);
            finish();
		}
	};

}
