/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn.world;

import java.sql.*;

/**
 *
 * @author ECN
 */
public class Paysan extends Personnage {

    private int cultureDuRiz;


    public int getCultureDuRiz() {
        return cultureDuRiz;
    }

    public void setCultureDuRiz(int cultureDuRiz) {
        this.cultureDuRiz = cultureDuRiz;
    }

    /**
     *
     * @param world
     */
    public Paysan(World world) {
        super(world);
        cultureDuRiz = 2;
    }
    
    /**
     *
     * @param connection
     */
    @Override
    public void saveToDatabase(Connection connection, int monde_id) throws SQLException {
        super.saveToDatabase(connection,monde_id);

        // In super, we will renew the creature's id
        String sqlCheckExist = "select * from creature_skill where skill_id = ? and cre_id = ?";
        PreparedStatement prstmt= connection.prepareStatement(sqlCheckExist,ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        prstmt.setInt(1, 3);
        prstmt.setInt(2, this.getId());
        ResultSet rs = prstmt.executeQuery();
        if(!rs.next()){                 // haven't been insert into creature_skill, so we insert a new record
            String sqlInsertCreatureSkill = "insert into creature_skill (skill_id,cre_id,value)\n" +
                    "values(3,?,?)";
            prstmt = connection.prepareStatement(sqlInsertCreatureSkill);
            prstmt.setInt(1, this.getId());
            prstmt.setInt(2, cultureDuRiz);
            prstmt.executeUpdate();
        } else{                        // renew value of exist skill
            String sqlUpdateCreatureSkill = "Update creature_skill\n" +      // renew skill 1
                    "set value = ?\n" +
                    "where cre_id = ? and skill_id = 3;";
            prstmt = connection.prepareStatement(sqlUpdateCreatureSkill);
            prstmt.setInt(2, this.getId());
            prstmt.setInt(1, cultureDuRiz);
            prstmt.executeUpdate();

        }
    }

    /**
     *
     * @param connection
     * @param id
     */
    @Override
    public void getFromDatabase(Connection connection, Integer id) throws SQLException {
        super.getFromDatabase(connection,id);
        String sqlGetSkill = "select * from creature_skill where cre_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlGetSkill);
        prstmt.setInt(1, id);
        ResultSet rs = prstmt.executeQuery();
        if (rs.next()){
            cultureDuRiz = rs.getInt(3);
        }
    }
}
