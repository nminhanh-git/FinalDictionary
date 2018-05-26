package com.app.mightylion.finaldictionary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Handler;

/**
 * Created by nminh on 12/2/2017.
 */


public class WordListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> list = new ArrayList<>();

    public WordListAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.word_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(getItem(position));
        return convertView;
    }
}
