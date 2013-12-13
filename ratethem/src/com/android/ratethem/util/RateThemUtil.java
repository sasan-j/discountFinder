package com.android.ratethem.util;

import android.net.Uri;

import com.android.ratethem.providers.RateAgent;

public class RateThemUtil {
	
	public static final String SERVER_URL = "/localhost/8081";
	
	public static final String CRITERIA = "criteria";
	
	public static final String RADIUS = "radius";
    
    public static final String FOOD = "Food";
    
    public static final String HOMECARE = "HomeCare";
    
    public static final String ELECTRONICS = "Technology";
    
    public static final String CLOTHING = "Clothing";
    
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
    
    public static final Uri RATE_URI = Uri
            .parse("content://com.android.ratethem.providers.RateContentProvider" + "/"
                    + RateAgent.RateProvider.TABLE_NAME);
}
