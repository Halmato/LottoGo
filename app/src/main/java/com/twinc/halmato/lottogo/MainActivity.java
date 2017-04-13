package com.twinc.halmato.lottogo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twinc.halmato.lottogo.model.Pick;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPagerItemFragment.FragmentPagerItemCallback
{
    private static final String TAG = "MainActivity";
    private static final int PICKS_FRAGMENT_POSITION = 1;
    private TabLayout tabLayout;
    private ViewPager pager;

    private static final String KEY_DRAW_RESULTS_LIST = "KEY_DRAWS_LIST";
    private static final String ROOT_JSON_OBJECT_KEY = "draws";

    private SharedPreferences sharedPreferences;
    private DatabaseReference db;

    private List<Pick> drawResultsList = new ArrayList<>();

    private PicksFragment picksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        setupPagerAndTabs();

        connectToDatabase();

        loadLatestDraws();
    }

    private void loadLatestDraws() {

        if(db != null) {
            loadLatestDrawsFromDatabase();

        } else {
            loadDrawsListFromSharedPreferences();
        }
    }

    private void initializeComponents() {

        tabLayout = (TabLayout) findViewById(R.id.tbl_main_content);
        pager = (ViewPager) findViewById(R.id.vpg_main_content);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setupPagerAndTabs(){

        tabLayout.setTabTextColors(ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));


        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(1,false);

        pager.setOffscreenPageLimit(adapter.getCount());

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

    private void connectToDatabase() {

        try
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            db = database.getReference(ROOT_JSON_OBJECT_KEY);

        } catch (Exception e) {
            Log.e(TAG, "connectToDatabase: Could not connect to database!");

        }
    }

    private void loadLatestDrawsFromDatabase() {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Gson gson = new Gson();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    Pick draw = gson.fromJson(snapshot.getValue().toString(),Pick.class);
                    addDrawToDrawsList(draw);
                }

                saveDrawResultsListToSharedPreferences(drawResultsList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void loadDrawsListFromSharedPreferences() {

        String drawResultsListJson = sharedPreferences.getString(KEY_DRAW_RESULTS_LIST,"[]");

        Gson g = new Gson();

        Type type = new TypeToken<ArrayList<Pick>>(){}.getType();

        drawResultsList = g.fromJson(drawResultsListJson,type);
    }

    public void onReceivePickFromCamera(Pick result) {

        showPicksFragment();

        picksFragment.addPick(result);
    }


    private void saveDrawResultsListToSharedPreferences(List<Pick> drawsList) {

        String serializedDraws = new Gson().toJson(drawsList);

        sharedPreferences.edit().putString(KEY_DRAW_RESULTS_LIST,serializedDraws).apply();
    }

    private void addDrawToDrawsList(Pick draw) {
        drawResultsList.add(draw);
    }

    private void showPicksFragment()
    {
        pager.setCurrentItem(PICKS_FRAGMENT_POSITION,true);

    }

    public void setPicksFragment(PicksFragment picksFragment) {
        this.picksFragment = picksFragment;
    }





}