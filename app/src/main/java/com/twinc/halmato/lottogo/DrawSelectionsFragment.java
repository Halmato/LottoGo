package com.twinc.halmato.lottogo;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.twinc.halmato.lottogo.model.DrawResultModel;

import java.io.IOException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class DrawSelectionsFragment extends Fragment
{
    private static final String TAG = "DrawSelectionsFragment";

    ArrayList<DrawResultModel> dataModels;
    ListView listView;
    private static DrawResultsListAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.activity_main,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();

        setAdapter();
    }

    public void addPick(String pick) {
        dataModels.add(new DrawResultModel(pick));
    }

    private void setAdapter() {
        adapter= new DrawResultsListAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
    }

    private void initializeComponents() {

        listView = (ListView) getActivity().findViewById(R.id.myList);
        dataModels= new ArrayList<>();
    }


}