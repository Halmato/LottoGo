package com.twinc.halmato.lottogo;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twinc.halmato.lottogo.model.DrawResultModel;

import java.util.ArrayList;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class DrawResultsListAdapter extends ArrayAdapter<DrawResultModel> implements View.OnClickListener{

    private ArrayList<DrawResultModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvDate;

        TextView tvball1;
        TextView tvball2;
        TextView tvball3;
        TextView tvball4;
        TextView tvball5;
        TextView tvball6;
    }

    // Constructor
    public DrawResultsListAdapter(ArrayList<DrawResultModel> data, Context context) {
        super(context, R.layout.draw_results_list_view_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DrawResultModel dataModel=(DrawResultModel)object;

        /*switch (v.getId())
        {
            case R.id.item_info: // this is the info button in the list item
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DrawResultModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.draw_results_list_view_item, parent, false);

            setupViews(convertView, viewHolder);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        setResultOfBalls(dataModel, viewHolder);

        // Return the completed view to render on screen
        return convertView;
    }

    private void setResultOfBalls(DrawResultModel dataModel, ViewHolder viewHolder) {
        viewHolder.tvDate.setText(dataModel.getDate());

        viewHolder.tvball1.setText(dataModel.getResultOfBallByIndex(0));
        viewHolder.tvball2.setText(dataModel.getResultOfBallByIndex(1));
        viewHolder.tvball3.setText(dataModel.getResultOfBallByIndex(2));
        viewHolder.tvball4.setText(dataModel.getResultOfBallByIndex(3));
        viewHolder.tvball5.setText(dataModel.getResultOfBallByIndex(4));
        viewHolder.tvball6.setText(dataModel.getResultOfBallByIndex(5));
    }

    private void setupViews(View convertView, ViewHolder viewHolder) {
        viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);

        viewHolder.tvball1 = (TextView) convertView.findViewById(R.id.tv_ball_1);
        viewHolder.tvball2 = (TextView) convertView.findViewById(R.id.tv_ball_2);
        viewHolder.tvball3 = (TextView) convertView.findViewById(R.id.tv_ball_3);
        viewHolder.tvball4 = (TextView) convertView.findViewById(R.id.tv_ball_4);
        viewHolder.tvball5 = (TextView) convertView.findViewById(R.id.tv_ball_5);
        viewHolder.tvball6 = (TextView) convertView.findViewById(R.id.tv_ball_6);
    }
}