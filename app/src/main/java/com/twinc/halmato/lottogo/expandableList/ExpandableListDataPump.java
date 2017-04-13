package com.twinc.halmato.lottogo.expandableList;

import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    public static final String PICK_LIST_TITLE_OLD = "Old";
    public static final String PICK_LIST_TITLE_CURRENT = "Current";

    private HashMap<String, List<Pick>> data = new HashMap<>();

    // Constructor
    public ExpandableListDataPump() {

        data.put(PICK_LIST_TITLE_OLD,new ArrayList<Pick>());
        data.put(PICK_LIST_TITLE_CURRENT,new ArrayList<Pick>());
        
        loadDataFromSharedPreferences();
        
    }

    private void loadDataFromSharedPreferences() {
    }


    public HashMap<String,List<Pick>> getData() {
        return data;
    }
}