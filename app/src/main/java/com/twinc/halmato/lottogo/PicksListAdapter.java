package com.twinc.halmato.lottogo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinc.halmato.lottogo.model.Pick;

import java.util.ArrayList;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class PicksListAdapter extends ArrayAdapter<Pick> implements View.OnClickListener{

    private ArrayList<Pick> dataSet;
    Context mContext;

    int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {


        RelativeLayout[] relativeLayoutsOfBalls = new RelativeLayout[6];


        /*RelativeLayout rlBall1;
        RelativeLayout rlBall2;
        RelativeLayout rlBall3;
        RelativeLayout rlBall4;
        RelativeLayout rlBall5;
        RelativeLayout rlBall6;*/

        TextView tvDate;

        TextView tvBall1;
        TextView tvBall2;
        TextView tvBall3;
        TextView tvBall4;
        TextView tvBall5;
        TextView tvBall6;
    }

    // Constructor
    public PicksListAdapter(ArrayList<Pick> data, Context context) {
        super(context, R.layout.picks_list_view_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void add(@Nullable Pick object) {
        super.add(object);

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);

        Pick dataModel=(Pick)object;

        switch (v.getId())
        {
            case R.id.iv_ball_1: // this is the info button in the list item
                Toast.makeText(mContext, "ball 1 clicked", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mContext, "default", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pick dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.picks_list_view_item, parent, false);

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



    private void setResultOfBalls(Pick dataModel, ViewHolder viewHolder) {
        viewHolder.tvDate.setText(dataModel.getDate());

        viewHolder.tvBall1.setText(dataModel.getResultOfBallByIndex(0));
        viewHolder.tvBall2.setText(dataModel.getResultOfBallByIndex(1));
        viewHolder.tvBall3.setText(dataModel.getResultOfBallByIndex(2));
        viewHolder.tvBall4.setText(dataModel.getResultOfBallByIndex(3));
        viewHolder.tvBall5.setText(dataModel.getResultOfBallByIndex(4));
        viewHolder.tvBall6.setText(dataModel.getResultOfBallByIndex(5));
    }

    private void setupViews(View convertView, ViewHolder viewHolder) {


        viewHolder.relativeLayoutsOfBalls[0] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_1);
        viewHolder.relativeLayoutsOfBalls[1] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_2);
        viewHolder.relativeLayoutsOfBalls[2] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_3);
        viewHolder.relativeLayoutsOfBalls[3] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_4);
        viewHolder.relativeLayoutsOfBalls[4] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_5);
        viewHolder.relativeLayoutsOfBalls[5] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_6);

        /*
        viewHolder.rlBall1 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_1);
        viewHolder.rlBall2 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_2);
        viewHolder.rlBall3 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_3);
        viewHolder.rlBall4 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_4);
        viewHolder.rlBall5 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_5);
        viewHolder.rlBall6 = (RelativeLayout) convertView.findViewById(R.id.rl_ball_6);
        */

        viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);

        viewHolder.tvBall1 = (TextView) convertView.findViewById(R.id.tv_ball_1);
        viewHolder.tvBall2 = (TextView) convertView.findViewById(R.id.tv_ball_2);
        viewHolder.tvBall3 = (TextView) convertView.findViewById(R.id.tv_ball_3);
        viewHolder.tvBall4 = (TextView) convertView.findViewById(R.id.tv_ball_4);
        viewHolder.tvBall5 = (TextView) convertView.findViewById(R.id.tv_ball_5);
        viewHolder.tvBall6 = (TextView) convertView.findViewById(R.id.tv_ball_6);

        setOnClickListenerOfBallGrouping(viewHolder.relativeLayoutsOfBalls);
    }



    private void setOnClickListenerOfBallGrouping(RelativeLayout[] rls) {

        for (RelativeLayout rl:rls) {

            rl.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {        //makeAllComponentsInRelativeLayoutClickable(view);

                    TextView tvNumber = findTextViewInViewGroup((ViewGroup) view);
                    Toast.makeText(mContext, "Clicked on ball with number: " + tvNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    TextView findTextViewInViewGroup(ViewGroup vg) {
        TextView tv = null;

        for (int i = 0; i < vg.getChildCount(); i++) {
            View viewChild = vg.getChildAt(i);

            if(viewChild instanceof TextView) {
                tv = (TextView) viewChild;
            }
        }

        return tv;
    }

    void makeAllComponentsInRelativeLayoutClickable(View view) {
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup .getChildCount(); i++) {
            View viewChild = viewGroup .getChildAt(i);
            viewChild.setPressed(true);}
    }





}