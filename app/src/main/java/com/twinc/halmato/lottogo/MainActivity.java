package com.twinc.halmato.lottogo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twinc.halmato.lottogo.model.Draw;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        ViewPagerItemFragment.FragmentPagerItemCallback
{
    private static final String KEY_DRAWS_LIST = "KEY_DRAWS_LIST";
    private static final String TAG = "ViewPagerActivity";
    private static final String ROOT_JSON_OBJECT_KEY = "draws";
    private static final String[] PAGE_TITLES = {"CAMERA", "Music", "Podcasts", "Other"};

    private static final int CAMERA_FRAGMENT_POSITION = 0;
    private static final int MAIN_FRAGMENT_POSITION = 1;

    private TabLayout tabLayout;
    private ViewPager pager;

    private SharedPreferences sharedPreferences;
    private DatabaseReference db;
    private List<Draw> drawsList = new ArrayList<>();


    public void onReceiveResultsFromCamera(Draw result) {

        showMainFragment();

        createDrawLineItem(result);

    }

    private void createDrawLineItem(Draw result) {

        Toast.makeText(this, "Draw Result: " + result.getResult() + "\n  - "+result.getDate(), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        initializeComponents();

        setupSupportActionBar();
        setupDatabase();
        setupPagerAndTabs();

        getDrawsListFromSharedPreferences();
    }



    private void saveDrawsListToSharedPreferences(ArrayList<Draw> drawsList) {

        String serializedDraws = new Gson().toJson(drawsList);

        sharedPreferences.edit().putString(KEY_DRAWS_LIST,serializedDraws).apply();
    }

    private void addDrawToDrawsList(Draw draw) {
        drawsList.add(draw);

        Toast.makeText(this, "Draw captured: "+draw.getResult(), Toast.LENGTH_SHORT).show();
    }



    // Database

    private void setupDatabase() {

        connectToDatabase();
        readFromDatabase();
    }


    // Tabs and ViewPager

    private void setupPagerAndTabs(){

        tabLayout.setTabTextColors(ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        CustomAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(1,false);

        pager.setOffscreenPageLimit(PAGE_TITLES.length);

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


    // Fragment instantiator

    public static class CustomAdapter extends FragmentPagerAdapter {
        public CustomAdapter (FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            if(position == CAMERA_FRAGMENT_POSITION) {

                fragment = new CameraFragment();

            } else {

                fragment = ViewPagerItemFragment.getInstanceOfFragment(PAGE_TITLES[position]);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return PAGE_TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return PAGE_TITLES[position];
        }
    }


    // Bottom-level methods

    private void showMainFragment()
    {
        pager.setCurrentItem(MAIN_FRAGMENT_POSITION,true);
    }
    private void initializeComponents() {
        tabLayout = (TabLayout) findViewById(R.id.tbl_main_content);
        pager = (ViewPager) findViewById(R.id.vpg_main_content);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    private void getDrawsListFromSharedPreferences() {

        String drawsListJson = sharedPreferences.getString(KEY_DRAWS_LIST,"[]");

        Gson g = new Gson();

        Type type = new TypeToken<ArrayList<Draw>>(){}.getType();

        drawsList = g.fromJson(drawsListJson,type);
    }
    private void connectToDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference(ROOT_JSON_OBJECT_KEY);
    }
    private void readFromDatabase() {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Gson gson = new Gson();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    Draw draw = gson.fromJson(snapshot.getValue().toString(),Draw.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    private void setupSupportActionBar() {
        getSupportActionBar().setElevation(0f);
    }

}