package com.tranotsika.app.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Modèle POJO représentant une annonce immobilière créée et suivie par l'utilisateur.
 * Annoté avec JAXB (@XmlRootElement, @XmlElement, @XmlAttribute) pour le marshalling / unmarshalling XML et SOAP.
 */
@XmlRootElement(name = "annonce")
@XmlAccessorType(XmlAccessType.FIELD)
public class Annonce implements Serializable {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "titre")
    private String titre;

    @XmlElement(name = "type")
    private String type; // Duplex, Studio, Villa, Appartement

    @XmlElement(name = "prix")
    private String prix; // ex: "250 000 Ar/mois" ou "250000"

    @XmlElement(name = "localisation")
    private String localisation; // ex: "Cocody Riviera 3, Abidjan"

    @XmlElement(name = "chambres")
    private int chambres;

    @XmlElement(name = "sallesDeBain")
    private int sallesDeBain;

    @XmlElement(name = "superficie")
    private int superficie; // en m²

    @XmlElement(name = "nomProprietaire")
    private String nomProprietaire;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "statut")
    private String statut; // "Active", "Attente"

    @XmlElement(name = "vues")
    private int vues; // compteur de vues

    @XmlElement(name = "favoris")
    private int favoris; // compteur de favoris reçus

    @XmlElement(name = "datePublication")
    private String datePublication; // ex: "12 Oct"

    @XmlElement(name = "imageResNom")
    private String imageResNom; // ex: "house_sample_1", "house_sample_2"

    public Annonce() {
        this.statut = "Active";
        this.vues = 0;
        this.favoris = 0;
        this.datePublication = "Aujourd'hui";
        this.imageResNom = "house_sample_1";
    }

    public Annonce(String id, String titre, String type, String prix, String localisation, 
                   int chambres, int sallesDeBain, int superficie, String nomProprietaire, 
                   String description, String statut, int vues, int favoris, 
                   String datePublication, String imageResNom) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.prix = prix;
        this.localisation = localisation;
        this.chambres = chambres;
        this.sallesDeBain = sallesDeBain;
        this.superficie = superficie;
        this.nomProprietaire = nomProprietaire;
        this.description = description;
        this.statut = statut;
        this.vues = vues;
        this.favoris = favoris;
        this.datePublication = datePublication;
        this.imageResNom = imageResNom;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public int getChambres() {
        return chambres;
    }

    public void setChambres(int chambres) {
        this.chambres = chambres;
    }

    public int getSallesDeBain() {
        return sallesDeBain;
    }

    public void setSallesDeBain(int sallesDeBain) {
        this.sallesDeBain = sallesDeBain;
    }

    public int getSuperficie() {
        return superficie;
    }

    public void setSuperficie(int superficie) {
        this.superficie = superficie;
    }

    public String getNomProprietaire() {
        return nomProprietaire;
    }

    public void setNomProprietaire(String nomProprietaire) {
        this.nomProprietaire = nomProprietaire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getVues() {
        return vues;
    }

    public void setVues(int vues) {
        this.vues = vues;
    }

    public int getFavoris() {
        return favoris;
    }

    public void setFavoris(int favoris) {
        this.favoris = favoris;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        this.datePublication = datePublication;
    }

    public String getImageResNom() {
        return imageResNom;
    }

    public void setImageResNom(String imageResNom) {
        this.imageResNom = imageResNom;
    }

    public String getPrixAffiche() {
        if (prix == null) return "0 Ar/mois";
        if (prix.toLowerCase().contains("ar") || prix.toLowerCase().contains("fcfa")) {
            return prix;
        }
        return prix + " Ar/mois";
    }
}
