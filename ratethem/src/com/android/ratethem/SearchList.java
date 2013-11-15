package com.android.ratethem;

import java.io.ByteArrayInputStream;

import com.android.ratethem.providers.RateAgent;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchList extends ListActivity {
	
	private static final String TAG = "SearchList";
	
	private ListView mList;
	
	private String mSelection = null;
	
	private RateAdapter mAdapter;
	
	private String mCriteria = null;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		String itemName = null;
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			itemName = extras.getString("item_name");
			mCriteria = extras.getString("criteria");
		}
		setTitle(itemName);
		mList = getListView();
		mList.setOnItemClickListener(itemListener);
		mSelection = RateAgent.RateProvider.ITEM_NAME + "="+itemName;
		Cursor cursor = getContentResolver().query(RateAgent.RateProvider.CONTENT_URI, null, null, null, null);
		Log.d(TAG, "Cursor: "+cursor.getCount());
		mAdapter = new RateAdapter(getBaseContext(), cursor);
		setListAdapter(mAdapter);
	}
	
	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Cursor cursor = mAdapter.getCursor();
			cursor.moveToPosition(position);
			String comments = cursor.getString(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_COMMENT));
			String location = cursor.getString(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_LOC));
			byte[] blob = cursor.getBlob(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
			String rate = cursor.getString(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_RATING));
			
			Intent intent = new Intent(SearchList.this, InsertData.class);
			intent.putExtra("pic", blob);
			intent.putExtra("comment", comments);
			intent.putExtra("location", location);
			intent.putExtra("rate", rate);
			intent.putExtra("criteria", mCriteria);
		}
	};
	
	

	private class RateAdapter extends CursorAdapter{
		
		public RateAdapter(Context context, Cursor c) {
			super(context, c);
		}


		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView icon = (ImageView) view.findViewById(R.id.pic);
			TextView locInfo = (TextView) view.findViewById(R.id.loc_info);
			RatingBar rate = (RatingBar) view.findViewById(R.id.ratingBar);
			
			locInfo.setText(cursor.getString(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_LOC)));
			rate.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_RATING))));
			byte[] blob = cursor.getBlob(cursor.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
			if(blob != null){
				icon.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(blob)));
			}
			
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.rowlayout, parent, false);
			if(c != null){
				bindView(view, context, c);
			}
			return view;
		}

	}
}
