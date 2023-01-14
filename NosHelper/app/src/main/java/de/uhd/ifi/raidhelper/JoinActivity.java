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




    }

    private void show(Player p1){
        Toast.makeText(this, p1.getName(), Toast.LENGTH_SHORT).show();
    }

}