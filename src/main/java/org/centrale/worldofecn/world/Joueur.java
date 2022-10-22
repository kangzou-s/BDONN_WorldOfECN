/* --------------------------------------------------------------------------------
 * WoE
 * 
 * Ecole Centrale Nantes - Septembre 2022
 * Equipe pédagogique Informatique et Mathématiques
 * JY Martin
 * -------------------------------------------------------------------------------- */
package org.centrale.worldofecn.world;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ECN
 */
public class Joueur {

    private  Integer joueur_id;
    private String nom;
    private String login;
    private String password;
    private Personnage personnage;

    private List<Objet> sac;
    
    /**
     *
     * @param nom
     */
    public Joueur(String nom) {
        this(nom, null, null);
        joueur_id = -1;
        this.sac = new LinkedList<>();
    }

    /**
     *
     * @param nom
     * @param login
     * @param password
     */
    public Joueur(String nom, String login, String password) {
        this.nom = nom;
        this.login = login;
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getNom() {
        return nom;
    }

    /**
     *
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     *
     * @return
     */
    public String getLogin() {
        return login;
    }

    /**
     *
     * @param login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public Personnage getPersonnage() {
        return personnage;
    }

    /**
     *
     * @param personnage
     */
    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }

    public List<Objet> getSac() {
        return sac;
    }

    public void setSac(List<Objet> sac) {
        this.sac = sac;
    }

    public void setSac(Object obj){
        this.sac.add((Objet) obj);
    }

    public Integer getJoueur_id() {
        return joueur_id;
    }

    public void setJoueur_id(Integer joueur_id) {
        this.joueur_id = joueur_id;
    }
}
