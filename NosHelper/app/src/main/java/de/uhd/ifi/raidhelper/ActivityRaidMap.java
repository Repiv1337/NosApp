package de.uhd.ifi.raidhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import de.uhd.ifi.raidhelper.playerdirect.Player;

public class ActivityRaidMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raid_map);
        Intent intent = getIntent();
        ArrayList<Player> load;
        load = (ArrayList<Player>) intent.getSerializableExtra("toraid");
        TextView textView = (TextView) findViewById(R.id.textraid);
        textView.setText(load.get(load.size()-1).getName());
    }
}