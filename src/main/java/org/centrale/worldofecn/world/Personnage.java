/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn.world;

import java.sql.Connection;

/**
 *
 * @author ECN
 */
public abstract class Personnage extends Creature {

    /**
     *
     * @param world
     */
    public Personnage(World world) {
        super(world);
    }

//    public Personnage() {
//        super();
//    }
}
