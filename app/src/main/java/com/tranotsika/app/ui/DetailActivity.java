package com.tranotsika.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlManager;
import com.tranotsika.app.model.Maison;

/**
 * Activité "Détail" de l'application Tranotsika.
 * Affiche l'image hero avec boutons flottants (retour, partage, favori),
 * les caractéristiques complètes, la description, la carte de l'agent,
 * et les boutons d'action "Contacter" et "Réserver".
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MAISON = "extra_maison";

    private Maison maison;

    private ImageView imgHero;
    private FrameLayout btnBack;
    private FrameLayout btnShare;
    private FrameLayout btnFavoriDetail;
    private ImageView imgFavoriDetail;

    private TextView tvDetailTitre;
    private TextView tvDetailPrix;
    private TextView tvDetailAdresse;
    private TextView badgeChambres;
    private TextView badgeSdb;
    private TextView badgeSuperficie;
    private TextView badgeMeuble;
    private TextView tvDetailDescription;

    private TextView tvAgentNom;
    private TextView tvAgentRole;

    private AppCompatButton btnContacter;
    private AppCompatButton btnReserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recupererDonneesIntent();
        initialiserVues();
        remplirChamps();
        configurerActions();
    }

    private void recupererDonneesIntent() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_MAISON)) {
            maison = (Maison) getIntent().getSerializableExtra(EXTRA_MAISON);
        }

        // Sécurité si aucune maison n'a été passée
        if (maison == null) {
            maison = new Maison(
                    "m1",
                    "Duplex Moderne Riviera",
                    "Duplex",
                    "250k AR/m",
                    "Cocody Riviera 3, Abidjan, Côte d'Ivoire",
                    "Abidjan",
                    3,
                    2,
                    120,
                    true,
                    "Ce magnifique duplex meublé, situé en plein cœur de la Riviera 3, offre des prestations haut de gamme avec une cuisine équipée, un grand salon lumineux et une sécurité 24h/24. Idéal pour une famille ou un cadre dynamique.",
                    "house_sample_1",
                    false,
                    "Amadou Koné",
                    "Agent Certifié Fediko",
                    "+2250701020304"
            );
        }
    }

    private void initialiserVues() {
        imgHero = findViewById(R.id.imgHero);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnFavoriDetail = findViewById(R.id.btnFavoriDetail);
        imgFavoriDetail = findViewById(R.id.imgFavoriDetail);

        tvDetailTitre = findViewById(R.id.tvDetailTitre);
        tvDetailPrix = findViewById(R.id.tvDetailPrix);
        tvDetailAdresse = findViewById(R.id.tvDetailAdresse);
        badgeChambres = findViewById(R.id.badgeChambres);
        badgeSdb = findViewById(R.id.badgeSdb);
        badgeSuperficie = findViewById(R.id.badgeSuperficie);
        badgeMeuble = findViewById(R.id.badgeMeuble);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        tvAgentNom = findViewById(R.id.tvAgentNom);
        tvAgentRole = findViewById(R.id.tvAgentRole);

        btnContacter = findViewById(R.id.btnContacter);
        btnReserver = findViewById(R.id.btnReserver);
    }

    private void remplirChamps() {
        // Image d'illustration
        if ("house_sample_2".equalsIgnoreCase(maison.getImageNom())) {
            imgHero.setImageResource(R.drawable.house_sample_2);
        } else {
            imgHero.setImageResource(R.drawable.house_sample_1);
        }

        tvDetailTitre.setText(maison.getTitre());
        // Affichage prix format compact comme la maquette : "250k AR/m"
        String prixAffiche = maison.getPrix();
        if (prixAffiche != null && prixAffiche.contains("250 000")) {
            prixAffiche = "250k AR/m";
        }
        tvDetailPrix.setText(prixAffiche);

        tvDetailAdresse.setText(maison.getAdresse());

        // Badges
        badgeChambres.setText(getString(R.string.feature_bedrooms, maison.getChambres()));
        badgeSdb.setText(getString(R.string.feature_bathrooms, maison.getSallesDeBain()));
        badgeSuperficie.setText(getString(R.string.feature_surface, maison.getSuperficie()));
        badgeMeuble.setText(maison.isMeuble() ? getString(R.string.feature_furnished) : getString(R.string.feature_unfurnished));

        // Description
        tvDetailDescription.setText(maison.getDescription());

        // Profil agent
        if (maison.getNomAgent() != null && !maison.getNomAgent().isEmpty()) {
            tvAgentNom.setText(maison.getNomAgent());
        }
        if (maison.getRoleAgent() != null && !maison.getRoleAgent().isEmpty()) {
            tvAgentRole.setText(maison.getRoleAgent());
        }

        // État de l'icône favori
        majIconeFavori(maison.isEstFavori());
    }

    private void configurerActions() {
        // Bouton Retour
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Bouton Partager
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partagerLogement();
            }
        });

        // Bouton Favori
        btnFavoriDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nouvelEtat = !maison.isEstFavori();
                maison.setEstFavori(nouvelEtat);
                majIconeFavori(nouvelEtat);

                // Sauvegarde persistante en XML interne
                XmlManager.setFavori(DetailActivity.this, maison.getId(), nouvelEtat);

                String msg = nouvelEtat ? getString(R.string.toast_favorite_added) 
                                        : getString(R.string.toast_favorite_removed);
                Toast.makeText(DetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Contacter
        btnContacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacterAgent();
            }
        });

        // Bouton Réserver
        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, getString(R.string.toast_reserved), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void majIconeFavori(boolean estFavori) {
        if (estFavori) {
            imgFavoriDetail.setImageResource(R.drawable.ic_heart_filled);
        } else {
            imgFavoriDetail.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private void partagerLogement() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Regardez ce logement sur Tranotsika : " 
                + maison.getTitre() + " (" + maison.getPrix() + ") à " + maison.getAdresse());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Partager le logement via");
        startActivity(shareIntent);
    }

    private void contacterAgent() {
        String num = maison.getTelephoneAgent() != null ? maison.getTelephoneAgent() : "+22507000000";
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + num));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.toast_contacting, maison.getNomAgent()), Toast.LENGTH_SHORT).show();
        }
    }
}
