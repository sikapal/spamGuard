package com.supptic.spamguard.ui;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.supptic.spamguard.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalyseAdapter extends ListAdapter<Analyse, AnalyseAdapter.AnalyseViewHolder> {

    public AnalyseAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public AnalyseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analyse, parent, false);
        return new AnalyseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class AnalyseViewHolder extends RecyclerView.ViewHolder {
        private static final SimpleDateFormat HEURE = new SimpleDateFormat("HH:mm", Locale.FRANCE);

        private final ImageView ivIcone;
        private final TextView tvTexte, tvVerdict, tvHeure, tvPourcentage;

        AnalyseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcone = itemView.findViewById(R.id.ivIcone);
            tvTexte = itemView.findViewById(R.id.tvTexte);
            tvVerdict = itemView.findViewById(R.id.tvVerdict);
            tvHeure = itemView.findViewById(R.id.tvHeure);
            tvPourcentage = itemView.findViewById(R.id.tvPourcentage);
        }

        void bind(Analyse a) {
            boolean spam = a.getReponse().isSpam();
            int couleur = ContextCompat.getColor(itemView.getContext(), spam ? R.color.danger : R.color.safe);
            int fond = ContextCompat.getColor(itemView.getContext(),
                    spam ? R.color.danger_container : R.color.safe_container);

            ivIcone.setImageResource(spam ? R.drawable.ic_dangerous : R.drawable.ic_check_circle);
            ivIcone.setBackgroundTintList(ColorStateList.valueOf(fond));
            ImageViewCompat.setImageTintList(ivIcone, ColorStateList.valueOf(couleur));

            tvTexte.setText(a.getTexte());
            tvVerdict.setText(spam ? R.string.verdict_spam : R.string.verdict_fiable);
            tvVerdict.setTextColor(couleur);
            tvHeure.setText("· " + HEURE.format(new Date(a.getHorodatage())));
            tvPourcentage.setText(itemView.getContext()
                    .getString(R.string.pourcentage_format, a.getReponse().getConfiance()));
            tvPourcentage.setTextColor(couleur);
        }
    }

    private static final DiffUtil.ItemCallback<Analyse> DIFF = new DiffUtil.ItemCallback<Analyse>() {
        @Override
        public boolean areItemsTheSame(@NonNull Analyse a, @NonNull Analyse b) {
            return a.getHorodatage() == b.getHorodatage();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Analyse a, @NonNull Analyse b) {
            return a.getTexte().equals(b.getTexte());
        }
    };
}

