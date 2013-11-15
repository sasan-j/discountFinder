package com.android.ratethem;

import com.android.ratethem.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScreenOne extends Activity {

	private Button search, publish;
	protected static final String TAG = "DiscountFinder_S1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_one);
		addListenerOnButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_one, menu);
		return true;
	}

	public void addListenerOnButton(){
				// Display button Search
				search = (Button) findViewById(R.id.Search);
				// Display button click listener
				search.setOnClickListener(mButtonListener);
				
				// Display button Rate
				publish = (Button) findViewById(R.id.Publish);
				// Display button click listener
				publish.setOnClickListener(mButtonListener);
	}
	
	private View.OnClickListener mButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button b = (Button)v;
			Intent i=new Intent(ScreenOne.this, CommonMenu.class);
			i.putExtra("criteria", b.getText().toString());
	        startActivity(i);
			
		}
	};
	
	private View.OnClickListener SearchListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d(TAG, "Search Button Button Pressed");
			//Intent intent = new Intent(v.getContext(), CommonMenu.class);
			Intent i=new Intent(ScreenOne.this, CommonMenu.class);
	        startActivity(i);
           // startActivity(i);
            finish();
		}
	};
	
	private View.OnClickListener PublishListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Log.d(TAG, "Publish Button Pressed");
			//Intent intent = new Intent(v.getContext(), CommonMenu.class);
			Intent i=new Intent(ScreenOne.this, CommonMenu.class);
            startActivity(i);
            finish();
		}
	};
}

