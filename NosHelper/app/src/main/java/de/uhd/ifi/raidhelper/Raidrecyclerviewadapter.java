package de.uhd.ifi.raidhelper;

import android.content.Context;
import android.media.TimedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.uhd.ifi.raidhelper.playerdirect.Player;

public class Raidrecyclerviewadapter extends RecyclerView.Adapter<Raidrecyclerviewadapter.MyViewHolder> {
    ArrayList<Player> player;
    public  Raidrecyclerviewadapter(Context context){
        this.context = context;
        player = NextActivity.load;
        }


    Context context;
    @NonNull
    @Override
    public Raidrecyclerviewadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row,parent,false);

        return new Raidrecyclerviewadapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Raidrecyclerviewadapter.MyViewHolder holder, int position) {
    holder.membername.setText(player.get(position).getName());
    holder.memberlvl.setText(player.get(position).getChampion_lvl());
    if(player.get(position).getKlasse().equalsIgnoreCase("Mage")){
        holder.Klasse.setImageResource(R.drawable.mage_icon);
    }
        if(player.get(position).getKlasse().equalsIgnoreCase("Sword")){
            holder.Klasse.setImageResource(R.drawable.mage_icon);
        }
        if(player.get(position).getKlasse().equalsIgnoreCase("Bogi")){
            holder.Klasse.setImageResource(R.drawable.bogi_icon);
        }
    }

    @Override
    public int getItemCount() {
        return player.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView membername;
        TextView memberlvl;
        ImageView Klasse;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            membername = itemView.findViewById(R.id.membername);
            memberlvl = itemView.findViewById(R.id.memberlvl);
            Klasse = itemView.findViewById(R.id.classimage);

        }
    }
}
