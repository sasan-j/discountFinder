package com.android.ratethem;

import com.android.ratethem.util.RateThemUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Activity to add criteria to search.
 */
public class SearchCriteria extends Activity {

	private static final String TAG = "ratethem";

	private Context mContext;

	private Spinner mItemSelection;

	private Spinner mLocationSelection;

	private Spinner mDistance;

	private EditText mManualLocation;

	private Button mDoneButton;

	private String mItemSearched = null;

	private String mDistanceString = null;

	private String mLocationInfo = null;

	private String mCriteria = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_info);
		mContext = this;
		Bundle extras = getIntent().getExtras();
		if (null != extras) {
			mCriteria = extras.getString(RateThemUtil.CRITERIA);
		}
		mManualLocation = (EditText) findViewById(R.id.add_location);
		addListenerOnSpinnerSelection();
		addListenerToButton();
	}

	private void addListenerOnSpinnerSelection() {
		mItemSelection = (Spinner) findViewById(R.id.search_item);
		mLocationSelection = (Spinner) findViewById(R.id.location1);
		mDistance = (Spinner) findViewById(R.id.radius1);
		mItemSelection.setOnItemSelectedListener(mItemSelectedListener);
		mLocationSelection.setOnItemSelectedListener(mLocationSelected);
		mDistance.setOnItemSelectedListener(mDistanceSelectedListener);
	}

	private void addListenerToButton() {
		mDoneButton = (Button) findViewById(R.id.search);
		mDoneButton.setOnClickListener(mDoneListener);
	}

	private AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mItemSearched = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private AdapterView.OnItemSelectedListener mLocationSelected = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String curLoc = getString(R.string.current_location);
			String locSpinnerInfo = parent.getItemAtPosition(position)
					.toString();
			if (!curLoc.equals(locSpinnerInfo)) {
				mManualLocation.setVisibility(View.VISIBLE);
			} else {
				mManualLocation.setVisibility(View.GONE);
				mLocationInfo = parent.getItemAtPosition(position).toString();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private AdapterView.OnItemSelectedListener mDistanceSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String[] distVal = mContext.getResources().getStringArray(
					R.array.radius_value);
			mDistanceString = distVal[position];
			Log.d(TAG, "Distance Chosen: " + mDistanceString);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private View.OnClickListener mDoneListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), SearchList.class);
			if (null != mCriteria) {
				intent.putExtra(RateThemUtil.CRITERIA, mCriteria);
			} else {
				intent.putExtra(RateThemUtil.CRITERIA, v.getContext()
						.getResources().getString(R.string.search));
			}
			if (null != mItemSearched) {
				//intent.putExtra(RateThemUtil.ITEM_NAME, mItemSearched);
				intent.putExtra(RateThemUtil.ITEM_CATEGORY, mItemSearched);
			}
			if (null != mDistanceString) {
				intent.putExtra(RateThemUtil.RADIUS, mDistanceString);
			}
			if (mManualLocation.getVisibility() == View.VISIBLE) {
				mLocationInfo = mManualLocation.getText().toString();
			}
			if (null != mLocationInfo) {
				intent.putExtra(RateThemUtil.ITEM_LOC, mLocationInfo);
			}
			startActivity(intent);
			finish();
		}
	};
}
