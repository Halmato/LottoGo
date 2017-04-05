package com.twinc.halmato.lottogo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class PicksFragment extends Fragment
{
    private static final String TAG = "DrawSelectionsFragment";

    ArrayList<Pick> pickList;
    ListView listView;
    private PicksListAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_picks,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).setPicksFragment(this);
        }

        initializeComponents();

        setAdapter();
    }

    public void addPick(Pick pick) {

        adapter.add(pick);
    }

    private void setAdapter() {
        adapter= new PicksListAdapter(pickList,getApplicationContext());
        listView.setAdapter(adapter);
    }

    private void initializeComponents() {

        listView = (ListView) getActivity().findViewById(R.id.myList);
        pickList = new ArrayList<>();
    }

}