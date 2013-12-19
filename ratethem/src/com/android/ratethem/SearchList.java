package com.android.ratethem;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.androidquery.*;

/**
 * ListActivity to display all information retrieved from server or database.
 */
public class SearchList extends ListActivity {

	private static final String TAG = "ratethem";
	private ListView mList;
	private ArrayList <ItemInfo> list = new ArrayList<ItemInfo>();
	private String mItemName = null;
	private String mItemCategory = null;
	private RateAdapter mAdapter;
	private String mCriteria = null;
	private String mPlaceName = null;
	private String mRating = null;
	private String mPicPath = null;
	private String mLocation = null;
	private String mComments = null;
	private String mLocLatitude = null;
	private String mLocLongitude = null;
	private String mItemID = null;
	private String mItemImageUrl = null;
	private String mItemLocalImagePath = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//mItemName = extras.getString(RateThemUtil.ITEM_NAME);
			mItemCategory = extras.getString(RateThemUtil.ITEM_CATEGORY);
			mCriteria = extras.getString(RateThemUtil.CRITERIA);
		}
		setTitle(mItemName);
		mList = getListView();
		mList.setOnItemClickListener(mItemListener);
		// Retrieving from Database. Comment this when using Server.
		//getCursorFromDbToDisplay();
		
		// Server information retrieval and display. Uncomment when available.
		new GetHttpData().execute();
	}
	
	private void getCursorFromDbToDisplay(){
		ContentProviderClient client = getBaseContext().getContentResolver()
				.acquireContentProviderClient(RateThemUtil.RATE_URI);
		Cursor cursor = null;
		try {
			cursor = client.query(RateThemUtil.RATE_URI, null,
					RateAgent.RateProvider.ITEM_CATEGORY + "=\"" + mItemCategory + "\"",
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
			JSONArray jArray = serGet.getJSONQuery(RateThemUtil.SERVER_QUERY_URL, mItemCategory);
			try {
			for(int i = 0; i < jArray.length(); i++){				
					JSONObject jsonItem = jArray.getJSONObject(i);
					String imageName = jsonItem.getString(RateThemUtil.ITEM_PIC);
					String imageUrl = null;
					if(imageName != null && imageName.length()>1) 
						imageUrl=RateThemUtil.IMAGE_UPLOAD_DIR_PREFIX+imageName;
					list.add(new ItemInfo(
							jsonItem.getString(RateThemUtil.ITEM_ID),
							jsonItem.getString(RateThemUtil.ITEM_NAME), 
							jsonItem.getString(RateThemUtil.ITEM_CATEGORY), 
							jsonItem.getString(RateThemUtil.ITEM_PLACE_NAME), 
							jsonItem.getString(RateThemUtil.ITEM_RATING), 
							imageUrl, /*Somehow obsolete field kept for compatibility*/
							//jsonItem.getString(RateThemUtil.ITEM_PIC)
							jsonItem.getString(RateThemUtil.ITEM_LOC), 
							jsonItem.getString(RateThemUtil.ITEM_LATITUDE), 
							jsonItem.getString(RateThemUtil.ITEM_LONGITUDE), 
							jsonItem.getString(RateThemUtil.ITEM_COMMENT),
							imageUrl,	/*Image URL*/
							null));	/*Local Image Path*/
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
		mItemName = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_NAME));
		mPlaceName = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_PLACE_NAME));
		mLocation = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_LOC));
		mComments = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_COMMENT));
		mPicPath = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
		mRating = cursor.getString(cursor
				.getColumnIndex(RateAgent.RateProvider.ITEM_RATING));
	}
	
	
	private void getInformationFromServer(int position){
		ItemInfo currentItemInfo = list.get(position);
		ServerGet serGet = new ServerGet();
		JSONArray jArray = serGet.getJSONItemDetails(currentItemInfo.getmItemID());
		try {
		for(int i = 0; i < jArray.length(); i++){				
				JSONObject itemName = jArray.getJSONObject(i);
				mItemID = itemName.getString(RateThemUtil.ITEM_ID);				
				mItemCategory = itemName.getString(RateThemUtil.ITEM_CATEGORY);
				mItemName = itemName.getString(RateThemUtil.ITEM_NAME);
				mRating = itemName.getString(RateThemUtil.ITEM_RATING);					
				mPlaceName = itemName.getString(RateThemUtil.ITEM_PLACE_NAME);
				mLocation = itemName.getString(RateThemUtil.ITEM_LOC);
				mLocLatitude = itemName.getString(RateThemUtil.ITEM_LATITUDE);
				mLocLongitude = itemName.getString(RateThemUtil.ITEM_LONGITUDE);
				mComments = itemName.getString(RateThemUtil.ITEM_COMMENT);
				mPicPath = itemName.getString(RateThemUtil.ITEM_PIC);
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
			//getInformationFromCursor(position);
			/***************************
			Intent intent = new Intent(SearchList.this, SearchItemView.class);
			intent.putExtra(RateThemUtil.ITEM_ID, mItemID);
			intent.putExtra(RateThemUtil.ITEM_CATEGORY, mItemCategory);
			intent.putExtra(RateThemUtil.ITEM_NAME, mItemName);
			intent.putExtra(RateThemUtil.ITEM_PLACE_NAME, mPlaceName);
			intent.putExtra(RateThemUtil.ITEM_LOC, mLocation);
			intent.putExtra(RateThemUtil.ITEM_LATITUDE, mLocLatitude);
			intent.putExtra(RateThemUtil.ITEM_LONGITUDE, mLocLongitude);
			intent.putExtra(RateThemUtil.ITEM_PIC, mPicPath);
			intent.putExtra(RateThemUtil.ITEM_COMMENT, mComments);
			intent.putExtra(RateThemUtil.ITEM_RATING, mRating);
			intent.putExtra(RateThemUtil.CRITERIA, mCriteria);
			startActivity(intent); 
			 */
			// Uncomment this when server available.
			//TODO: uncomment after list view works!
			//getInformationFromServer(position);
			ItemInfo currentItemInfo = list.get(position);
			loadItemInfo(currentItemInfo);
			Intent intent = new Intent(SearchList.this, SearchItemView.class);
			intent.putExtra(RateThemUtil.ITEM_ID, mItemID);
			intent.putExtra(RateThemUtil.ITEM_CATEGORY, mItemCategory);
			intent.putExtra(RateThemUtil.ITEM_NAME, mItemName);
			intent.putExtra(RateThemUtil.ITEM_PLACE_NAME, mPlaceName);
			intent.putExtra(RateThemUtil.ITEM_LOC, mLocation);
			intent.putExtra(RateThemUtil.ITEM_LATITUDE, mLocLatitude);
			intent.putExtra(RateThemUtil.ITEM_LONGITUDE, mLocLongitude);
			intent.putExtra(RateThemUtil.ITEM_PIC, mItemImageUrl);
			intent.putExtra(RateThemUtil.ITEM_COMMENT, mComments);
			intent.putExtra(RateThemUtil.ITEM_RATING, mRating);
			//intent.putExtra(RateThemUtil.CRITERIA, mCriteria);
			startActivity(intent);
		}
	};
	
	private void loadItemInfo(ItemInfo _itemInfo){
		mItemID = _itemInfo.getmItemID();
		mItemCategory = _itemInfo.getmItemCategory();
		mItemName = _itemInfo.getmItemName();
		mPlaceName = _itemInfo.getmPlaceName();
		mLocation = _itemInfo.getmLocation();
		mLocLatitude = _itemInfo.getmLocLatitude();
		mLocLongitude = _itemInfo.getmLocLongitude();
		mPicPath = _itemInfo.getmPicPath();
		mComments = _itemInfo.getmComments();
		mRating = _itemInfo.getmRating();
		mItemImageUrl = _itemInfo.getmPicPath();

	}
	
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
				icon = (ImageView) v.findViewById(R.id.pic);
				itemInfo = (TextView) v.findViewById(R.id.loc_info);
				rate = (RatingBar) v.findViewById(R.id.ratingBar);
				//TODO: change number of stars to a global variable
				rate.setNumStars(RateThemUtil.NO_STARS);
			}
			
			//preset image
			Bitmap presetImage = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.no_image);
			
			ItemInfo item = mItems.get(position);
			if(item != null){
				String picPath = item.getmPicPath();
				/*
				if (picPath != null && picPath.length()>1) {
				    Drawable d = getResources().getDrawable(R.drawable.no_image);
				    //Log.d("ratethem", "heigh is : "+(d.getIntrinsicHeight()));
				    /* Decode the JPEG file into a Bitmap *//*
					Bitmap mImageBitmap = BitmapFactory.decodeFile(picPath);
					icon.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));
				}*/
				
				//load an image to an ImageView from network, cache image to file and memory
				String picUrl = item.getmImageUrl();
		        AQuery aq = new AQuery(view);
				if(picUrl != null && picUrl.length()>1){
				    Drawable d = getResources().getDrawable(R.drawable.no_image);
					aq.id(icon).image(picUrl, true, true, d.getIntrinsicWidth()/2, 0, presetImage, AQuery.FADE_IN_NETWORK, AQuery.RATIO_PRESERVE);
				}
				//icon.setImageBitmap(BitmapFactory.decodeFile(picPath));
				itemInfo.setText(item.getmItemName());
				rate.setRating(Float.parseFloat(item.getmRating()));
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
			String picPath = cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_PIC));
			if (picPath != null && picPath.length()>1) {
			    Drawable d = getResources().getDrawable(R.drawable.no_image);
			    //Log.d("ratethem", "heigh is : "+(d.getIntrinsicHeight()));
			    /* Decode the JPEG file into a Bitmap */
				Bitmap mImageBitmap = BitmapFactory.decodeFile(picPath);
				icon.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));
			}
			
			TextView locInfo = (TextView) view.findViewById(R.id.loc_info);
			RatingBar rate = (RatingBar) view.findViewById(R.id.ratingBar);
			rate.setNumStars(RateThemUtil.NO_STARS);

			locInfo.setText(cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_NAME)));
			rate.setRating(Float.parseFloat(cursor.getString(cursor
					.getColumnIndex(RateAgent.RateProvider.ITEM_RATING))));
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
