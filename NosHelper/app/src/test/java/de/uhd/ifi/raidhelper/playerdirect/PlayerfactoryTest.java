package de.uhd.ifi.raidhelper.playerdirect;

import junit.framework.TestCase;

public class PlayerfactoryTest extends TestCase {

    public void testGetPlayer() {

        Playermodel mage = Playerfactory.getPlayer("Mage","90","Mage","100");
        System.out.println(mage.getChampion_lvl());
    }
}