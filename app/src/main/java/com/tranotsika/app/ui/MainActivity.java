package com.tranotsika.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlManager;
import com.tranotsika.app.model.Maison;
import com.tranotsika.app.network.SocketClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Activité principale "Accueil" de l'application Tranotsika.
 * Affiche la recherche, les catégories de filtres, la liste des logements recommandés,
 * et la barre de navigation inférieure.
 */
public class MainActivity extends AppCompatActivity implements MaisonAdapter.OnMaisonClickListener {

    private RecyclerView recyclerView;
    private MaisonAdapter adapter;
    private List<Maison> listeCompleteMaisons;
    private List<Maison> listeAffichee;

    private EditText etRecherche;
    private FrameLayout btnFiltreSearch;
    private FrameLayout btnNotification;
    private TextView tvEmptyState;
    private TextView tvVoirTout;
    private TextView tvTitreSection;

    // Chips
    private TextView chipAppartement;
    private TextView chipVilla;
    private TextView chipStudio;
    private TextView chipDuplex;
    private String categorieActive = "Appartement"; // Catégorie sélectionnée par défaut

    // Bottom Navigation
    private LinearLayout navItemAccueil;
    private LinearLayout navItemFavoris;
    private LinearLayout navItemPublier;
    private LinearLayout navItemMessages;
    private LinearLayout navItemProfil;
    private ImageView iconNavAccueil, iconNavFavoris;
    private TextView tvNavAccueil, tvNavFavoris;

