package de.uhd.ifi.raidhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NextActivity extends AppCompatActivity {

    ImageView a8;
    EditText text;
    ArrayList<Player> load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        a8 = (ImageView) findViewById(R.id.a8);
        text = (EditText) findViewById(R.id.edittest);
        loadData();

        Button buttonsave = findViewById(R.id.save);
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player p1 = new Player(text.getText().toString());
                load.add(p1);
                saveData();
            }
        });

        a8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   show();
            }
        });
    }
    private void saveData() {
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
        }
    }
    private void show(){
        if(!load.isEmpty()){
            Toast.makeText(NextActivity.this,load.get(0).name,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(NextActivity.this,"fail",Toast.LENGTH_SHORT).show();

        }
    }


}