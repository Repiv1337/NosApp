package de.uhd.ifi.raidhelper.playerdirect;

import java.io.Serializable;

public class Player extends Playermodel implements Serializable {
    public Player(String klasse, String name, String lvl, String dmg) {
        this.klasse = klasse;
        this.name=name;
        this.champion_lvl=lvl;
        this.dmg=dmg;

    }


    public String getKlasse() {
        return this.klasse;
    }


    public void setKlasse(String klasse) {

    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {

    }


    public String getChampion_lvl() {
        return this.champion_lvl;
    }


    public void setChampion_lvl(String champion_lvl) {

    }


    public void setDmg(String dmg) {

    }


    public String getDmg() {
        return this.dmg+100;
    }

    public String getLvl(){
        return this.champion_lvl;
    }



}
