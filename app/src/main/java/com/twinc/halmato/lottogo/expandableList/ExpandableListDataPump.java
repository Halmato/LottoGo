package com.twinc.halmato.lottogo.expandableList;

import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private HashMap<String, List<Pick>> data = new HashMap<>();

    // Constructor
    public ExpandableListDataPump() {

        data.put("Old",new ArrayList<Pick>());
        data.put("Current",new ArrayList<Pick>());
    }



    public HashMap<String,List<Pick>> getData() {
        return data;
    }
}