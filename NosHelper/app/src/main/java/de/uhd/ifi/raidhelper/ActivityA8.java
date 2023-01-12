package de.uhd.ifi.raidhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import de.uhd.ifi.raidhelper.playerdirect.Player;

public class ActivityA8 extends AppCompatActivity {
    ImageView alza;
    ImageView a6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a8);
        Intent intent = getIntent();
        ArrayList<Player> load;
        load = (ArrayList<Player>) intent.getSerializableExtra("toa8");
        alza = (ImageView) findViewById(R.id.a8);
        alza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityA8.this,ActivityRaidMap.class);
                intent.putExtra("toraid",load);
                startActivity(intent);
            }
        });

    }




}