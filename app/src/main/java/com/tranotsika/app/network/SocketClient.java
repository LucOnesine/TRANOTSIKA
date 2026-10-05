package com.tranotsika.app.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client réseau Socket (java.net.Socket).
 * Exécute systématiquement les communications sur un thread d'arrière-plan via ExecutorService
 * pour éviter NetworkOnMainThreadException.
 * Les callbacks sont postés sur l'UI Thread via Handler.
 */
public class SocketClient {

    private static final String TAG = "SocketClient";
    private static final int TIMEOUT_MS = 6000;

    // Singleton pour réutilisation propre
    private static SocketClient instance;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface SocketResponseCallback {
        void onSuccess(String reponse);
        void onError(Exception exception);
    }

    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    private SocketClient() {
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Envoie une requête générique (JSON ou XML) à un serveur Socket et attend la réponse.
     *
     * @param host Adresse IP ou nom d'hôte du serveur
     * @param port Port TCP
     * @param payload Contenu de la requête (chaîne XML ou JSON)
     * @param callback Callback notifié sur le thread principal
     */
    public void envoyerRequete(final String host, final int port, final String payload, final SocketResponseCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                BufferedReader reader = null;
                PrintWriter writer = null;

                try {
                    Log.d(TAG, "Tentative de connexion Socket vers " + host + ":" + port);
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
                    socket.setSoTimeout(TIMEOUT_MS);

                    // Émission de la requête
                    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    writer.println(payload);
                    writer.flush();

                    // Réception de la réponse
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                        // Condition de fin de trame si flux continu (ex: fin XML, SOAP ou JSON)
                        if (line.contains("</tranotsika>") || 
                            line.contains("</soapenv:Envelope>") || 
                            line.contains("</soap:Envelope>") || 
                            line.contains("</annonce>") || 
                            line.trim().endsWith("}") || 
                            line.trim().equals("EOF")) {
                            break;
                        }
                    }

                    final String reponse = sb.toString().trim();
                    Log.d(TAG, "Réponse Socket reçue (" + reponse.length() + " chars)");

                    // Notification sur l'UI Thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess(reponse);
                            }
                        }
                    });

                } catch (final Exception e) {
                    Log.e(TAG, "Erreur communication Socket : " + e.getMessage(), e);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                } finally {
                    try {
                        if (writer != null) writer.close();
                        if (reader != null) reader.close();
                        if (socket != null && !socket.isClosed()) socket.close();
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    /**
     * Envoie une requête de recherche avec filtres vers le serveur distant.
     * Exemple de format envoyé : JSON {"action":"SEARCH", "keyword":"Riviera", "type":"Duplex"}
     */
    public void rechercherLogements(String host, int port, String motCle, String typeLogement, SocketResponseCallback callback) {
        String jsonPayload = "{\"action\":\"SEARCH\",\"keyword\":\"" + (motCle != null ? motCle : "") 
                + "\",\"type\":\"" + (typeLogement != null ? typeLogement : "") + "\"}";
        envoyerRequete(host, port, jsonPayload, callback);
    }

    /**
     * Synchronise le catalogue local XML avec le serveur distant.
     */
    public void synchroniserCatalogueXml(String host, int port, String catalogueXml, SocketResponseCallback callback) {
        envoyerRequete(host, port, catalogueXml, callback);
    }

    /**
     * Publie une annonce complète au format XML vers le serveur distant par Socket TCP.
     * Envoie la structure XML sur un thread d'arrière-plan via ExecutorService.
     */
    public void publierAnnonceSurServeur(String host, int port, com.tranotsika.app.model.Annonce annonce, SocketResponseCallback callback) {
        String xmlPayload = com.tranotsika.app.data.XmlStorageManager.exporterAnnonceEnXmlString(annonce);
        Log.d(TAG, "Émission de l'annonce XML vers " + host + ":" + port + "\n" + xmlPayload);
        envoyerRequete(host, port, xmlPayload, callback);
    }

    /**
     * Publie une annonce via SOAP / JAXB en enveloppant l'objet Annonce dans une enveloppe SOAP 1.1 / 1.2
     * et en transmettant la requête sur un thread secondaire via ExecutorService.
     */
    public void publierAnnonceSoap(String host, int port, com.tranotsika.app.model.Annonce annonce, SocketResponseCallback callback) {
        String soapEnvelope = construireEnveloppeSoap(annonce);
        Log.d(TAG, "Émission du message SOAP vers " + host + ":" + port + "\n" + soapEnvelope);
        envoyerRequete(host, port, soapEnvelope, callback);
    }

    /**
     * Construit une enveloppe SOAP standard contenant l'annonce sérialisée (compatible JAXB / DOM).
     */
    public static String construireEnveloppeSoap(com.tranotsika.app.model.Annonce annonce) {
        String annonceXml;
        try {
            annonceXml = com.tranotsika.app.data.XmlStorageManager.marshalAnnonceJAXB(annonce);
            // Supprimer le prologue <?xml ... ?> pour intégration propre dans le Body SOAP
            if (annonceXml.startsWith("<?xml")) {
                int finPrologue = annonceXml.indexOf("?>");
                if (finPrologue != -1) {
                    annonceXml = annonceXml.substring(finPrologue + 2).trim();
                }
            }
        } catch (Exception e) {
            annonceXml = com.tranotsika.app.data.XmlStorageManager.exporterAnnonceEnXmlString(annonce);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tran=\"http://tranotsika.app/ws\">\n");
        sb.append("   <soapenv:Header/>\n");
        sb.append("   <soapenv:Body>\n");
        sb.append("      <tran:publierAnnonceRequest>\n");
        sb.append("         ").append(annonceXml).append("\n");
        sb.append("      </tran:publierAnnonceRequest>\n");
        sb.append("   </soapenv:Body>\n");
        sb.append("</soapenv:Envelope>");
        return sb.toString();
    }

    /**
     * Libération des ressources de l'exécuteur.
     */
    public void arreter() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
