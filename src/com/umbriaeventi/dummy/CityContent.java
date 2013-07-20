package com.umbriaeventi.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CityContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<CityItem> ITEMS = new ArrayList<CityItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static TreeMap<String, CityItem> ITEM_MAP = new TreeMap<String, CityItem>();

    public static boolean isEmpty(){
    	return ITEMS.isEmpty();
    }
    public static void clear(){
		ITEMS.clear();
    	ITEM_MAP.clear();
    }
    public static void addItem(CityItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class CityItem {
        public String id;
        public String region;
        public String city;

        public CityItem(String id,String region,String city) {
            this.id = id;
            this.region = region;
            this.city = city;
        }

        @Override
        public String toString() {
            return city;
        }
    }
}
