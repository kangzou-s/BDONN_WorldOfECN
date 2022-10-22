/* --------------------------------------------------------------------------------
 * ECN Tools
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */

package org.centrale.worldofecn;

import org.centrale.worldofecn.world.Creature;
import org.centrale.worldofecn.world.World;

import java.sql.SQLException;

/**
 *
 * @author ECN
 */
public class WorldOfECN {

    /**
     * main program
     * @param args
     */
    public static void main(String[] args) throws SQLException {
        World world = new World();
//        world.setPlayer("Saegusa");
//        world.getListElements().clear();
        
        // Test phase
        DatabaseTools database = new DatabaseTools();

        // Save world
        database.connect();
        Integer playerId = database.getPlayerID("Saegusa", "Mayumi");


//        database.saveWorld(playerId, "Test Game 1", "Start", world);
        
        // Retreive World
        database.readWorld(playerId, "Test Game 1", "Start", world);
        database.saveWorld(playerId, "Test Game 1", "Start", world);
        database.disconnect();
    }
}
