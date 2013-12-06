package com.android.ratethem;

import com.android.ratethem.R;
import com.android.ratethem.util.RateThemUtil;

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
	protected static final String TAG = "ratethem";

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

	public void addListenerOnButton() {
		// Display button Search
		search = (Button) findViewById(R.id.Search);
		// Display button click listener
		search.setOnClickListener(mSearchListener);

		// Display button Rate
		publish = (Button) findViewById(R.id.Publish);
		// Display button click listener
		publish.setOnClickListener(mPublishListener);
	}

	private View.OnClickListener mSearchListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String search = v.getContext().getString(R.string.search);
			Log.d(TAG, "Search Button Button Pressed: " + search);
			// Intent intent = new Intent(v.getContext(), CommonMenu.class);
			Intent i = new Intent(ScreenOne.this, SearchCriteria.class);
			// Add Search criteria. This is required in insertdata activity to
			// display or not display certain screen items.
			i.putExtra(RateThemUtil.CRITERIA, search);
			startActivity(i);
		}
	};

	private View.OnClickListener mPublishListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String publish = v.getContext().getString(R.string.publish);
			Log.d(TAG, "Publish Button Pressed: " + publish);
			// Intent intent = new Intent(v.getContext(), CommonMenu.class);
			Intent i = new Intent(ScreenOne.this, CommonMenu.class);
			i.putExtra(RateThemUtil.CRITERIA, publish);
			startActivity(i);
		}
	};
}
