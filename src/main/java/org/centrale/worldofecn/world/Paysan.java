/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn.world;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ECN
 */
public class Paysan extends Personnage {
    
    /**
     *
     * @param world
     */
    public Paysan(World world) {
        super(world);
    }
    
    /**
     *
     * @param connection
     */
    @Override
    public void saveToDatabase(Connection connection, int monde_id) throws SQLException {
        super.saveToDatabase(connection,monde_id);
    }

    /**
     *
     * @param connection
     * @param id
     */
    @Override
    public void getFromDatabase(Connection connection, Integer id) throws SQLException {
        super.getFromDatabase(connection,id);
    }
}
