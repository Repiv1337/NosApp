package de.uhd.ifi.raidhelper.playerdirect;

public class Player {
    String klasse;
    String name;
    String level;
    String dmg;

    public String getKlasse() {
        return klasse;
    }

    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDmg() {
        return dmg;
    }

    public void setDmg(String dmg) {
        this.dmg = dmg;
    }

    public Player(String klasse, String name, String level, String dmg){
        this.klasse = klasse;
        this.name = name;
        this.level = level;
        this.dmg = dmg;
    }


}
