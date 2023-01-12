package de.uhd.ifi.raidhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import de.uhd.ifi.raidhelper.playerdirect.Player;

public class JoinActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Intent intent = getIntent();
        ArrayList<Player> load;
        load = (ArrayList<Player>) intent.getSerializableExtra("test");
        String text = load.get(load.size()-1).getName();
        Button button = (Button) findViewById(R.id.showbutt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(load.get(load.size()-1));
            }
        });

        TextView textView = (TextView) findViewById(R.id.texttest);
        TextView textView1 = (TextView) findViewById(R.id.texttest2);
        textView.setText(text);
        textView1.setText(load.get(load.size()-1).getChampion_lvl());
    }

    private void show(Player p1){
        Toast.makeText(this, p1.getName(), Toast.LENGTH_SHORT).show();
    }

}