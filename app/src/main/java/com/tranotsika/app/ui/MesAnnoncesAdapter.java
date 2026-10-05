package com.tranotsika.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.tranotsika.app.R;
import com.tranotsika.app.model.Annonce;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour l'écran "Mes Annonces" (suivi des publications).
 * Affiche la vignette, le statut, les vues, les favoris et les boutons d'édition / suppression.
 */
public class MesAnnoncesAdapter extends RecyclerView.Adapter<MesAnnoncesAdapter.AnnonceViewHolder> {

    public interface OnAnnonceActionListener {
        void onEditerClick(Annonce annonce);
        void onSupprimerClick(Annonce annonce, int position);
        void onAnnonceClick(Annonce annonce);
    }

    private final Context context;
    private List<Annonce> listeAnnonces;
    private final OnAnnonceActionListener listener;

    public MesAnnoncesAdapter(Context context, List<Annonce> listeAnnonces, OnAnnonceActionListener listener) {
        this.context = context;
        this.listeAnnonces = listeAnnonces != null ? new ArrayList<>(listeAnnonces) : new ArrayList<Annonce>();
        this.listener = listener;
    }

    public void mettreAJourDonnees(List<Annonce> nouvellesAnnonces) {
        this.listeAnnonces = nouvellesAnnonces != null ? new ArrayList<>(nouvellesAnnonces) : new ArrayList<Annonce>();
        notifyDataSetChanged();
    }

    public void supprimerItem(int position) {
        if (position >= 0 && position < listeAnnonces.size()) {
            listeAnnonces.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listeAnnonces.size());
        }
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mes_annonce, parent, false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnnonceViewHolder holder, final int position) {
        final Annonce annonce = listeAnnonces.get(position);

        holder.tvTitreAnnonce.setText(annonce.getTitre());
        holder.tvPrixAnnonce.setText(annonce.getPrixAffiche());
        holder.tvDatePublication.setText("Publié le " + annonce.getDatePublication());
        holder.tvVuesCount.setText(annonce.getVues() + " vues");
        holder.tvFavorisCount.setText(annonce.getFavoris() + " favoris");

        // Style du badge de statut
        if ("Attente".equalsIgnoreCase(annonce.getStatut())) {
            holder.tvStatutBadge.setText("Attente");
            holder.tvStatutBadge.setBackgroundResource(R.drawable.bg_badge_pending);
            holder.tvStatutBadge.setTextColor(Color.parseColor("#B45309"));
        } else {
            holder.tvStatutBadge.setText("Active");
            holder.tvStatutBadge.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatutBadge.setTextColor(Color.parseColor("#15803D"));
        }

        // Image d'illustration
        if ("house_sample_2".equalsIgnoreCase(annonce.getImageResNom())) {
            holder.imgAnnonce.setImageResource(R.drawable.house_sample_2);
        } else {
            holder.imgAnnonce.setImageResource(R.drawable.house_sample_1);
        }

        // Clic global sur l'annonce
        holder.cardMesAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAnnonceClick(annonce);
                }
            }
        });

        // Clic sur Éditer
        holder.btnEditerAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditerClick(annonce);
                }
            }
        });

        // Clic sur Supprimer
        holder.btnSupprimerAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSupprimerClick(annonce, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listeAnnonces.size();
    }

    static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMesAnnonce;
        ImageView imgAnnonce;
        TextView tvStatutBadge;
        TextView tvDatePublication;
        TextView tvTitreAnnonce;
        TextView tvPrixAnnonce;
        TextView tvVuesCount;
        TextView tvFavorisCount;
        FrameLayout btnEditerAnnonce;
        FrameLayout btnSupprimerAnnonce;

        public AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMesAnnonce = itemView.findViewById(R.id.cardMesAnnonce);
            imgAnnonce = itemView.findViewById(R.id.imgAnnonce);
            tvStatutBadge = itemView.findViewById(R.id.tvStatutBadge);
            tvDatePublication = itemView.findViewById(R.id.tvDatePublication);
            tvTitreAnnonce = itemView.findViewById(R.id.tvTitreAnnonce);
            tvPrixAnnonce = itemView.findViewById(R.id.tvPrixAnnonce);
            tvVuesCount = itemView.findViewById(R.id.tvVuesCount);
            tvFavorisCount = itemView.findViewById(R.id.tvFavorisCount);
            btnEditerAnnonce = itemView.findViewById(R.id.btnEditerAnnonce);
            btnSupprimerAnnonce = itemView.findViewById(R.id.btnSupprimerAnnonce);
        }
    }
}
