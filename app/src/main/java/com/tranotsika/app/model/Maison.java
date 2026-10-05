package com.tranotsika.app.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Modèle de données représentant une maison / un logement dans l'application Tranotsika.
 * Annoté JAXB pour la conformité de sérialisation XML / SOAP.
 */
@XmlRootElement(name = "maison")
@XmlAccessorType(XmlAccessType.FIELD)
public class Maison implements Serializable {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "titre")
    private String titre;

    @XmlElement(name = "type")
    private String type; // Appartement, Villa, Studio, Duplex

    @XmlElement(name = "prix")
    private String prix; // ex: "250 000 Ar/mois" ou "250k AR/m"

    @XmlElement(name = "adresse")
    private String adresse; // ex: "Cocody Riviera 3, Abidjan"

    @XmlElement(name = "ville")
    private String ville; // ex: "Abidjan, Côte d'Ivoire"

    @XmlElement(name = "chambres")
    private int chambres; // ex: 3

    @XmlElement(name = "sallesDeBain")
    private int sallesDeBain; // ex: 2

    @XmlElement(name = "superficie")
    private int superficie; // en m², ex: 140

    @XmlElement(name = "meuble")
    private boolean meuble; // meublé ou non

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "imageNom")
    private String imageNom; // nom du drawable ou chemin d'image

    @XmlElement(name = "estFavori")
    private boolean estFavori;

    @XmlElement(name = "nomAgent")
    private String nomAgent; // ex: "Amadou Koné"

    @XmlElement(name = "roleAgent")
    private String roleAgent; // ex: "Agent Certifié Fediko"

    @XmlElement(name = "telephoneAgent")
    private String telephoneAgent; // pour le contact

    public Maison() {
    }

    public Maison(String id, String titre, String type, String prix, String adresse, 
                  String ville, int chambres, int sallesDeBain, int superficie, 
                  boolean meuble, String description, String imageNom, boolean estFavori, 
                  String nomAgent, String roleAgent, String telephoneAgent) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.prix = prix;
        this.adresse = adresse;
        this.ville = ville;
        this.chambres = chambres;
        this.sallesDeBain = sallesDeBain;
        this.superficie = superficie;
        this.meuble = meuble;
        this.description = description;
        this.imageNom = imageNom;
        this.estFavori = estFavori;
        this.nomAgent = nomAgent;
        this.roleAgent = roleAgent;
        this.telephoneAgent = telephoneAgent;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
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

    public boolean isMeuble() {
        return meuble;
    }

    public void setMeuble(boolean meuble) {
        this.meuble = meuble;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageNom() {
        return imageNom;
    }

    public void setImageNom(String imageNom) {
        this.imageNom = imageNom;
    }

    public boolean isEstFavori() {
        return estFavori;
    }

    public void setEstFavori(boolean estFavori) {
        this.estFavori = estFavori;
    }

    public String getNomAgent() {
        return nomAgent;
    }

    public void setNomAgent(String nomAgent) {
        this.nomAgent = nomAgent;
    }

    public String getRoleAgent() {
        return roleAgent;
    }

    public void setRoleAgent(String roleAgent) {
        this.roleAgent = roleAgent;
    }

    public String getTelephoneAgent() {
        return telephoneAgent;
    }

    public void setTelephoneAgent(String telephoneAgent) {
        this.telephoneAgent = telephoneAgent;
    }

    /**
     * Retourne une synthèse des caractéristiques comme affiché sur la maquette :
     * ex: "3 Ch. • 2 SdB • 140 m²"
     */
    public String getCaracteristiquesResume() {
        return chambres + " Ch. • " + sallesDeBain + " SdB • " + superficie + " m²";
    }
}
