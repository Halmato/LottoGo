package com.twinc.halmato.lottogo;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerItemFragment extends Fragment
{
    private static final String PAGE_TITLE = "PAGE_TITLE";

    private String pageTitle;

    private FragmentPagerItemCallback callback;
    private SurfaceViewFragmentSetupCompletedCallback cameraAttachedCallback;

    // Empty Constructor
    public ViewPagerItemFragment(){
    }

    // Static method to return Fragment
    public static ViewPagerItemFragment getInstance(String pageTitle){
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_TITLE, pageTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.pageTitle = getArguments().getString(PAGE_TITLE);
        } else {
            Log.d("TAG", "Well... F***.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;

        if(pageTitle=="CAMERA") {

            v = inflater.inflate(R.layout.camera_layout,container,false);
            cameraAttachedCallback.surfaceViewSetupComplete(v);

        } else {


            v = inflater.inflate(R.layout.fragment_view_pager_item, container, false);
            TextView content = ((TextView) v.findViewById(R.id.lbl_pager_item_content));
            content.setText(pageTitle);
            content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    callback.onPagerItemClick(
                            ((TextView) v).getText().toString()
                    );
                }
            });
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (FragmentPagerItemCallback) context;
            cameraAttachedCallback = (SurfaceViewFragmentSetupCompletedCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
        cameraAttachedCallback = null;
    }

    public interface  FragmentPagerItemCallback {
        void onPagerItemClick(String message);
    }

    public interface  SurfaceViewFragmentSetupCompletedCallback {
        void surfaceViewSetupComplete(View rootView);
    }
}