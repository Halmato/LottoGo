package com.twinc.halmato.lottogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Tiaan on 04/12/2017.
 */

public class LottoNumberPicker extends DialogFragment
{
    private static final String TAG = "LottoNumberPicker";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(! (getParentFragment() instanceof NumberSelectedListener)) {

            Log.e(TAG, "onCreateDialog: Must implement NumberSelectedListener");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View containerView = inflater.inflate(R.layout.picker_lotto_number, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(containerView)
                .setMessage(R.string.lotto_number_picker_message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // don't think anything needs to be done here...
                    }
                });

        setOnClickListenersForAllBalls(containerView);

        return builder.create();
    }

    private void setOnClickListenersForAllBalls(View container) {

        View[] balls = getAllBallViewsInLayout(container);

        for (View ball:balls) {

            ball.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    onBallClicked(view);
                }
            });
        }
    }

    private View[] getAllBallViewsInLayout(View container) {

        ViewGroup vg = (ViewGroup) container;

        View[] allBallViews = new View[vg.getChildCount()];

        for (int i = 0; i < vg.getChildCount(); i++) {
            allBallViews[i] = vg.getChildAt(i);
        }

        return allBallViews;
    }

    private void onBallClicked(View v) {

        notifyNumberPickerListenerOfSelection(v);

        dismiss();
    }

    private void notifyNumberPickerListenerOfSelection(View v) {
        ((NumberSelectedListener) getParentFragment()).numberPickerNumberSelected(getTextInBall(v));
    }

    public String getTextInBall(View v) {

        return ((Button)v).getText().toString();
        /*ViewGroup vg = (ViewGroup) v;

        String ballNumber = "";

        for (int i = 0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i) instanceof TextView) {
                ballNumber = ((TextView) vg.getChildAt(i)).getText().toString();
                break;
            }
        }

        return ballNumber;*/
    }

    public interface NumberSelectedListener {
        void numberPickerNumberSelected(String number);
    }

}
