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
public abstract class Creature extends ElementDeJeu {

    private int id;
    private int ptvie;
    /**
     *
     * @param world
     */
    public Creature(World world) {
        super(world);
        id = -1;
        ptvie = 100;
    }

//    public Creature() {
//    }

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

    /**
     *
     * @param connection
     */
    @Override
    public void saveToDatabase(Connection connection, int monde_id) throws SQLException {
        Integer cre_type_id = -1;
        if (this instanceof Archer){
            cre_type_id = 0;
        } else if (this instanceof Guerrier) {
            cre_type_id = 1;
        } else if (this instanceof Lapin) {
            cre_type_id = 2;
        } else if (this instanceof Loup) {
            cre_type_id = 3;
        } else if (this instanceof Paysan) {
            cre_type_id = 4;
        }
        if (this.getId() == -1) {                             //is the first time to save
            // store in table creature
            String sqlInsertCreature = "insert into creature (cre_type_id,monde_id,coordinate_x,coordinate_y,point_de_vie)\n" +
                    "values(?,?,?,?,?)";
            PreparedStatement prstmt = connection.prepareStatement(sqlInsertCreature,Statement.RETURN_GENERATED_KEYS);
            prstmt.setInt(1, cre_type_id);
            prstmt.setInt(2, monde_id);
            prstmt.setInt(3, this.getPosition().getX());
            prstmt.setInt(4, this.getPosition().getY());
            prstmt.setInt(5, this.getPtvie());
            prstmt.executeUpdate();
            ResultSet rs = prstmt.getGeneratedKeys();
            rs.next();
            this.setId(rs.getInt(1));
            prstmt.close();
            rs.close();
        } else {              // it's not the first time, so we update information in table creature
            String sqlInsertCreature = "update creature\n" +
                    " set coordinate_x = ?, coordinate_y =?, point_de_vie =?\n" +
                    " where cre_id = ?";
            PreparedStatement prstmt = connection.prepareStatement(sqlInsertCreature);
            prstmt.setInt(1, this.getPosition().getX());
            prstmt.setInt(2, this.getPosition().getY());
            prstmt.setInt(3, this.getPtvie());
            prstmt.setInt(4, this.id);
            prstmt.executeUpdate();
            prstmt.close();
        }
    }

    public void getFromDatabase(Connection connection, Integer id) throws SQLException {
        String sqlGetCreature = "select * from creature\n" +
                "where cre_id = ?";
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




