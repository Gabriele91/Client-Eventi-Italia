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
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static TreeMap<String, DummyItem> ITEM_MAP = new TreeMap<String, DummyItem>();

    public static boolean isEmpty(){
    	return ITEMS.isEmpty();
    }
    public static void clear(){
		ITEMS.clear();
    	ITEM_MAP.clear();
    }
    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String city;

        public DummyItem(String id,String city) {
            this.id = id;
            this.city = city;
        }

        @Override
        public String toString() {
            return city;
        }
    }
}
