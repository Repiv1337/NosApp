package de.uhd.ifi.raidhelper;
import java.io.Serializable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import de.uhd.ifi.raidhelper.playerdirect.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Stack;

import de.uhd.ifi.raidhelper.playerdirect.Playermodel;

public class NextActivity extends AppCompatActivity implements Serializable{

    public static int changecounter = 0;
    EditText text;
    EditText leveltext;
    public static ArrayList<Player> load;
    ImageView a8;
    ImageView swordi;
    ImageView mage;
    ImageView bogi;
    ImageView classholder = null;
    String classchoice = "You must chooste a class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        if(changecounter!=0){
            load.remove(1);
            saveData();
        }
        classholder = (ImageView) findViewById(R.id.classholder);
        text = (EditText) findViewById(R.id.edittest);
        leveltext = (EditText) findViewById(R.id.textchampionlvl);
        swordi = (ImageView) findViewById(R.id.swordi);
        bogi = (ImageView) findViewById(R.id.bogi);
        mage = (ImageView) findViewById(R.id.mage);
        loadData();
        bogi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classholder.setImageResource(R.drawable.bogi_icon);
                classchoice = "Bogi";
            }
        });
        swordi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classholder.setImageResource(R.drawable.sword_icon);
                classchoice = "Swordi";
            }
        });
        mage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classholder.setImageResource(R.drawable.mage_icon);
                classchoice ="Mage";
            }
        });
        Button buttonsave = findViewById(R.id.save);
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check()) {
                    pustoList();
                    saveData();
                    Intent intent2 = new Intent(NextActivity.this, ActivityA8.class);
                    startActivity(intent2);
                }
                else{
                    show();
                }
            }
        });
        Button buttonjoin = findViewById(R.id.Join);
        buttonjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check()) {
                    pustoList();
                    saveData();
                    Intent intent = new Intent(NextActivity.this, JoinActivity.class);
                    startActivity(intent);
                }
                else{
                    show();
                }
            }
        });


    }
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(load);
        editor.putString("task list", json);
        editor.apply();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        load= gson.fromJson(json, type);
        if (load == null) {
            load = new ArrayList<>();
            return;
        }

        text.setText(load.get(load.size()-1).getName());
        leveltext.setText(load.get(load.size()-1).getChampion_lvl());


    }

    private void pustoList(){
        if(text!=null && leveltext!=null){

            load.add( new Player(classchoice,text.getText().toString(),leveltext.getText().toString(),"100"));
        }
    }
    public void show(){
        Toast.makeText(this,"Bitte wähle Name und Level",Toast.LENGTH_SHORT).show();
    }

    private boolean check(){
        if(this.text.getText().toString().equals("") || this.leveltext.getText().toString().equals("")){
            return true;
        }
        return false;
    }



}