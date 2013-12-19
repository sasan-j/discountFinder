package com.android.ratethem.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class RateAgent {

	public static final String AUTHORITY = "com.android.ratethem.providers.RateContentProvider";

	public static final class RateProvider implements BaseColumns {

		public static final Uri CONTENT_URI = Uri
				.parse("content://com.android.ratethem.providers.RateContentProvider");

		public static final String TABLE_NAME = "Items";

		public static final String _ID = "_id";

		public static final String ITEM_NAME = "item_name";

		public static final String ITEM_CATEGORY = "item_category";
		
		public static final String ITEM_PIC = "item_pic";

		public static final String ITEM_RATING = "item_rating";
		
		public static final String ITEM_PLACE_NAME = "item_place_name";

		public static final String ITEM_LOC = "item_location";
		
		public static final String ITEM_LATITUDE = "item_location_latitude";
		
		public static final String ITEM_LONGITUDE = "item_location_longitude";

		public static final String ITEM_COMMENT = "item_comment";
	}

}
