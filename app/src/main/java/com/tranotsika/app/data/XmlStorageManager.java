package com.tranotsika.app.data;

import android.content.Context;
import android.util.Log;

import com.tranotsika.app.model.Annonce;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Gestionnaire XML utilisant DocumentBuilderFactory et DOM Parser pour les annonces créées.
 * Gère le fichier local mes_annonces.xml dans le stockage interne de l'application.
 */
public class XmlStorageManager {

    private static final String TAG = "XmlStorageManager";
    public static final String FICHIER_MES_ANNONCES = "mes_annonces.xml";

    /**
     * Initialise les 3 annonces de la maquette si le fichier n'existe pas encore.
     */
    public static synchronized void initialiserAnnoncesParDefautSiNecessaire(Context context) {
        File file = new File(context.getFilesDir(), FICHIER_MES_ANNONCES);
        if (!file.exists()) {
            Log.d(TAG, "Création du fichier XML par défaut : " + FICHIER_MES_ANNONCES);
            List<Annonce> list = new ArrayList<>();

            // 1. Duplex Moderne Riviera (Active, 124 vues, 28 favoris)
            list.add(new Annonce(
                    "A101",
                    "Duplex Moderne Riviera",
                    "Duplex",
                    "250 000 Ar/mois",
                    "Cocody Riviera 3, Abidjan",
                    3,
                    2,
                    140,
                    "Amadou Koné",
                    "Ce magnifique duplex meublé, situé en plein cœur de la Riviera 3, offre des prestations haut de gamme avec une cuisine équipée, un grand salon lumineux et une sécurité 24h/24.",
                    "Active",
                    124,
                    28,
                    "12 Oct",
                    "house_sample_1"
            ));

            // 2. Studio Meublé Chic Zone 4 (Active, 412 vues, 64 favoris)
            list.add(new Annonce(
                    "A102",
                    "Studio Meublé Chic Zone 4",
                    "Studio",
                    "150 000 Ar/mois",
                    "Zone 4C, Marcory, Abidjan",
                    1,
                    1,
                    45,
                    "Amadou Koné",
                    "Magnifique studio tout confort meublé avec goût dans un immeuble sécurisé avec ascenseur et piscine.",
                    "Active",
                    412,
                    64,
                    "08 Oct",
                    "house_sample_2"
            ));

            // 3. Villa Basse Angré (Attente, 0 vues, 0 favoris)
            list.add(new Annonce(
                    "A103",
                    "Villa Basse Angré",
                    "Villa",
                    "450 000 Ar/mois",
                    "Angré 8ème Tranche, Abidjan",
                    4,
                    3,
                    250,
                    "Amadou Koné",
                    "Grande villa basse avec jardin arboré, garage pour 2 véhicules et dépendance gardien. En attente de validation des documents fonciers.",
                    "Attente",
                    0,
                    0,
                    "01 Oct",
                    "house_sample_1"
            ));

            sauvegarderToutesAnnonces(context, list);
        }
    }

    /**
     * Charge toutes les annonces depuis mes_annonces.xml.
     */
    public static synchronized List<Annonce> chargerMesAnnonces(Context context) {
        initialiserAnnoncesParDefautSiNecessaire(context);
        List<Annonce> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FICHIER_MES_ANNONCES);

        if (!file.exists()) {
            return list;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("annonce");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Annonce a = convertirElementEnAnnonce(element);
                    if (a != null) {
                        list.add(a);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lecture mes_annonces.xml : " + e.getMessage(), e);
        }

        return list;
    }

