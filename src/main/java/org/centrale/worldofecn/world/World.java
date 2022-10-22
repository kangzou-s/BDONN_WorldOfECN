/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn.world;

import org.centrale.worldofecn.DatabaseTools;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ECN
 */
public class World {

    private static final int MAXPEOPLE = 20;
    private static final int MAXMONSTERS = 10;
    private static final int MAXOBJECTS = 20;

    private Integer monde_id;
    private Integer width;
    private Integer height;

    private List<ElementDeJeu> listElements;
    private Joueur player;

    /**
     * Default constructor
     */
    public World() {
        this(20, 20);
        monde_id = -1;
    }

    /**
     * Constructor for specific world size
     *
     * @param width : world width
     * @param height : world height
     */
    public World(int width, int height) {
        this.setHeightWidth(height, width);
        init();
        generate();
        // because we haven't written the code of pick object, chose person. So here we distribute by default
        if(listElements.size() >3){
            this.player.setPersonnage((Personnage) listElements.get(0));
            this.player.setSac(listElements.get(listElements.size() -1));
            this.player.setSac(listElements.get(listElements.size() -2));
        }
    }

    /**
     * Initialize elements
     */
    private void init() {
        this.listElements = new LinkedList();
        this.player = new Joueur("Player");
    }


    public Integer getMonde_id() {
        return monde_id;
    }

    public void setMonde_id(Integer monde_id) {
        this.monde_id = monde_id;
    }

    /**
     *
     * @return
     */
    public Integer getWidth() {
        return width;
    }

    /**
     *
     * @param width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     *
     * @return
     */
    public Integer getHeight() {
        return height;
    }

    /**
     *
     * @param height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     *
     * @param height
     * @param width
     */
    public final void setHeightWidth(Integer height, Integer width) {
        this.setHeight(height);
        this.setWidth(width);
    }

    public Joueur getPlayer() {
        return player;
    }

    /**
     * Check element can be created
     *
     * @param element
     * @return
     */
    private ElementDeJeu check(ElementDeJeu element) {
        return element;
    }

    /**
     * Generate personnages
     */
    private void generatePersonnages(int nbElements) {
        Random rand = new Random();
        for (int i = 0; i < nbElements; i++) {
            int itemType = rand.nextInt(3);
            Personnage item = null;
            while (item == null) {
                switch (itemType) {
                    case 0: // Guerrier
                        item = new Guerrier(this);
                        break;
                    case 1: // Archer
                        item = new Archer(this);
                        break;
                    case 2: // Paysan
                        item = new Paysan(this);
                        break;
                }
                item = (Personnage) check(item);
            }
            // Add to list
            this.listElements.add(item);
        }
    }

    /**
     * Generate Monsters
     */
    private void generateMonsters(int nbElements) {
        Random rand = new Random();

        // Generate monsters
        for (int i = 0; i < nbElements; i++) {
            int itemType = rand.nextInt(2);
            Monstre item = null;
            while (item == null) {
                switch (itemType) {
                    case 0: // Lapin
                        item = new Lapin(this);
                        break;
                    case 1: // Loup
                        item = new Loup(this);
                        break;
                }
                item = (Monstre) check(item);
            }
            // Add to list
            this.listElements.add(item);
        }
    }

    /**
     * Generate Objects
     */
    private void generateObjects(int nbElements) {
        Random rand = new Random();

        // Generate objects
        for (int i = 0; i < nbElements; i++) {
            int itemType = rand.nextInt(1);
            Objet item = null;
            while (item == null) {
                switch (itemType) {
                    case 0: // Potion de soin
                        item = new PotionSoin(this);
                        break;
                    case 1: // Arme
                        item = new PotionSoin(this);
                        break;
                }
                item = (Objet) check(item);
            }
            // Add to list
            this.listElements.add(item);
        }
    }

    /**
     * Generate Player
     */
    private void generatePlayer(int itemType) {
        Personnage item = null;
        while (item == null) {
            switch (itemType) {
                case 0: // Guerrier
                    item = new Guerrier(this);
                    break;
                case 1: // Archer
                    item = new Archer(this);
                    break;
                case 2: // Paysan
                    item = new Paysan(this);
                    break;
            }
            item = (Personnage) check(item);
        }
        // Add to list
        this.listElements.add(item);
    }

    /**
     * Generate elements randomly
     */
    private void generate() {
        Random rand = new Random();

        generatePlayer(1);

        this.generatePersonnages(rand.nextInt(MAXPEOPLE));
        this.generateMonsters(rand.nextInt(MAXMONSTERS));
        this.generateObjects(rand.nextInt(MAXOBJECTS));
    }

