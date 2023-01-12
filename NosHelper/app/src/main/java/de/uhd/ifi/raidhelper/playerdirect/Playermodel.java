package de.uhd.ifi.raidhelper.playerdirect;

import java.io.Serializable;

abstract public class Playermodel implements Serializable {
    String klasse;
    String name;
    String champion_lvl;
    String dmg;
    abstract public String getKlasse();

    abstract public void setKlasse(String klasse);

    abstract public String getName();

    abstract public void setName(String name);

    abstract public String getChampion_lvl();

    abstract public void setChampion_lvl(String champion_lvl);
    abstract public void setDmg(String dmg) ;
   abstract public String getDmg();



    String show(){
        return this.champion_lvl + " " + this.name;
    }




}
