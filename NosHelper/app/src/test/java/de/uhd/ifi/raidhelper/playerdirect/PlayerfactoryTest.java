package de.uhd.ifi.raidhelper.playerdirect;

import junit.framework.TestCase;

public class PlayerfactoryTest extends TestCase {

    public void testGetPlayer() {

        Playermodel p1 = Playerfactory.getPlayer("Buggy","90","Mage","100");
        System.out.println(p1.getDmg());
    }
}