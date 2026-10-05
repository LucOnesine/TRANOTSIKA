package com.tranotsika.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.tranotsika.app.R;
import com.tranotsika.app.data.XmlManager;
import com.tranotsika.app.model.Maison;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour l'affichage des cartes de logements dans l'écran Accueil.
 */
public class MaisonAdapter extends RecyclerView.Adapter<MaisonAdapter.MaisonViewHolder> {

    public interface OnMaisonClickListener {
        void onMaisonClick(Maison maison);
        void onFavoriToggle(Maison maison, boolean estFavori);
    }

    private final Context context;
    private List<Maison> listeMaisons;
    private final OnMaisonClickListener listener;

    public MaisonAdapter(Context context, List<Maison> listeMaisons, OnMaisonClickListener listener) {
        this.context = context;
        this.listeMaisons = listeMaisons != null ? new ArrayList<>(listeMaisons) : new ArrayList<Maison>();
        this.listener = listener;
    }

    public void mettreAJourDonnees(List<Maison> nouvellesMaisons) {
        this.listeMaisons = nouvellesMaisons != null ? new ArrayList<>(nouvellesMaisons) : new ArrayList<Maison>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaisonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maison, parent, false);
        return new MaisonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MaisonViewHolder holder, int position) {
        final Maison maison = listeMaisons.get(position);

        holder.tvTitre.setText(maison.getTitre());
        holder.tvAdresse.setText(maison.getAdresse());
        holder.tvPrixBadge.setText(maison.getPrix());
        holder.tvCaracteristiques.setText(maison.getCaracteristiquesResume());

        // Choix de l'image d'illustration
        if ("house_sample_2".equalsIgnoreCase(maison.getImageNom())) {
            holder.imgMaison.setImageResource(R.drawable.house_sample_2);
        } else {
            holder.imgMaison.setImageResource(R.drawable.house_sample_1);
        }

        // Affichage de l'état favori (cœur rempli ou contour)
        majIconeFavori(holder.imgFavoriCard, maison.isEstFavori());

        // Clic sur l'ensemble de la carte -> écran détail
        holder.cardMaison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMaisonClick(maison);
                }
            }
        });

        // Clic sur le bouton favori circulaire
        holder.btnFavoriCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nouvelEtat = !maison.isEstFavori();
                maison.setEstFavori(nouvelEtat);
                majIconeFavori(holder.imgFavoriCard, nouvelEtat);

                // Sauvegarde directe dans le fichier XML via XmlManager
                XmlManager.setFavori(context, maison.getId(), nouvelEtat);

                String msg = nouvelEtat ? context.getString(R.string.toast_favorite_added) 
                                        : context.getString(R.string.toast_favorite_removed);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onFavoriToggle(maison, nouvelEtat);
                }
            }
        });
    }

    private void majIconeFavori(ImageView imgView, boolean estFavori) {
        if (estFavori) {
            imgView.setImageResource(R.drawable.ic_heart_filled);
        } else {
            imgView.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    @Override
    public int getItemCount() {
        return listeMaisons.size();
    }

    static class MaisonViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMaison;
        ImageView imgMaison;
        TextView tvPrixBadge;
        FrameLayout btnFavoriCard;
        ImageView imgFavoriCard;
        TextView tvTitre;
        TextView tvAdresse;
        TextView tvCaracteristiques;

        public MaisonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMaison = itemView.findViewById(R.id.cardMaison);
            imgMaison = itemView.findViewById(R.id.imgMaison);
            tvPrixBadge = itemView.findViewById(R.id.tvPrixBadge);
            btnFavoriCard = itemView.findViewById(R.id.btnFavoriCard);
            imgFavoriCard = itemView.findViewById(R.id.imgFavoriCard);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvAdresse = itemView.findViewById(R.id.tvAdresse);
            tvCaracteristiques = itemView.findViewById(R.id.tvCaracteristiques);
        }
    }
}
