package com.tranotsika.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlStorageManager;
import com.tranotsika.app.model.Annonce;
import com.tranotsika.app.network.SocketClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Écran 2 : Formulaire de Création / Édition d'Annonce ("publier-annonce").
 * Permet de saisir les détails du bien, d'émettre la structure XML au serveur par Socket TCP
 * sur un thread d'arrière-plan, et d'enregistrer l'annonce dans mes_annonces.xml.
 */
public class PublierAnnonceActivity extends AppCompatActivity {

    public static final String EXTRA_ANNONCE_A_MODIFIER = "extra_annonce_a_modifier";

    private FrameLayout btnBackPublier;
    private FrameLayout btnAddPhoto;

    private EditText etFormTitre;
    private EditText etFormType;
    private EditText etFormPrix;
    private EditText etFormLocalisation;
    private EditText etFormChambres;
    private EditText etFormSdb;
    private EditText etFormSuperficie;
    private EditText etFormNom;
    private EditText etFormDescription;
    private AppCompatButton btnPublierAnnonce;

    // Navigation inférieure
    private LinearLayout formNavAccueil;
    private LinearLayout formNavFavoris;
    private LinearLayout formNavPublier;
    private LinearLayout formNavMessages;
    private LinearLayout formNavProfil;

    private Annonce annonceEnModification;
    private boolean modeModification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publier_annonce);

        initialiserVues();
        verifierModeEdition();
        configurerActions();
    }

    private void initialiserVues() {
        btnBackPublier = findViewById(R.id.btnBackPublier);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);

        etFormTitre = findViewById(R.id.etFormTitre);
        etFormType = findViewById(R.id.etFormType);
        etFormPrix = findViewById(R.id.etFormPrix);
        etFormLocalisation = findViewById(R.id.etFormLocalisation);
        etFormChambres = findViewById(R.id.etFormChambres);
        etFormSdb = findViewById(R.id.etFormSdb);
        etFormSuperficie = findViewById(R.id.etFormSuperficie);
        etFormNom = findViewById(R.id.etFormNom);
        etFormDescription = findViewById(R.id.etFormDescription);
        btnPublierAnnonce = findViewById(R.id.btnPublierAnnonce);

        formNavAccueil = findViewById(R.id.formNavAccueil);
        formNavFavoris = findViewById(R.id.formNavFavoris);
        formNavPublier = findViewById(R.id.formNavPublier);
        formNavMessages = findViewById(R.id.formNavMessages);
        formNavProfil = findViewById(R.id.formNavProfil);
    }

    private void verifierModeEdition() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_ANNONCE_A_MODIFIER)) {
            annonceEnModification = (Annonce) getIntent().getSerializableExtra(EXTRA_ANNONCE_A_MODIFIER);
            if (annonceEnModification != null) {
                modeModification = true;
                btnPublierAnnonce.setText("Enregistrer les modifications");

                // Pré-remplissage des champs
                etFormTitre.setText(annonceEnModification.getTitre());
                etFormType.setText(annonceEnModification.getType());
                etFormPrix.setText(annonceEnModification.getPrix());
                etFormLocalisation.setText(annonceEnModification.getLocalisation());
                etFormChambres.setText(String.valueOf(annonceEnModification.getChambres()));
                etFormSdb.setText(String.valueOf(annonceEnModification.getSallesDeBain()));
                etFormSuperficie.setText(String.valueOf(annonceEnModification.getSuperficie()));
                etFormNom.setText(annonceEnModification.getNomProprietaire());
                etFormDescription.setText(annonceEnModification.getDescription());
            }
        }
    }

    private void configurerActions() {
        // Bouton Retour
        btnBackPublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Bouton "+" Ajout de photo
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublierAnnonceActivity.this, "Photo ajoutée à la galerie de la propriété (4/10)", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton "Publier l'annonce"
        btnPublierAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                traiterPublication();
            }
        });

        // Navigation
        formNavAccueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierAnnonceActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        formNavFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierAnnonceActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void traiterPublication() {
        String titre = etFormTitre.getText().toString().trim();
        String type = etFormType.getText().toString().trim();
        String prix = etFormPrix.getText().toString().trim();
        String localisation = etFormLocalisation.getText().toString().trim();
        String chambresStr = etFormChambres.getText().toString().trim();
        String sdbStr = etFormSdb.getText().toString().trim();
        String superficieStr = etFormSuperficie.getText().toString().trim();
        String nom = etFormNom.getText().toString().trim();
        String description = etFormDescription.getText().toString().trim();

        if (titre.isEmpty() || prix.isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner au moins le titre et le prix.", Toast.LENGTH_SHORT).show();
            return;
        }

        int chambres = 1;
        int sdb = 1;
        int superficie = 50;
        try {
            chambres = Integer.parseInt(chambresStr);
            sdb = Integer.parseInt(sdbStr);
            superficie = Integer.parseInt(superficieStr);
        } catch (Exception ignored) {}

        final Annonce annonce = modeModification ? annonceEnModification : new Annonce();
        if (!modeModification) {
            annonce.setId("A" + (System.currentTimeMillis() % 10000));
            // Date du jour format court ex: "12 Oct"
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.FRENCH);
            annonce.setDatePublication(sdf.format(new Date()));
            annonce.setStatut("Active");
            annonce.setVues(0);
            annonce.setFavoris(0);
            annonce.setImageResNom("house_sample_1");
        }

        annonce.setTitre(titre);
        annonce.setType(type.isEmpty() ? "Appartement" : type);
        annonce.setPrix(prix.contains("Ar") || prix.contains("FCFA") ? prix : prix + " Ar/mois");
        annonce.setLocalisation(localisation);
        annonce.setChambres(chambres);
        annonce.setSallesDeBain(sdb);
        annonce.setSuperficie(superficie);
        annonce.setNomProprietaire(nom);
        annonce.setDescription(description);

        Toast.makeText(this, "Transmission de l'annonce SOAP / JAXB via Socket TCP...", Toast.LENGTH_SHORT).show();

        // 3. Transmission réseau SOAP via Sockets (sur thread secondaire avec ExecutorService)
        SocketClient.getInstance().publierAnnonceSoap("127.0.0.1", 8080, annonce, new SocketClient.SocketResponseCallback() {
            @Override
            public void onSuccess(String reponse) {
                // Confirmation SOAP reçue du serveur distant avant validation locale
                finaliserSauvegardeLocale(annonce, "Annonce confirmée par le serveur SOAP Socket et enregistrée !");
            }

            @Override
            public void onError(Exception exception) {
                // Si serveur socket inaccessible en test local, validation directe en stockage interne XML
                finaliserSauvegardeLocale(annonce, "Serveur SOAP Socket (127.0.0.1:8080) hors-ligne. Annonce enregistrée localement dans mes_annonces.xml !");
            }
        });
    }

    private void finaliserSauvegardeLocale(Annonce annonce, String messageFeedback) {
        if (modeModification) {
            XmlStorageManager.modifierAnnonceXML(this, annonce);
        } else {
            XmlStorageManager.ajouterAnnonceXML(this, annonce);
        }

        Toast.makeText(this, messageFeedback, Toast.LENGTH_LONG).show();

        // Redirection vers l'écran de suivi "Mes Annonces"
        Intent intent = new Intent(this, MesAnnoncesActivity.class);
        startActivity(intent);
        finish();
    }
}
