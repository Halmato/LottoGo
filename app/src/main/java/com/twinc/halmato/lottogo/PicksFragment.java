package com.twinc.halmato.lottogo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.twinc.halmato.lottogo.expandableList.CustomExpandableListAdapter;
import com.twinc.halmato.lottogo.expandableList.ExpandableListDataPump;
import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class PicksFragment extends Fragment implements LottoNumberPickerFragment.NumberSelectedListener
{
    private static final String TAG = "DrawSelectionsFragment";

    CustomExpandableListAdapter adapter;
    ExpandableListView expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<Pick>> expandableListDetail;

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

        registerAsPickCapturedListener();

        setUpExpandableList();

    }

    private void registerAsPickCapturedListener() {
        // Bad name, but whatever.

        if(getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).setPicksFragment(this);
        }
    }

    public void addPick(Pick pick) {

        adapter.addPickToCurrentPicks(pick);
    }



    private void setUpExpandableList() {

        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expandableListView);

        expandableListDetail = new ExpandableListDataPump().getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        setAdapter(expandableListTitle, expandableListDetail);

        expandableListView.setAdapter(adapter);

        // ADD PICK?

        setExpandListener();
        setCollapseListener();
        setOnChildClickListener();
    }

    private void setAdapter(List<String> titles, HashMap<String, List<Pick>> detail) {
        adapter = new CustomExpandableListAdapter(getContext(), titles, detail);
    }

    private void setOnChildClickListener() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();*/
                return false;
            }
        });
    }
    private void setCollapseListener() {
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();*/

            }
        });
    }
    private void setExpandListener() {
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public void numberPickerNumberSelected(String number) {
        Toast.makeText(getActivity(), "Number selected: " + number, Toast.LENGTH_SHORT).show();
    }
}