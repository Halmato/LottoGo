package com.twinc.halmato.lottogo.expandableList;

import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private HashMap<String, List<String>> data = new HashMap<>();

    // Constructor
    public ExpandableListDataPump() {

        data.put("Current",new ArrayList<String>());
        data.put("Old",new ArrayList<String>());
    }



    public HashMap<String,List<String>> getData() {
        return data;
    }
}