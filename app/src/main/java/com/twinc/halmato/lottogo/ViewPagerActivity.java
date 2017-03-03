package com.twinc.halmato.lottogo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twinc.halmato.lottogo.model.Draw;
import com.twinc.halmato.lottogo.model.Draws;

import java.io.IOException;


public class ViewPagerActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        ViewPagerItemFragment.FragmentPagerItemCallback, ViewPagerItemFragment.SurfaceViewFragmentSetupCompletedCallback
{
    private static final String BASE_JSON_OBJECT_KEY = "base";
    final String TAG = "ViewPagerActivity";
    final int REQUEST_CAMERA_PERMISSION_ID = 1001;

    private CameraSource cameraSource;

    private TabLayout tabLayout;
    private ViewPager pager;

    private SurfaceView cameraSurfaceView;
    private TextView resultTextView;

    private static final String[] pageTitles = {"CAMERA", "Music", "Podcasts", "Other"};

    private DatabaseReference db;

    private void findViews()
    {
        tabLayout = (TabLayout) findViewById(R.id.tbl_main_content);
        pager = (ViewPager) findViewById(R.id.vpg_main_content);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {

            case REQUEST_CAMERA_PERMISSION_ID: {
                startCamera(grantResults[0]);
            }
        }
    }

    private void startCamera(int grantResult)
    {
        if (requestIsGranted(grantResult)) {

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            try {
                cameraSource.start(cameraSurfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean requestIsGranted(int grantResult)
    {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        connectToDatabase();
        readFromDatabase();

        getSupportActionBar().setElevation(0f);

        findViews();

        setUpPagerAndTabs();
    }

    private void readFromDatabase() {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Gson gson = new Gson();
                Toast.makeText(ViewPagerActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                Draws d = gson.fromJson(dataSnapshot.toString(),Draws.class);
                /*for (Draw draw:draws.getDraws()) {
                    Toast.makeText(ViewPagerActivity.this, "Date: "+draw.getDate(), Toast.LENGTH_SHORT).show();
                }*/

                /*
                working
                for (DataSnapshot draw:dataSnapshot.getChildren()) {


                    Draw d = gson.fromJson(draw.getValue().toString(),Draw.class);
                    Toast.makeText(ViewPagerActivity.this, "Date: " + d.getDate(), Toast.LENGTH_SHORT).show();

                }*/
                
                //Draw myPOJO = gson.fromJson(dataSnapshot.getValue().toString(), Draw.class);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void connectToDatabase()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference(BASE_JSON_OBJECT_KEY);
    }

    private void setUpCamera()
    {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "setUpCamera: Detector Dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .build();


            cameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder)
                {
                    try {
                        if (ActivityCompat.checkSelfPermission(ViewPagerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ViewPagerActivity.this,new String[] { Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION_ID);

                            return;
                        }
                        cameraSource.start(cameraSurfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
                {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder)
                {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>()
            {
                @Override
                public void release()
                {

                }
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections)
                {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0) {
                        resultTextView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                               StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); i++) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                resultTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    private void setUpPagerAndTabs(){

        tabLayout.setTabTextColors(ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        CustomAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(1,false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


        tabLayout.addOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(0).setIcon(android.R.drawable.ic_menu_camera);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onPagerItemClick(String message) {
        Toast.makeText(this, message + "!", Toast.LENGTH_SHORT).show();
    }



    /*For Pagers with a smaller/static number of pages, use FragmentPagerAdapter. It keeps visited
    caches fragments which have been opened in memory. If you require a large/dynamic number
    of pages, use FragmentStatePagerAdapter instead.
    */
    public static class CustomAdapter extends FragmentPagerAdapter
    {

        public CustomAdapter (FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return ViewPagerItemFragment.getInstance(pageTitles[position]);
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return pageTitles[position];
        }
    }

    @Override
    public void surfaceViewSetupComplete(View rootView)
    {
        cameraSurfaceView = (SurfaceView) rootView.findViewById(R.id.surface_view);
        resultTextView = (TextView) rootView.findViewById(R.id.result_text_view);

        setUpCamera();
    }



}