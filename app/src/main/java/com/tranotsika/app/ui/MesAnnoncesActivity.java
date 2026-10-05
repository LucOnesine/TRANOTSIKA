package com.tranotsika.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlStorageManager;
import com.tranotsika.app.model.Annonce;
import com.tranotsika.app.model.Maison;

import java.util.ArrayList;
import java.util.List;

/**
 * Écran 3 : Mes Annonces / Suivi des Publications ("suivi-publications").
 * Affiche la liste des annonces de l'utilisateur avec filtres par statut (Toutes, Actives, Attente),
 * métriques de vues et favoris, boutons d'édition/suppression, et bouton d'ajout flottant.
 */
public class MesAnnoncesActivity extends AppCompatActivity implements MesAnnoncesAdapter.OnAnnonceActionListener {

    private FrameLayout btnBackMesAnnonces;
    private TextView chipToutes;
    private TextView chipActives;
    private TextView chipAttente;

    private RecyclerView recyclerView;
    private MesAnnoncesAdapter adapter;
    private TextView tvEmptyMesAnnonces;
    private LinearLayout btnFloatingNouvelleAnnonce;

    private List<Annonce> listeToutesAnnonces;
    private List<Annonce> listeAffichee;
    private String filtreActif = "TOUTES"; // TOUTES, ACTIVE, ATTENTE

    // Barre de navigation
    private LinearLayout suiviNavAccueil;
    private LinearLayout suiviNavFavoris;
    private LinearLayout suiviNavPublier;
    private LinearLayout suiviNavMessages;
    private LinearLayout suiviNavProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_annonces);

        initialiserVues();
        configurerRecyclerView();
        configurerFiltres();
        configurerActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerDonneesXml();
    }

    private void initialiserVues() {
        btnBackMesAnnonces = findViewById(R.id.btnBackMesAnnonces);
        chipToutes = findViewById(R.id.chipToutes);
        chipActives = findViewById(R.id.chipActives);
        chipAttente = findViewById(R.id.chipAttente);

        recyclerView = findViewById(R.id.recyclerViewMesAnnonces);
        tvEmptyMesAnnonces = findViewById(R.id.tvEmptyMesAnnonces);
        btnFloatingNouvelleAnnonce = findViewById(R.id.btnFloatingNouvelleAnnonce);

        suiviNavAccueil = findViewById(R.id.suiviNavAccueil);
        suiviNavFavoris = findViewById(R.id.suiviNavFavoris);
        suiviNavPublier = findViewById(R.id.suiviNavPublier);
        suiviNavMessages = findViewById(R.id.suiviNavMessages);
        suiviNavProfil = findViewById(R.id.suiviNavProfil);
    }

    private void configurerRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listeAffichee = new ArrayList<>();
        adapter = new MesAnnoncesAdapter(this, listeAffichee, this);
        recyclerView.setAdapter(adapter);
    }

    private void chargerDonneesXml() {
        listeToutesAnnonces = XmlStorageManager.chargerMesAnnonces(this);
        mettreAJourCompteursChips();
        appliquerFiltres();
    }

    private void mettreAJourCompteursChips() {
        int total = listeToutesAnnonces.size();
        int actives = 0;
        int attente = 0;

        for (Annonce a : listeToutesAnnonces) {
            if ("Attente".equalsIgnoreCase(a.getStatut())) {
                attente++;
            } else {
                actives++;
            }
        }

        chipToutes.setText("Toutes (" + total + ")");
        chipActives.setText("Actives (" + actives + ")");
        chipAttente.setText("Attente (" + attente + ")");
    }

    private void configurerFiltres() {
        chipToutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtreActif = "TOUTES";
                majStyleChips(chipToutes);
                appliquerFiltres();
            }
        });

        chipActives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtreActif = "ACTIVE";
                majStyleChips(chipActives);
                appliquerFiltres();
            }
        });

        chipAttente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtreActif = "ATTENTE";
                majStyleChips(chipAttente);
                appliquerFiltres();
            }
        });
    }

    private void majStyleChips(TextView chipActifSelectionne) {
        TextView[] chips = {chipToutes, chipActives, chipAttente};
        for (TextView c : chips) {
            c.setBackgroundResource(R.drawable.bg_chip_unselected);
            c.setTextColor(getResources().getColor(R.color.text_primary));
        }
        chipActifSelectionne.setBackgroundResource(R.drawable.bg_chip_selected);
        chipActifSelectionne.setTextColor(getResources().getColor(R.color.text_white));
    }

    private void appliquerFiltres() {
        if (listeToutesAnnonces == null) return;

        List<Annonce> filtrees = new ArrayList<>();
        for (Annonce a : listeToutesAnnonces) {
            if ("TOUTES".equalsIgnoreCase(filtreActif)) {
                filtrees.add(a);
            } else if ("ACTIVE".equalsIgnoreCase(filtreActif) && "Active".equalsIgnoreCase(a.getStatut())) {
                filtrees.add(a);
            } else if ("ATTENTE".equalsIgnoreCase(filtreActif) && "Attente".equalsIgnoreCase(a.getStatut())) {
                filtrees.add(a);
            }
        }

        listeAffichee = filtrees;
        adapter.mettreAJourDonnees(filtrees);

        if (filtrees.isEmpty()) {
            tvEmptyMesAnnonces.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyMesAnnonces.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void configurerActions() {
        btnBackMesAnnonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFloatingNouvelleAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesAnnoncesActivity.this, PublierAnnonceActivity.class);
                startActivity(intent);
            }
        });

        // Navigation
        suiviNavAccueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesAnnoncesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        suiviNavFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesAnnoncesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        suiviNavPublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesAnnoncesActivity.this, PublierHubActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onAnnonceClick(Annonce annonce) {
        // Redirige vers DetailActivity en construisant un objet Maison
        Maison maison = new Maison(
                annonce.getId(),
                annonce.getTitre(),
                annonce.getType(),
                annonce.getPrixAffiche(),
                annonce.getLocalisation(),
                "Abidjan",
                annonce.getChambres(),
                annonce.getSallesDeBain(),
                annonce.getSuperficie(),
                true,
                annonce.getDescription(),
                annonce.getImageResNom(),
                false,
                annonce.getNomProprietaire(),
                "Propriétaire / Agent",
                "+225 07 00 00 00 00"
        );

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MAISON, maison);
        startActivity(intent);
    }

    @Override
    public void onEditerClick(Annonce annonce) {
        Intent intent = new Intent(this, PublierAnnonceActivity.class);
        intent.putExtra(PublierAnnonceActivity.EXTRA_ANNONCE_A_MODIFIER, annonce);
        startActivity(intent);
    }

    @Override
    public void onSupprimerClick(final Annonce annonce, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'annonce")
                .setMessage("Êtes-vous sûr de vouloir supprimer définitivement l'annonce \"" + annonce.getTitre() + "\" ?")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Suppression du fichier local XML
                        boolean supprime = XmlStorageManager.supprimerAnnonceXML(MesAnnoncesActivity.this, annonce.getId());
                        if (supprime) {
                            chargerDonneesXml();
                            Toast.makeText(MesAnnoncesActivity.this, "Annonce supprimée avec succès.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
