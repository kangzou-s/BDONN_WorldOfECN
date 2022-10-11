/* --------------------------------------------------------------------------------
 * WoE Tools
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.centrale.worldofecn.world.Archer;
import org.centrale.worldofecn.world.ElementDeJeu;
import org.centrale.worldofecn.world.Guerrier;
import org.centrale.worldofecn.world.World;

/**
 *
 * @author ECN
 */
public class DatabaseTools {

    private String login;
    private String password;
    private String url;
    private Connection connection;

    /**
     * Load infos
     */
    public DatabaseTools() {
        try {
            // Get Properties file
            ResourceBundle properties = ResourceBundle.getBundle(DatabaseTools.class.getPackage().getName() + ".database");

            // USE config parameters
            login = properties.getString("login");
            password = properties.getString("password");
            String server = properties.getString("server");
            String database = properties.getString("database");
            url = "jdbc:postgresql://" + server + "/" + database;

            // Mount driver
            Driver driver = DriverManager.getDriver(url);
            if (driver == null) {
                Class.forName("org.postgresql.Driver");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.connection = null;
    }

    /**
     * Get connection to the database
     */
    public void connect() {
        if (this.connection == null) {
            try {
                this.connection = DriverManager.getConnection(url, login, password);
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Disconnect from database
     */
    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * get Player ID
     * @param nomJoueur
     * @param password
     * @return
     */
    public  Integer getPlayerID(String nomJoueur, String password) throws SQLException {

        String sql = "select * from joueur\n" +
                "where login = ?" +
                " and password = ?;";
        PreparedStatement prstmt = connection.prepareStatement(sql);
        prstmt.setString(1, nomJoueur);
        prstmt.setString(2, password);
        ResultSet rs = prstmt.executeQuery();
        if (!rs.next()){
            System.out.println("There is not such player Or incorrect login or password ");
            return null;
        }

        if(!rs.wasNull()){
            int id = rs.getInt(1);
            return id;
        }

        rs.close();
        prstmt.close();
        return null;
    }

    /**
     * save world to database
     * @param idJoueur
     * @param nomPartie
     * @param nomSauvegarde
     * @param monde
     */
    public void saveWorld(Integer idJoueur, String nomPartie, String nomSauvegarde, World monde) throws SQLException {
        // 判断数据库中是否存在
        String sqlFindMonde = "select * from monde\n" +
                "where monde_id = ?\n" +
                "and jouer_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlFindMonde);
        prstmt.setInt(1, monde.getMonde_id());
        prstmt.setInt(2, idJoueur);
        ResultSet rs = prstmt.executeQuery();
        // 数据库中不存在，在monde中存入
        if (!rs.next()){
            String sqlInsertMonde = "insert into monde (nom,width,height,jouer_id)\n" +
                    "values (?,?,?,?)";
            prstmt = connection.prepareStatement(sqlInsertMonde);
            prstmt.setString(1, nomPartie);
            prstmt.setInt(2, monde.getWidth());
            prstmt.setInt(3, monde.getHeight());
            prstmt.setInt(4, idJoueur);
            prstmt.executeUpdate();
            System.out.println("There is not such word part, we have insert one ");
        }
        //数据库中存在，先修改monde本身
        else{
            String sqlUpdateMonde = "Update monde\n" +
                    "set width=?,height=?\n" +
                    "where monde_id=?";
            prstmt = connection.prepareStatement(sqlUpdateMonde);
            prstmt.setInt(1, monde.getWidth());
            prstmt.setInt(2, monde.getHeight());
            prstmt.setInt(3, monde.getMonde_id());
            prstmt.executeUpdate();
            System.out.println("It's un update, we continue to update the objects in the world");
        }

        // 接下来向各个类别中存入数据
        // 向表'sauvgarde'中存入数据
        World.saveToDatabase(connection, nomPartie, nomSauvegarde,idJoueur);
//        sql = "select id from monde\n" +
//                "where nom = '" + "" + nomPartie + "'";
//        rs = stmt.executeQuery(sql);
//        if (rs.next()){
//            monde_id = rs.getInt(1);
//        }
//        // 向各个类别的生物中存入
        for (ElementDeJeu ele : monde.getListElements()){
            ele.saveToDatabase(connection, monde.getMonde_id());
        }


    }

    /**
     * get world from database
     * @param idJoueur
     * @param nomPartie
     * @param nomSauvegarde
     * @param monde
     */
    public void readWorld(Integer idJoueur, String nomPartie, String nomSauvegarde, World monde) throws SQLException {
        monde.getFromDatabase(connection, nomPartie, nomSauvegarde);
    }
}
