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

import org.centrale.worldofecn.world.*;

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
        // 使用jouerID, nomPartie等去 sauvegarde 中找，如果有相同的，说明是覆盖式存储
        String sqlFindSauvegarde = "select * from sauvgarde where nom_sauvgarde = ? and nom_partie = ? and jourer_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlFindSauvegarde);
        prstmt.setString(1, nomSauvegarde);
        prstmt.setString(2,nomPartie);
        prstmt.setInt(3, idJoueur);
        ResultSet rs = prstmt.executeQuery();
        // 如果有相同的，利用monde_id, 将当前monde中的相关参数存入数据库中
        if(rs.next()){
            monde.setMonde_id(rs.getInt(5));
            rs.close();
            prstmt.close();
            String sqlUpdateMonde = "Update monde\n" +
                    "set width=?,height=?\n" +
                    "where monde_id=?";
            prstmt = connection.prepareStatement(sqlUpdateMonde);
            prstmt.setInt(1, monde.getWidth());
            prstmt.setInt(2, monde.getHeight());
            prstmt.setInt(3, monde.getMonde_id());
            prstmt.executeUpdate();
        }
        // 没有相同的，说明需要新建存档，且将当前的monde在表格monde中给存档. 顺序为先建立monde的存档，然后读取monde_id放入 sauvegarde中存储
        else{
            rs.close();
            prstmt.close();
            // save to table Monde
            String sqlInsertMonde = "insert into monde (nom,width,height,jouer_id)\n" +
                    "values (?,?,?,?) ";
            prstmt = connection.prepareStatement(sqlInsertMonde,Statement.RETURN_GENERATED_KEYS);
            prstmt.setString(1, nomPartie);
            prstmt.setInt(2, monde.getWidth());
            prstmt.setInt(3, monde.getHeight());
            prstmt.setInt(4, idJoueur);
            prstmt.executeUpdate();
            rs = prstmt.getGeneratedKeys();
            rs.next();
            int mondeId = rs.getInt(1);
            monde.setMonde_id(mondeId);

            // save to table sauvagarde
            prstmt.close();
            rs.close();
            String sqlInsertSauvgarde = "Insert into sauvgarde (nom_sauvgarde,nom_partie,jourer_id,monde_id)\n" +
                    "values(?,?,?,?)";
            prstmt = connection.prepareStatement(sqlInsertSauvgarde);
            prstmt.setString(1, nomSauvegarde);
            prstmt.setString(2, nomPartie);
            prstmt.setInt(3, idJoueur);
            prstmt.setInt(4, monde.getMonde_id());
            prstmt.executeUpdate();
        }

        // 接下来向各个类别中存入数据
        for (ElementDeJeu ele : monde.getListElements()){
            ele.saveToDatabase(connection, monde.getMonde_id());
        }

        // Renew personnage_id in table jouer
        String sqlRewPersonnageIdINjouer = "Update joueur set personnage_id = ? where jouer_id = ?";
        prstmt = connection.prepareStatement(sqlRewPersonnageIdINjouer);
        prstmt.setInt(1, monde.getPlayer().getPersonnage().getId());
        prstmt.setInt(2, idJoueur);
        prstmt.executeUpdate();
        prstmt.close();

        // Renew object in jouer's sac
        // Because we don't know which object is new into sac, so we need to delete then add all
        String sqlDeleteSac = "delete  from sac where jouer_id = ?";
        prstmt = connection.prepareStatement(sqlDeleteSac);
        prstmt.setInt(1, idJoueur);
        prstmt.executeUpdate();
        prstmt.close();

        // for object in sac, save to table sac
        for (Objet obj : monde.getPlayer().getSac()){
            String sqlRenewSac = "Insert into sac (jouer_id,object_id) values (?,?)";
            prstmt = connection.prepareStatement(sqlRenewSac);
            prstmt.setInt(1, idJoueur);
            prstmt.setInt(2, obj.getId());
            prstmt.executeUpdate();
            prstmt.close();
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
        // 使用jouerID, nomPartie等去 sauvegarde 中找。      有相同的，就读取，没有相同的，则报错。
        String sqlFindSauvegarde = "select * from sauvgarde where nom_sauvgarde = ? and nom_partie = ? and jourer_id = ?";
        PreparedStatement prstmt = connection.prepareStatement(sqlFindSauvegarde);
        prstmt.setString(1, nomSauvegarde);
        prstmt.setString(2,nomPartie);
        prstmt.setInt(3, idJoueur);
        ResultSet rs = prstmt.executeQuery();
        // 如果有相同的，利用monde_id, 将当前monde中的相关参数存入数据库中
        if(rs.next()){
            monde.setMonde_id(rs.getInt(5));
            monde.getPlayer().setJoueur_id(rs.getInt(4));
            rs.close();
            prstmt.close();

        }
        monde.getFromDatabase(connection, nomPartie, nomSauvegarde,idJoueur);

        // set personnage for player
        String sqlPersonJouer = "select * from joueur where jouer_id = ?";
        prstmt = connection.prepareStatement(sqlPersonJouer);
        prstmt.setInt(1, monde.getPlayer().getJoueur_id());
        rs = prstmt.executeQuery();
        rs.next();
        int personId = rs.getInt(10);
        for (ElementDeJeu ele : monde.getListElements()){
            if(ele instanceof Personnage){
                if(((Personnage) ele).getId() == personId){
                    monde.getPlayer().setPersonnage((Personnage) ele);
                }
            }
        }


        // set sac for player
        String sqlGetSac = "select * from sac where jouer_id = ?";
        prstmt = connection.prepareStatement(sqlGetSac);
        prstmt.setInt(1, idJoueur);
        rs = prstmt.executeQuery();
        while (rs.next()){
            int idObjectSac = rs.getInt(2);
            for (ElementDeJeu ele : monde.getListElements()){
                if(ele instanceof Objet){
                    if(((Objet) ele).getId() == idObjectSac){
                        monde.getPlayer().setSac(ele);
                    }
                }
            }
        }




    }
}
