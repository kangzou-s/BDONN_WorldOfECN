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
 * @author kwyhr
 */
public class PotionSoin extends Objet {
    private int pdRecouvrer;

    public int getPdRecouvrer() {
        return pdRecouvrer;
    }

    public void setPdRecouvrer(int pdRecouvrer) {
        this.pdRecouvrer = pdRecouvrer;
    }

    /**
     *
     */
    public PotionSoin(World world) {
        super(world);
        pdRecouvrer = 5;
    }

    /**
     *
     * @param connection
     */
    @Override
    public void saveToDatabase(Connection connection, int monde_id) throws SQLException {
        super.saveToDatabase(connection,monde_id);
        // In super, we will renew the object's id
        String sqlCheckExist = "select * from object_skill where obj_skill_id = ? and obj_id = ?";
        PreparedStatement prstmt= connection.prepareStatement(sqlCheckExist,ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        prstmt.setInt(1, 1);
        prstmt.setInt(2, this.getId());
        ResultSet rs = prstmt.executeQuery();
        if(!rs.next()){                 // haven't been insert into creature_skill, so we insert a new record
            String sqlInsertCreatureSkill = "insert into object_skill (obj_skill_id,obj_id,value)\n" +
                    "values(1,?,?)";
            prstmt = connection.prepareStatement(sqlInsertCreatureSkill);
            prstmt.setInt(1, this.getId());
            prstmt.setInt(2, pdRecouvrer);
            prstmt.executeUpdate();
        } else{                        // renew value of exist skill
            String sqlUpdateCreatureSkill = "Update object_skill\n" +      // renew skill 1
                    "set value = ?\n" +
                    "where obj_id = ? and obj_skill_id = 1;";
            prstmt = connection.prepareStatement(sqlUpdateCreatureSkill);
            prstmt.setInt(2, this.getId());
            prstmt.setInt(1, pdRecouvrer);
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
        super.getFromDatabase(connection, id);
        String sqlGetSkill = "select * from object_skill where obj_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlGetSkill);
        prstmt.setInt(1, id);
        ResultSet rs = prstmt.executeQuery();
        if (rs.next()){
            pdRecouvrer = rs.getInt(3);
        }
    }
}
