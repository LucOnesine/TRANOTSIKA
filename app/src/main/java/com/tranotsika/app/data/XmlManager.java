package com.tranotsika.app.data;

import android.content.Context;
import android.util.Log;

import com.tranotsika.app.model.Maison;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import org.xml.sax.InputSource;

/**
 * Gestionnaire XML utilisant DocumentBuilderFactory et DOM Parser.
 * Gère la persistance locale dans le stockage interne (tranotsika_data.xml et favoris.xml).
 */
public class XmlManager {

    private static final String TAG = "XmlManager";
    public static final String FICHIER_DATA = "tranotsika_data.xml";
    public static final String FICHIER_FAVORIS = "favoris.xml";

    /**
     * Initialise les données par défaut si le fichier tranotsika_data.xml n'existe pas encore.
     */
    public static synchronized void initialiserDonneesSiNecessaire(Context context) {
        File file = new File(context.getFilesDir(), FICHIER_DATA);
        if (!file.exists()) {
            Log.d(TAG, "Création du fichier XML par défaut : " + FICHIER_DATA);
            List<Maison> initialList = new ArrayList<>();

            // Données fidèles à la maquette
            initialList.add(new Maison(
                    "m1",
                    "Duplex Moderne Riviera",
                    "Duplex",
                    "250 000 Ar/mois",
                    "Cocody Riviera 3, Abidjan",
                    "Abidjan, Côte d'Ivoire",
                    3,
                    2,
                    140,
                    true,
                    "Ce magnifique duplex meublé, situé en plein cœur de la Riviera 3, offre des prestations haut de gamme avec une cuisine équipée, un grand salon lumineux et une sécurité 24h/24. Idéal pour une famille ou un cadre dynamique.",
                    "house_sample_1",
                    false,
                    "Amadou Koné",
                    "Agent Certifié Fediko",
                    "+225 07 01 02 03 04"
            ));

            initialList.add(new Maison(
                    "m2",
                    "Villa avec Piscine Almadies",
                    "Villa",
                    "250 000 Ar/mois",
                    "Cocody Riviera 3, Abidjan",
                    "Abidjan, Côte d'Ivoire",
                    4,
                    3,
                    220,
                    true,
                    "Superbe villa d'architecte avec piscine privée, vaste terrasse aménagée et jardin tropical. Calme absolu, garage double et système de vidéosurveillance moderne.",
                    "house_sample_2",
                    false,
                    "Amadou Koné",
                    "Agent Certifié Fediko",
                    "+225 07 01 02 03 04"
            ));

            initialList.add(new Maison(
                    "m3",
                    "Appartement Standing Plateau",
                    "Appartement",
                    "180 000 Ar/mois",
                    "Plateau Centre, Abidjan",
                    "Abidjan, Côte d'Ivoire",
                    2,
                    1,
                    85,
                    false,
                    "Appartement lumineux avec vue dégagée, idéal pour jeune actif ou couple. Immeuble sécurisé avec ascenseur et parking sous-terrain.",
                    "house_sample_1",
                    false,
                    "Sarah Diallo",
                    "Conseillère Immobilière",
                    "+225 05 10 20 30 40"
            ));

            initialList.add(new Maison(
                    "m4",
                    "Studio Cosy Ankorondrano",
                    "Studio",
                    "95 000 Ar/mois",
                    "Ankorondrano, Antananarivo",
                    "Antananarivo, Madagascar",
                    1,
                    1,
                    38,
                    true,
                    "Studio moderne tout équipé situé à proximité directe des centres d'affaires et commerciaux. Équipé d'une connexion fibre et d'un groupe électrogène.",
                    "house_sample_2",
                    false,
                    "Andry Rakoto",
                    "Agent Partenaire",
                    "+261 34 12 345 67"
            ));

            sauvegarderToutesMaisons(context, initialList);
        }
    }

    /**
     * Lit la liste des maisons depuis tranotsika_data.xml via DOM Parser.
     */
    public static synchronized List<Maison> chargerMaisons(Context context) {
        initialiserDonneesSiNecessaire(context);
        List<Maison> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FICHIER_DATA);

        if (!file.exists()) {
            return list;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("maison");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Maison m = convertirElementEnMaison(element);
                    if (m != null) {
                        list.add(m);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture du fichier XML : " + e.getMessage(), e);
        }

        return list;
    }