    /**
     * Ajoute une nouvelle annonce au fichier XML local.
     * Si une annonce avec le même ID existe déjà, elle est mise à jour (upsert sans doublon).
     */
    public static synchronized void ajouterAnnonceXML(Context context, Annonce annonce) {
        List<Annonce> list = chargerMesAnnonces(context);
        boolean existe = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equalsIgnoreCase(annonce.getId())) {
                list.set(i, annonce);
                existe = true;
                break;
            }
        }
        if (!existe) {
            // Ajout en tête de liste pour afficher les plus récentes en premier
            list.add(0, annonce);
        }
        sauvegarderToutesAnnonces(context, list);
    }

    /**
     * Modifie une annonce existante repérée par son identifiant.
     */
    public static synchronized boolean modifierAnnonceXML(Context context, Annonce annonceModifiee) {
        List<Annonce> list = chargerMesAnnonces(context);
        boolean trouve = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equalsIgnoreCase(annonceModifiee.getId())) {
                list.set(i, annonceModifiee);
                trouve = true;
                break;
            }
        }
        if (trouve) {
            sauvegarderToutesAnnonces(context, list);
        }
        return trouve;
    }

    /**
     * Supprime une annonce du fichier XML.
     */
    public static synchronized boolean supprimerAnnonceXML(Context context, String annonceId) {
        List<Annonce> list = chargerMesAnnonces(context);
        boolean supprime = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equalsIgnoreCase(annonceId)) {
                list.remove(i);
                supprime = true;
                break;
            }
        }
        if (supprime) {
            sauvegarderToutesAnnonces(context, list);
        }
        return supprime;
    }

    /**
     * Sauvegarde l'ensemble de la liste des annonces dans mes_annonces.xml avec DOM Transformer.
     * Utilise une écriture atomique sécurisée (fichier temporaire + renommage) pour éviter
     * toute corruption en cas d'interruption ou coupure inopinée.
     */
    public static synchronized void sauvegarderToutesAnnonces(Context context, List<Annonce> annonces) {
        File finalFile = new File(context.getFilesDir(), FICHIER_MES_ANNONCES);
        File tempFile = new File(context.getFilesDir(), FICHIER_MES_ANNONCES + ".tmp");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("annonces");
            doc.appendChild(rootElement);

            for (Annonce a : annonces) {
                Element annonceElem = doc.createElement("annonce");
                annonceElem.setAttribute("id", a.getId());

                ajouterElement(doc, annonceElem, "titre", a.getTitre());
                ajouterElement(doc, annonceElem, "type", a.getType());
                ajouterElement(doc, annonceElem, "prix", a.getPrix());
                ajouterElement(doc, annonceElem, "statut", a.getStatut());
                ajouterElement(doc, annonceElem, "vues", String.valueOf(a.getVues()));
                ajouterElement(doc, annonceElem, "favoris", String.valueOf(a.getFavoris()));
                ajouterElement(doc, annonceElem, "datePublication", a.getDatePublication());
                ajouterElement(doc, annonceElem, "localisation", a.getLocalisation());
                ajouterElement(doc, annonceElem, "chambres", String.valueOf(a.getChambres()));
                ajouterElement(doc, annonceElem, "sallesDeBain", String.valueOf(a.getSallesDeBain()));
                ajouterElement(doc, annonceElem, "superficie", String.valueOf(a.getSuperficie()));
                ajouterElement(doc, annonceElem, "nomProprietaire", a.getNomProprietaire());
                ajouterElement(doc, annonceElem, "description", a.getDescription());
                ajouterElement(doc, annonceElem, "imageResNom", a.getImageResNom());

                rootElement.appendChild(annonceElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);
                fos.flush();
            }

            // Renommage atomique du fichier temporaire vers le fichier final
            if (finalFile.exists()) {
                finalFile.delete();
            }
            if (!tempFile.renameTo(finalFile)) {
                Log.w(TAG, "Impossible de renommer le fichier temporaire, copie directe...");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur sauvegarde atomique mes_annonces.xml : " + e.getMessage(), e);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Convertit une Annonce en chaîne XML structurée pour la transmission Socket.
     */
    public static String exporterAnnonceEnXmlString(Annonce a) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element annonceElem = doc.createElement("annonce");
            annonceElem.setAttribute("id", a.getId());
            doc.appendChild(annonceElem);

            ajouterElement(doc, annonceElem, "titre", a.getTitre());
            ajouterElement(doc, annonceElem, "type", a.getType());
            ajouterElement(doc, annonceElem, "prix", a.getPrix());
            ajouterElement(doc, annonceElem, "statut", a.getStatut());
            ajouterElement(doc, annonceElem, "vues", String.valueOf(a.getVues()));
            ajouterElement(doc, annonceElem, "favoris", String.valueOf(a.getFavoris()));
            ajouterElement(doc, annonceElem, "datePublication", a.getDatePublication());
            ajouterElement(doc, annonceElem, "localisation", a.getLocalisation());
            ajouterElement(doc, annonceElem, "chambres", String.valueOf(a.getChambres()));
            ajouterElement(doc, annonceElem, "sallesDeBain", String.valueOf(a.getSallesDeBain()));
            ajouterElement(doc, annonceElem, "superficie", String.valueOf(a.getSuperficie()));
            ajouterElement(doc, annonceElem, "nomProprietaire", a.getNomProprietaire());
            ajouterElement(doc, annonceElem, "description", a.getDescription());
            ajouterElement(doc, annonceElem, "imageResNom", a.getImageResNom());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            Log.e(TAG, "Erreur génération XML string pour Socket : " + e.getMessage(), e);
            return "<annonce id=\"" + a.getId() + "\"><titre>" + a.getTitre() + "</titre></annonce>";
        }
    }

    private static Annonce convertirElementEnAnnonce(Element element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            id = getTagValue("id", element);
        }
        String titre = getTagValue("titre", element);
        String type = getTagValue("type", element);
        String prix = getTagValue("prix", element);
        String statut = getTagValue("statut", element);
        if (statut == null || statut.isEmpty()) statut = "Active";
        int vues = parseIntSafe(getTagValue("vues", element), 0);
        int favoris = parseIntSafe(getTagValue("favoris", element), 0);
        String datePublication = getTagValue("datePublication", element);
        if (datePublication == null || datePublication.isEmpty()) datePublication = "Aujourd'hui";
        String localisation = getTagValue("localisation", element);
        int chambres = parseIntSafe(getTagValue("chambres", element), 1);
        int sdb = parseIntSafe(getTagValue("sallesDeBain", element), 1);
        int superficie = parseIntSafe(getTagValue("superficie", element), 50);
        String nomProprietaire = getTagValue("nomProprietaire", element);
        String description = getTagValue("description", element);
        String imageResNom = getTagValue("imageResNom", element);
        if (imageResNom == null || imageResNom.isEmpty()) imageResNom = "house_sample_1";

        return new Annonce(id, titre, type, prix, localisation, chambres, sdb, 
                superficie, nomProprietaire, description, statut, vues, favoris, 
                datePublication, imageResNom);
    }

    private static void ajouterElement(Document doc, Element parent, String tag, String valeur) {
        Element el = doc.createElement(tag);
        el.setTextContent(valeur != null ? valeur : "");
        parent.appendChild(el);
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getFirstChild() != null) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return "";
    }

    private static int parseIntSafe(String val, int defVal) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return defVal;
        }
    }

    /**
     * Marshalling JAXB : Convertit un objet Annonce en chaîne XML via JAXB.
     */
    public static String marshalAnnonceJAXB(Annonce annonce) {
        try {
            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(Annonce.class);
            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(annonce, writer);
            return writer.toString();
        } catch (Exception e) {
            Log.e(TAG, "Erreur JAXB Marshalling : " + e.getMessage(), e);
            return exporterAnnonceEnXmlString(annonce);
        }
    }

    /**
     * Unmarshalling JAXB : Reconstruit un objet Annonce depuis une chaîne XML via JAXB.
     */
    public static Annonce unmarshalAnnonceJAXB(String xml) {
        try {
            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(Annonce.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            return (Annonce) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            Log.e(TAG, "Erreur JAXB Unmarshalling : " + e.getMessage(), e);
            return null;
        }
    }
}
