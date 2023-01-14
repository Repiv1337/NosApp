package de.uhd.ifi.raidhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.uhd.ifi.raidhelper.playerdirect.Player;

public class ActivityRaidMap extends AppCompatActivity {
    ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raid_map);
        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
        Raidrecyclerviewadapter raidrecyclerviewadapter = new Raidrecyclerviewadapter(this);
        recyclerView.setAdapter(raidrecyclerviewadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        delete = (ImageView) findViewById(R.id.imagedelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextActivity.load.remove(0);
                raidrecyclerviewadapter.notifyDataSetChanged();
                NextActivity.changecounter++;
                Intent inte = new Intent(ActivityRaidMap.this,NextActivity.class);
                startActivity(inte);
            }
        });

    }


}