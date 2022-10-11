/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */

package org.centrale.worldofecn.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author kwyhr
 */
public abstract class Objet extends ElementDeJeu{

    private int id;
    private int ptvie;
    /**
     *
     * @param world
     */
    public Objet(World world) {
        super(world);
        id = -1;
        ptvie = 100;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPtvie() {
        return ptvie;
    }

    public void setPtvie(int ptvie) {
        this.ptvie = ptvie;
    }


    public void saveToDatabase(Connection connection, int monde_id) throws SQLException {
        Integer obj_type_id = -1;
        if (this instanceof PotionSoin) {
            obj_type_id = 0;
        }
        if (this.getId() == -1) {                             //is the first time to save
            String sqlInsertCreature = "insert into object (obj_type_id,monde_id,coordinate_x,coordinate_y,point_de_vie)\n" +
                    "values(?,?,?,?,?)";
            PreparedStatement prstmt = connection.prepareStatement(sqlInsertCreature);
            prstmt.setInt(1, obj_type_id);
            prstmt.setInt(2, monde_id);
            prstmt.setInt(3, this.getPosition().getX());
            prstmt.setInt(4, this.getPosition().getY());
            prstmt.setInt(5, this.getPtvie());
            prstmt.executeUpdate();
        }else {
            String sqlInsertCreature = "update object\n" +
                    " set coordinate_x = ?, coordinate_y =?, point_de_vie =?\n" +
                    " where object_id = ?";
            PreparedStatement prstmt = connection.prepareStatement(sqlInsertCreature);
            prstmt.setInt(1, this.getPosition().getX());
            prstmt.setInt(2, this.getPosition().getY());
            prstmt.setInt(3, this.getPtvie());
            prstmt.setInt(4, this.id);
            prstmt.executeUpdate();
        }
    }

    public void getFromDatabase(Connection connection, Integer id) throws SQLException {
        String sqlGetCreature = "select * from object\n" +
                "where object_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlGetCreature);
        prstmt.setInt(1,id);
        ResultSet rs = prstmt.executeQuery();
        if(rs.next()){
            this.setId(rs.getInt(1));
            this.getPosition().setX(rs.getInt(4));
            this.getPosition().setY(rs.getInt(5));
            this.setPtvie(rs.getInt(6));
        }
    }

}