    private boolean modeFavoris = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiserVues();
        configurerRecyclerView();
        chargerDonneesXml();
        configurerRecherche();
        configurerChips();
        configurerNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les données au retour pour rafraîchir les favoris modifiés dans DetailActivity
        chargerDonneesXml();
    }

    private void initialiserVues() {
        recyclerView = findViewById(R.id.recyclerViewMaisons);
        etRecherche = findViewById(R.id.etRecherche);
        btnFiltreSearch = findViewById(R.id.btnFiltreSearch);
        btnNotification = findViewById(R.id.btnNotification);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvVoirTout = findViewById(R.id.tvVoirTout);
        tvTitreSection = findViewById(R.id.tvTitreSection);

        chipAppartement = findViewById(R.id.chipAppartement);
        chipVilla = findViewById(R.id.chipVilla);
        chipStudio = findViewById(R.id.chipStudio);
        chipDuplex = findViewById(R.id.chipDuplex);

        navItemAccueil = findViewById(R.id.navItemAccueil);
        navItemFavoris = findViewById(R.id.navItemFavoris);
        navItemPublier = findViewById(R.id.navItemPublier);
        navItemMessages = findViewById(R.id.navItemMessages);
        navItemProfil = findViewById(R.id.navItemProfil);

        iconNavAccueil = findViewById(R.id.iconNavAccueil);
        iconNavFavoris = findViewById(R.id.iconNavFavoris);
        tvNavAccueil = findViewById(R.id.tvNavAccueil);
        tvNavFavoris = findViewById(R.id.tvNavFavoris);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Aucune nouvelle notification", Toast.LENGTH_SHORT).show();
            }
        });

        tvVoirTout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorieActive = null;
                reinitialiserStyleChips();
                appliquerFiltres();
            }
        });

        // Démonstration du client réseau Socket sur clic du bouton filtre
        btnFiltreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testerCommunicationSocket();
            }
        });
    }

    private void configurerRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listeAffichee = new ArrayList<>();
        adapter = new MaisonAdapter(this, listeAffichee, this);
        recyclerView.setAdapter(adapter);
    }

    private void chargerDonneesXml() {
        // Lecture locale à partir de tranotsika_data.xml via DOM Parser
        listeCompleteMaisons = XmlManager.chargerMaisons(this);
        appliquerFiltres();
    }

    private void configurerRecherche() {
        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appliquerFiltres();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void configurerChips() {
        chipAppartement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionnerChip("Appartement", chipAppartement);
            }
        });

        chipVilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionnerChip("Villa", chipVilla);
            }
        });

        chipStudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionnerChip("Studio", chipStudio);
            }
        });

        chipDuplex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionnerChip("Duplex", chipDuplex);
            }
        });
    }

    private void selectionnerChip(String categorie, TextView chipSelectionne) {
        if (categorie.equalsIgnoreCase(categorieActive)) {
            // Désélection si déjà actif
            categorieActive = null;
            reinitialiserStyleChips();
        } else {
            categorieActive = categorie;
            reinitialiserStyleChips();
            chipSelectionne.setBackgroundResource(R.drawable.bg_chip_selected);
            chipSelectionne.setTextColor(getResources().getColor(R.color.text_white));
        }
        appliquerFiltres();
    }

    private void reinitialiserStyleChips() {
        TextView[] chips = {chipAppartement, chipVilla, chipStudio, chipDuplex};
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            chip.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void appliquerFiltres() {
        if (listeCompleteMaisons == null) return;

        String motCle = etRecherche.getText() != null ? etRecherche.getText().toString().trim().toLowerCase() : "";
        List<Maison> resultat = new ArrayList<>();

        for (Maison m : listeCompleteMaisons) {
            // Filtre mode favoris
            if (modeFavoris && !m.isEstFavori()) {
                continue;
            }

            // Filtre par catégorie de puce
            boolean matchCategorie = (categorieActive == null) || m.getType().equalsIgnoreCase(categorieActive);

            // Filtre par recherche textuelle (titre, adresse, ville ou description)
            boolean matchTexte = motCle.isEmpty() ||
                    m.getTitre().toLowerCase().contains(motCle) ||
                    m.getAdresse().toLowerCase().contains(motCle) ||
                    m.getVille().toLowerCase().contains(motCle);

            if (matchCategorie && matchTexte) {
                resultat.add(m);
            }
        }

        listeAffichee = resultat;
        adapter.mettreAJourDonnees(resultat);

        if (resultat.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void configurerNavigation() {
        navItemAccueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeFavoris = false;
                tvTitreSection.setText(R.string.section_recommended);
                iconNavAccueil.setColorFilter(getResources().getColor(R.color.nav_active));
                tvNavAccueil.setTextColor(getResources().getColor(R.color.nav_active));
                iconNavFavoris.setColorFilter(getResources().getColor(R.color.nav_inactive));
                tvNavFavoris.setTextColor(getResources().getColor(R.color.nav_inactive));
                chargerDonneesXml();
            }
        });

        navItemFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeFavoris = true;
                tvTitreSection.setText("Mes logements favoris");
                iconNavFavoris.setColorFilter(getResources().getColor(R.color.nav_active));
                tvNavFavoris.setTextColor(getResources().getColor(R.color.nav_active));
                iconNavAccueil.setColorFilter(getResources().getColor(R.color.nav_inactive));
                tvNavAccueil.setTextColor(getResources().getColor(R.color.nav_inactive));
                chargerDonneesXml();
            }
        });

        navItemPublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PublierHubActivity.class);
                startActivity(intent);
            }
        });

        navItemMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Messagerie avec les propriétaires", Toast.LENGTH_SHORT).show();
            }
        });

        navItemProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Mon Profil Utilisateur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Teste la communication Socket asynchrone sans bloquer l'UI Thread.
     */
    private void testerCommunicationSocket() {
        Toast.makeText(this, R.string.toast_socket_searching, Toast.LENGTH_SHORT).show();

        // Envoi d'une requête de recherche en arrière-plan vers le serveur Socket
        SocketClient.getInstance().rechercherLogements("127.0.0.1", 8080, "Riviera", "Duplex", new SocketClient.SocketResponseCallback() {
            @Override
            public void onSuccess(String reponse) {
                Toast.makeText(MainActivity.this, "Données reçues du serveur Socket !", Toast.LENGTH_LONG).show();
                // Si la réponse est du XML, elle peut être parsée directement :
                List<Maison> distantes = XmlManager.parserXmlString(reponse);
                if (!distantes.isEmpty()) {
                    listeCompleteMaisons = distantes;
                    appliquerFiltres();
                }
            }

            @Override
            public void onError(Exception exception) {
                // Démonstration : si le serveur local n'est pas allumé, on informe l'utilisateur
                Toast.makeText(MainActivity.this, "Client Socket prêt (serveur 127.0.0.1:8080 non joignable). Mode données XML locales actif.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMaisonClick(Maison maison) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MAISON, maison);
        startActivity(intent);
    }

    @Override
    public void onFavoriToggle(Maison maison, boolean estFavori) {
        // La mise à jour XML est déjà effectuée dans l'adapter via XmlManager
        if (modeFavoris && !estFavori) {
            appliquerFiltres();
        }
    }
}
