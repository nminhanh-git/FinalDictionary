package com.app.mightylion.finaldictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private ListView infoListView;
    private ArrayList<Info> infoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent infoIntent = getIntent();
        infoListView = (ListView) findViewById(R.id.infoListView);
        infoItem = new ArrayList<>();
        infoItem.add(new Info(R.string.application_version_title, R.string.application_version_number));
        infoItem.add(new Info(R.string.about_us_title, R.string.about_us_subheading));
        InfoAdapter adapter = new InfoAdapter(InfoActivity.this, R.layout.info_row_layout, infoItem);
        infoListView.setAdapter(adapter);
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (infoItem.get(i).getMainInfo() == R.string.application_version_title) {
                    Toast.makeText(InfoActivity.this, R.string.application_version_number, Toast.LENGTH_SHORT).show();
                }
                if (infoItem.get(i).getMainInfo() == R.string.about_us_title) {
                    Intent teamInfoIntent = new Intent(InfoActivity.this, TeamInfoActivity.class);
                    InfoActivity.this.startActivity(teamInfoIntent);
                }
            }
        });
    }
}
