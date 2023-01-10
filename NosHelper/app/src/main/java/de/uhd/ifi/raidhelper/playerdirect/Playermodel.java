package de.uhd.ifi.raidhelper.playerdirect;

abstract public class Playermodel {
    String name;
    String champion_lvl;
    String klasse; //Key for Fabric method
    String dmg;
    public String getDmg() {
        return dmg;
    }

    public void setDmg(String dmg) {
        this.dmg = dmg;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChampion_lvl() {
        return champion_lvl;
    }

    public void setChampion_lvl(String champion_lvl) {
        this.champion_lvl = champion_lvl;
    }

    public String getKlasse() {
        return klasse;
    }

    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }


    String show(){
        return this.champion_lvl + " " + this.name;
    }




}