    /**
     * Bascule ou définit l'état favori d'un logement dans tranotsika_data.xml
     * et met à jour le fichier favoris.xml.
     */
    public static synchronized boolean setFavori(Context context, String maisonId, boolean estFavori) {
        File file = new File(context.getFilesDir(), FICHIER_DATA);
        if (!file.exists()) {
            return false;
        }

        boolean modifie = false;
        try (FileInputStream fis = new FileInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fis);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("maison");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = getTagValue("id", element);
                    if (id != null && id.equals(maisonId)) {
                        NodeList favNodeList = element.getElementsByTagName("estFavori");
                        if (favNodeList.getLength() > 0) {
                            favNodeList.item(0).setTextContent(String.valueOf(estFavori));
                        } else {
                            Element newFavNode = doc.createElement("estFavori");
                            newFavNode.setTextContent(String.valueOf(estFavori));
                            element.appendChild(newFavNode);
                        }
                        modifie = true;
                        break;
                    }
                }
            }

            if (modifie) {
                // Écriture du Document modifié dans tranotsika_data.xml
                ecrireDocumentDansFichier(doc, file);
                // Mettre à jour le fichier dédié favoris.xml
                mettreAJourFichierFavoris(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur modification favori XML : " + e.getMessage(), e);
        }
        return modifie;
    }

    /**
     * Charge uniquement la liste des maisons marquées comme favorites.
     */
    public static synchronized List<Maison> chargerFavoris(Context context) {
        List<Maison> tous = chargerMaisons(context);
        List<Maison> favoris = new ArrayList<>();
        for (Maison m : tous) {
            if (m.isEstFavori()) {
                favoris.add(m);
            }
        }
        return favoris;
    }

    /**
     * Enregistre ou met à jour une maison dans le fichier XML local.
     */
    public static synchronized void sauvegarderMaison(Context context, Maison nouvelleMaison) {
        List<Maison> list = chargerMaisons(context);
        boolean existe = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(nouvelleMaison.getId())) {
                list.set(i, nouvelleMaison);
                existe = true;
                break;
            }
        }
        if (!existe) {
            list.add(nouvelleMaison);
        }
        sauvegarderToutesMaisons(context, list);
    }

    /**
     * Sauvegarde une liste complète de maisons dans tranotsika_data.xml via DOM Parser / Transformer.
     */
    public static synchronized void sauvegarderToutesMaisons(Context context, List<Maison> maisons) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("tranotsika");
            doc.appendChild(rootElement);

            for (Maison m : maisons) {
                Element maisonElem = doc.createElement("maison");

                ajouterElement(doc, maisonElem, "id", m.getId());
                ajouterElement(doc, maisonElem, "titre", m.getTitre());
                ajouterElement(doc, maisonElem, "type", m.getType());
                ajouterElement(doc, maisonElem, "prix", m.getPrix());
                ajouterElement(doc, maisonElem, "adresse", m.getAdresse());
                ajouterElement(doc, maisonElem, "ville", m.getVille());
                ajouterElement(doc, maisonElem, "chambres", String.valueOf(m.getChambres()));
                ajouterElement(doc, maisonElem, "sallesDeBain", String.valueOf(m.getSallesDeBain()));
                ajouterElement(doc, maisonElem, "superficie", String.valueOf(m.getSuperficie()));
                ajouterElement(doc, maisonElem, "meuble", String.valueOf(m.isMeuble()));
                ajouterElement(doc, maisonElem, "description", m.getDescription());
                ajouterElement(doc, maisonElem, "imageNom", m.getImageNom());
                ajouterElement(doc, maisonElem, "estFavori", String.valueOf(m.isEstFavori()));
                ajouterElement(doc, maisonElem, "nomAgent", m.getNomAgent());
                ajouterElement(doc, maisonElem, "roleAgent", m.getRoleAgent());
                ajouterElement(doc, maisonElem, "telephoneAgent", m.getTelephoneAgent());

                rootElement.appendChild(maisonElem);
            }

            File file = new File(context.getFilesDir(), FICHIER_DATA);
            ecrireDocumentDansFichier(doc, file);
            mettreAJourFichierFavoris(context);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la sauvegarde XML : " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le fichier dédié favoris.xml avec les IDs et détails des favoris.
     */
    private static void mettreAJourFichierFavoris(Context context) {
        try {
            List<Maison> maisons = chargerMaisons(context);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("favoris");
            doc.appendChild(root);

            for (Maison m : maisons) {
                if (m.isEstFavori()) {
                    Element fav = doc.createElement("favori");
                    ajouterElement(doc, fav, "id", m.getId());
                    ajouterElement(doc, fav, "titre", m.getTitre());
                    ajouterElement(doc, fav, "prix", m.getPrix());
                    root.appendChild(fav);
                }
            }

            File fileFav = new File(context.getFilesDir(), FICHIER_FAVORIS);
            ecrireDocumentDansFichier(doc, fileFav);

        } catch (Exception e) {
            Log.e(TAG, "Erreur mise à jour favoris.xml : " + e.getMessage(), e);
        }
    }

    /**
     * Écrit un Document XML DOM dans un fichier avec indentation propre.
     */
    private static void ecrireDocumentDansFichier(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        }
    }

    /**
     * Convertit une chaîne XML brute reçue par exemple via Socket en liste de Maison.
     */
    public static List<Maison> parserXmlString(String xmlString) {
        List<Maison> list = new ArrayList<>();
        if (xmlString == null || xmlString.trim().isEmpty()) {
            return list;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("maison");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Maison m = convertirElementEnMaison((Element) node);
                    if (m != null) {
                        list.add(m);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur parsing XML string : " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Exporte une liste de maisons vers une chaîne de caractères XML (pratique pour Socket).
     */
    public static String exporterVersXmlString(List<Maison> list) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("tranotsika");
            doc.appendChild(root);

            for (Maison m : list) {
                Element mElem = doc.createElement("maison");
                ajouterElement(doc, mElem, "id", m.getId());
                ajouterElement(doc, mElem, "titre", m.getTitre());
                ajouterElement(doc, mElem, "type", m.getType());
                ajouterElement(doc, mElem, "prix", m.getPrix());
                ajouterElement(doc, mElem, "adresse", m.getAdresse());
                ajouterElement(doc, mElem, "ville", m.getVille());
                ajouterElement(doc, mElem, "chambres", String.valueOf(m.getChambres()));
                ajouterElement(doc, mElem, "sallesDeBain", String.valueOf(m.getSallesDeBain()));
                ajouterElement(doc, mElem, "superficie", String.valueOf(m.getSuperficie()));
                ajouterElement(doc, mElem, "meuble", String.valueOf(m.isMeuble()));
                ajouterElement(doc, mElem, "description", m.getDescription());
                ajouterElement(doc, mElem, "imageNom", m.getImageNom());
                ajouterElement(doc, mElem, "estFavori", String.valueOf(m.isEstFavori()));
                ajouterElement(doc, mElem, "nomAgent", m.getNomAgent());
                ajouterElement(doc, mElem, "roleAgent", m.getRoleAgent());
                ajouterElement(doc, mElem, "telephoneAgent", m.getTelephoneAgent());
                root.appendChild(mElem);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            Log.e(TAG, "Erreur génération XML string : " + e.getMessage(), e);
            return "";
        }
    }

    private static Maison convertirElementEnMaison(Element element) {
        String id = getTagValue("id", element);
        String titre = getTagValue("titre", element);
        String type = getTagValue("type", element);
        String prix = getTagValue("prix", element);
        String adresse = getTagValue("adresse", element);
        String ville = getTagValue("ville", element);
        int chambres = parseIntSafe(getTagValue("chambres", element), 1);
        int sdb = parseIntSafe(getTagValue("sallesDeBain", element), 1);
        int superficie = parseIntSafe(getTagValue("superficie", element), 50);
        boolean meuble = Boolean.parseBoolean(getTagValue("meuble", element));
        String description = getTagValue("description", element);
        String imageNom = getTagValue("imageNom", element);
        boolean estFavori = Boolean.parseBoolean(getTagValue("estFavori", element));
        String nomAgent = getTagValue("nomAgent", element);
        String roleAgent = getTagValue("roleAgent", element);
        String telAgent = getTagValue("telephoneAgent", element);

        return new Maison(id, titre, type, prix, adresse, ville, chambres, sdb, 
                superficie, meuble, description, imageNom, estFavori, nomAgent, roleAgent, telAgent);
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
}
