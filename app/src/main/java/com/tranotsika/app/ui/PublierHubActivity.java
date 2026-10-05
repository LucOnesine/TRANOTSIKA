package com.tranotsika.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlStorageManager;
import com.tranotsika.app.model.Annonce;

import java.util.List;

/**
 * Écran 1 : Hub Publication & Gestion Premium ("hub-publier-premium").
 * Affiche le statut membre Pro, les cartes de statistiques dynamiques (annonces, vues, favoris)
 * et les options de navigation vers la création, la gestion et l'abonnement.
 */
public class PublierHubActivity extends AppCompatActivity {

    private TextView tvStatNbAnnonces;
    private TextView tvStatVuesTotales;
    private TextView tvStatFavorisRecus;
    private TextView tvSousTitreGerer;

    private MaterialCardView cardCreerAnnonce;
    private MaterialCardView cardGererAnnonces;
    private MaterialCardView cardMonAbonnement;
    private TextView btnGererAbonnementLien;

    // Navigation inférieure
    private LinearLayout hubNavAccueil;
    private LinearLayout hubNavFavoris;
    private LinearLayout hubNavPublier;
    private LinearLayout hubNavMessages;
    private LinearLayout hubNavProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publier_hub);

        initialiserVues();
        configurerActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recalculer les statistiques à partir du fichier XML local mes_annonces.xml
        actualiserStatistiques();
    }

    private void initialiserVues() {
        tvStatNbAnnonces = findViewById(R.id.tvStatNbAnnonces);
        tvStatVuesTotales = findViewById(R.id.tvStatVuesTotales);
        tvStatFavorisRecus = findViewById(R.id.tvStatFavorisRecus);
        tvSousTitreGerer = findViewById(R.id.tvSousTitreGerer);

        cardCreerAnnonce = findViewById(R.id.cardCreerAnnonce);
        cardGererAnnonces = findViewById(R.id.cardGererAnnonces);
        cardMonAbonnement = findViewById(R.id.cardMonAbonnement);
        btnGererAbonnementLien = findViewById(R.id.btnGererAbonnementLien);

        hubNavAccueil = findViewById(R.id.hubNavAccueil);
        hubNavFavoris = findViewById(R.id.hubNavFavoris);
        hubNavPublier = findViewById(R.id.hubNavPublier);
        hubNavMessages = findViewById(R.id.hubNavMessages);
        hubNavProfil = findViewById(R.id.hubNavProfil);
    }

    private void actualiserStatistiques() {
        List<Annonce> mesAnnonces = XmlStorageManager.chargerMesAnnonces(this);

        int nbTotal = mesAnnonces.size();
        int vuesTotales = 0;
        int favorisTotaux = 0;
        int nbActives = 0;

        for (Annonce a : mesAnnonces) {
            vuesTotales += a.getVues();
            favorisTotaux += a.getFavoris();
            if ("Active".equalsIgnoreCase(a.getStatut())) {
                nbActives++;
            }
        }

        tvStatNbAnnonces.setText(nbTotal + "/Illimités");
        tvStatVuesTotales.setText(String.valueOf(vuesTotales));
        tvStatFavorisRecus.setText(String.valueOf(favorisTotaux));
        tvSousTitreGerer.setText("Consultez le statut de vos " + nbActives + " annonces actives.");
    }

    private void configurerActions() {
        // Redirection vers Écran 2 : Formulaire de création
        cardCreerAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierHubActivity.this, PublierAnnonceActivity.class);
                startActivity(intent);
            }
        });

        // Redirection vers Écran 3 : Gestion & Suivi
        cardGererAnnonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierHubActivity.this, MesAnnoncesActivity.class);
                startActivity(intent);
            }
        });

        // Gestion de l'abonnement
        View.OnClickListener abonnementClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublierHubActivity.this, 
                        "Forfait Premium Pro actif jusqu'au 12 Nov 2026. Avantages : annonces illimitées, 10 photos, visibilité max.", 
                        Toast.LENGTH_LONG).show();
            }
        };
        cardMonAbonnement.setOnClickListener(abonnementClick);
        btnGererAbonnementLien.setOnClickListener(abonnementClick);

        // Barre de navigation
        hubNavAccueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierHubActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        hubNavFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublierHubActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        hubNavMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublierHubActivity.this, "Messagerie", Toast.LENGTH_SHORT).show();
            }
        });

        hubNavProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublierHubActivity.this, "Mon Profil Pro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
