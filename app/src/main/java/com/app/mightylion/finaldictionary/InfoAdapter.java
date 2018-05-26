package com.app.mightylion.finaldictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nminh on 8/18/2017.
 */

public class InfoAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Info> arrayListInfo;

    public InfoAdapter(Context context, int layout, ArrayList<Info> arrayListInfo) {
        this.context = context;
        this.layout = layout;
        this.arrayListInfo = arrayListInfo;
    }

    @Override
    public int getCount() {
        return arrayListInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayListInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        TextView mainInfoTextView = (TextView) view.findViewById(R.id.info_title);
        TextView subInfoTextView = (TextView) view.findViewById(R.id.info_subheading);

        Info info = arrayListInfo.get(i);
        mainInfoTextView.setText(info.getMainInfo());
        subInfoTextView.setText(info.getSubInfo());
        return view;
    }
}
