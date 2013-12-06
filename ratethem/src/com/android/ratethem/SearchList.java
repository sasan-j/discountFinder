package com.android.ratethem;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.ratethem.providers.RateAgent;
import com.android.ratethem.server.ServerGet;
import com.android.ratethem.util.RateThemUtil;

import android.app.ListActivity;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * ListActivity to display all information retrieved from server or database.
 */
public class SearchList extends ListActivity {

	private static final String TAG = "ratethem";

	private ListView mList;
	
	private ArrayList <ItemInfo> list = new ArrayList<ItemInfo>();

	private String mItemName = null;

	private RateAdapter mAdapter;

	private String mCriteria = null;
	
	private String mPlaceName = null;
	
	private String mRating = null;
	
	private byte[] mBlob = null;
	
	private String mLocation = null;
	
	private String mComments = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mItemName = extras.getString(RateThemUtil.ITEM_NAME);
			mCriteria = extras.getString(RateThemUtil.CRITERIA);
		}
		setTitle(mItemName);
		mList = getListView();
		mList.setOnItemClickListener(mItemListener);
		// Retrieving from Database. Comment this when using Server.
		getCursorFromDbToDisplay();
		
		// Server information retrieval and display. Uncomment when available.
//		new GetHttpData().execute();
	}
	
	private void getCursorFromDbToDisplay(){
		ContentProviderClient client = getBaseContext().getContentResolver()
				.acquireContentProviderClient(RateThemUtil.RATE_URI);
		Cursor cursor = null;
		try {
			cursor = client.query(RateThemUtil.RATE_URI, null,
					RateAgent.RateProvider.ITEM_NAME + "=\"" + mItemName + "\"",
					null, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "Cursor: " + cursor.getCount());
		mAdapter = new RateAdapter(getBaseContext(), cursor);
		setListAdapter(mAdapter);
	}
	
	private class GetHttpData extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			ServerGet serGet = new ServerGet();
			JSONArray jArray = serGet.getJSONUrl(RateThemUtil.SERVER_URL);
			try {
			for(int i = 0; i < jArray.length(); i++){				
					JSONObject itemName = jArray.getJSONObject(i);
					list.add(new ItemInfo(itemName.getString(RateThemUtil.ITEM_NAME), itemName.getString(RateThemUtil.ITEM_RATING), itemName.get(RateThemUtil.ITEM_PIC)));
					
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			setListAdapter(new ListAdapter(SearchList.this, R.layout.activity_data_list, list));
		}
		
	}
	
	private void getInformationFromCursor(int position){
		Cursor cursor = mAdapter.getCursor();
		cursor.moveToPosition(position);
		mPlaceName = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_PLACE_NAME));
		mComments = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_COMMENT));
		mLocation = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_LOC));
		mBlob = cursor.getBlob(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
		mRating = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_RATING));
	}
	
	private void getInformationFromServer(int position){
		ServerGet serGet = new ServerGet();
		JSONArray jArray = serGet.getJSONUrl(RateThemUtil.SERVER_URL);
		try {
		for(int i = 0; i < jArray.length(); i++){				
				JSONObject itemName = jArray.getJSONObject(i);
				mPlaceName = itemName.getString(RateThemUtil.ITEM_NAME);
				mComments = itemName.getString(RateThemUtil.ITEM_COMMENT);
				mLocation = itemName.getString(RateThemUtil.ITEM_LOC);
//				mBlob = itemName.get(RateThemUtil.ITEM_PIC);
				mRating = itemName.getString(RateThemUtil.ITEM_RATING);	
				
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Comment this when server available.
			getInformationFromCursor(position);
			// Uncomment this when server available.
//			getInformationFromServer(position);
			Intent intent = new Intent(SearchList.this, InsertData.class);
			intent.putExtra(RateThemUtil.ITEM_PLACE_NAME, mPlaceName);
			intent.putExtra(RateThemUtil.ITEM_PIC, mBlob);
			intent.putExtra(RateThemUtil.ITEM_COMMENT, mComments);
			intent.putExtra(RateThemUtil.ITEM_LOC, mLocation);
			intent.putExtra(RateThemUtil.ITEM_RATING, mRating);
			intent.putExtra(RateThemUtil.CRITERIA, mCriteria);
			startActivity(intent);
		}
	};
	
	private class ListAdapter extends ArrayAdapter<ItemInfo>{
		
		private List<ItemInfo> mItems;

		public ListAdapter(Context context, int resource, List<ItemInfo> items) {
			super(context, resource, items);
			this.mItems = items;
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent){
			View v = view;
			ImageView icon = null;
			TextView itemInfo = null;
			RatingBar rate = null;
			
			if(v == null){
				LayoutInflater vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.rowlayout, null);
				icon = (ImageView) view.findViewById(R.id.pic);
				itemInfo = (TextView) view.findViewById(R.id.loc_info);
				rate = (RatingBar) view.findViewById(R.id.ratingBar);
				rate.setNumStars(5);
			}
			
			ItemInfo item = mItems.get(position);
			if(item != null){
				Object blob = item.getPicture();
				itemInfo.setText(item.getName());
				rate.setRating(Float.parseFloat(item.getRating()));
			}
			return v;
		}
		
	}

	private class RateAdapter extends CursorAdapter {

		public RateAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView icon = (ImageView) view.findViewById(R.id.pic);
			TextView locInfo = (TextView) view.findViewById(R.id.loc_info);
			RatingBar rate = (RatingBar) view.findViewById(R.id.ratingBar);
			rate.setNumStars(5);

			locInfo.setText(cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_LOC)));
			String rating = cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_RATING));
			Log.d(TAG, "Get rating: " + rating);
			Log.d(TAG, "Get rating in float: " + Float.parseFloat(rating));
			rate.setRating(Float.parseFloat(cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_RATING))));
			byte[] blob = cursor.getBlob(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
			if (blob != null) {
				icon.setImageBitmap(BitmapFactory
						.decodeStream(new ByteArrayInputStream(blob)));
			}

		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.rowlayout, parent, false);
			if (c != null) {
				bindView(view, context, c);
			}
			return view;
		}

	}
}
