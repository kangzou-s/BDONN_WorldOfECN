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
public class Archer extends Personnage {

    private int attaque;
    private int distanceAttaque;


    public int getAttaque() {
        return attaque;
    }

    public void setAttaque(int attaque) {
        this.attaque = attaque;
    }

    public int getDistanceAttaque() {
        return distanceAttaque;
    }

    public void setDistanceAttaque(int distanceAttaque) {
        this.distanceAttaque = distanceAttaque;
    }

    /**
     *
     * @param world
     */
    public Archer(World world) {
        super(world);
        attaque = 10;
        distanceAttaque = 10;

    }

//    public Archer() {
//        super();
//    }

    /**
     *
     * @param connection
     * @param monde_id
     */
    @Override
    public  void saveToDatabase(Connection connection,int monde_id) throws SQLException {
        super.saveToDatabase(connection,monde_id);
        // In super, we have renewed the creature's id
        String sqlCheckExist = "select * from creature_skill where skill_id = ? and cre_id = ?";
        PreparedStatement prstmt= connection.prepareStatement(sqlCheckExist,ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        prstmt.setInt(1, 1);
        prstmt.setInt(2, this.getId());
        ResultSet rs = prstmt.executeQuery();
        if(!rs.next()){                 // haven't been insert into creature_skill, so we insert a new record
            String sqlInsertCreatureSkill = "insert into creature_skill (skill_id,cre_id,value)\n" +
                    "values(1,?,?),(2,?,?)";
            prstmt = connection.prepareStatement(sqlInsertCreatureSkill);
            prstmt.setInt(1, this.getId());
            prstmt.setInt(2, attaque);
            prstmt.setInt(3, this.getId());
            prstmt.setInt(4, distanceAttaque);
            prstmt.executeUpdate();
        } else{                        // renew value of exist skill
            String sqlUpdateCreatureSkill = "Update creature_skill\n" +      // renew skill 1
                    "set value = ?\n" +
                    "where cre_id = ? and skill_id = 1;";
            prstmt = connection.prepareStatement(sqlUpdateCreatureSkill);
            prstmt.setInt(2, this.getId());
            prstmt.setInt(1, attaque);
            prstmt.executeUpdate();

            sqlUpdateCreatureSkill = "Update creature_skill\n" +     // renew skill 2
                    "set value = ?\n" +
                    "where cre_id = ? and skill_id = 2;";
            prstmt = connection.prepareStatement(sqlUpdateCreatureSkill);
            prstmt.setInt(2, this.getId());
            prstmt.setInt(1, distanceAttaque);
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
       String sqlGetSkill = "select * from creature_skill where cre_id = ? order by skill_id";
       PreparedStatement prstmt = connection.prepareStatement(sqlGetSkill);
       prstmt.setInt(1, id);
       ResultSet rs = prstmt.executeQuery();
       if (rs.next()){
           attaque = rs.getInt(3);
       }
       if (rs.next()){
           distanceAttaque = rs.getInt(3);
       }
    }
}
