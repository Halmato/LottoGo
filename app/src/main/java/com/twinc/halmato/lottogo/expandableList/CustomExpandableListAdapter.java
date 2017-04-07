package com.twinc.halmato.lottogo.expandableList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinc.halmato.lottogo.R;
import com.twinc.halmato.lottogo.model.Pick;

import static android.R.attr.data;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    // Constructor
    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }


    public void addPickToCurrentPicks(String pick) {
        expandableListDetail.get("Current").add(pick);
    }
    public void movePickFromCurrentPickToOldPicks(String pick) {
        for (String s:expandableListDetail.get("Current")) {
            if(s.equals(pick)) {
                expandableListDetail.get("Old").add(s);
                expandableListDetail.get("Current").remove(s);
                return;
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.picks_list_item, null);
        }

        setupViews(convertView,new ViewHolder());

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.pick_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }



    // View lookup cache
    private static class ViewHolder {

        RelativeLayout[] relativeLayoutsOfBalls = new RelativeLayout[6];

        TextView tvDate;

        TextView tvBall1;
        TextView tvBall2;
        TextView tvBall3;
        TextView tvBall4;
        TextView tvBall5;
        TextView tvBall6;
    }

    private void setupViews(View convertView, ViewHolder viewHolder) {

        viewHolder.relativeLayoutsOfBalls[0] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_1);
        viewHolder.relativeLayoutsOfBalls[1] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_2);
        viewHolder.relativeLayoutsOfBalls[2] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_3);
        viewHolder.relativeLayoutsOfBalls[3] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_4);
        viewHolder.relativeLayoutsOfBalls[4] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_5);
        viewHolder.relativeLayoutsOfBalls[5] = (RelativeLayout) convertView.findViewById(R.id.rl_ball_6);

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
                    Toast.makeText(context, "Clicked on ball with number: " + tvNumber.getText().toString(), Toast.LENGTH_SHORT).show();
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

}