    /**
     * Set Player name
     *
     * @param name
     */
    public void setPlayer(String name) {
        this.player.setNom(name);
    }


    public List<ElementDeJeu> getListElements() {
        return listElements;
    }

    /**
     * Save world to database
     *
     * @param connection
     * @param gameName
     * @param saveName
     */
    public static void  saveToDatabase(Connection connection, String gameName, String saveName, int playerId) throws SQLException {
//        if (connection != null) {
//            // Get Player ID
//
//            // Save world for Player ID
//            String sqlInsertSauvgarde = "Insert into sauvgarde (nom_sauvgarde,nom_partie,jourer_id)\n" +
//                    "values(?,?,?)";
//            PreparedStatement prstmt = connection.prepareStatement(sqlInsertSauvgarde);
//            prstmt.setString(1, saveName);
//            prstmt.setString(2, gameName);
//            prstmt.setInt(3, playerId);
//            prstmt.executeUpdate();
//        }
    }

    /**
     * Get world from database
     *
     * @param connection
     * @param gameName
     * @param saveName
     */
    public void getFromDatabase(Connection connection, String gameName, String saveName,Integer idJoueur) throws SQLException {
        if (connection != null) {
            // Remove old data
            this.setHeightWidth(0, 0);
            init();
            this.getPlayer().setJoueur_id(idJoueur);

            // 把 monde最外层的参数设置好
            String sqlGetMonde = "select * from monde \n" +
                    "where monde_id = ?";
            PreparedStatement prstmt = connection.prepareStatement(sqlGetMonde);
            prstmt.setInt(1, this.getMonde_id());
            ResultSet rs = prstmt.executeQuery();
            rs.next();
            this.setWidth(rs.getInt(3));
            this.setHeight(rs.getInt(4));

            // Get Player ID
               // have done in DatabaseTool


            // get world for Player ID
            //把玩家参数设置好
            String sqlGetPlayer = "select * from joueur \n" +
                    "where jouer_id = ?";
            prstmt = connection.prepareStatement(sqlGetPlayer);
            prstmt.setInt(1, this.getPlayer().getJoueur_id());
            rs = prstmt.executeQuery();
            rs.next();
            this.getPlayer().setNom(rs.getString(2));
            this.getPlayer().setLogin(rs.getString(3));
            this.getPlayer().setPassword(rs.getString(4));

            //对具体的每个对象进行设置
            // 从monde 中取出所有creature
            String sqlGetCreature = "select * from creature\n" +
                    "where monde_id = ?";
            prstmt = connection.prepareStatement(sqlGetCreature);
            prstmt.setInt(1, monde_id);
            rs = prstmt.executeQuery();
            Creature crea;
            Objet obj;
            while (rs.next()){
                int type_id = rs.getInt(2);
                int cre_id = rs.getInt(1);
                switch (type_id){
                    case 0:      //Archer
                        crea = new Archer(this);
                        crea.getFromDatabase(connection,cre_id);
                        this.getListElements().add(crea);
                        break;
                    case 1: // Guerrier
                        crea = new Guerrier(this);
                        crea.getFromDatabase(connection, cre_id);
                        this.getListElements().add(crea);
                        break;
                    case 2: // Lapin
                        crea = new Lapin(this);
                        crea.getFromDatabase(connection, cre_id);
                        this.getListElements().add(crea);
                        break;
                    case 3:  //Loup
                        crea = new Loup(this);
                        crea.getFromDatabase(connection, cre_id);
                        this.getListElements().add(crea);
                        break;
                    case 4:  // Paysan
                        crea = new Paysan(this);
                        crea.getFromDatabase(connection, cre_id);
                        this.getListElements().add(crea);
                        break;
                }
            }
            // select out all object from table object
            String sqlGetObject ="select * from object\n" +
                    "where monde_id = ?";
            prstmt = connection.prepareStatement(sqlGetObject,ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            prstmt.setInt(1,monde_id);
            rs = prstmt.executeQuery();
            while (rs.next()){
                int type_id = rs.getInt(2);
                int object_id = rs.getInt(1);
                switch (type_id) {
                    case 0:      //PotionSoin
                        obj = new PotionSoin(this);
                        obj.getFromDatabase(connection, object_id);
                        this.getListElements().add(obj);
                        break;
                }
            }
        }
    }
}
