package com.supptic.spamguard.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.supptic.spamguard.R;
import com.supptic.spamguard.model.ReponseAnalyse;
import com.supptic.spamguard.viewmodel.AnalyseViewModel;

public class MainActivity extends AppCompatActivity {

    private AnalyseViewModel viewModel;
    private TextInputLayout tilMessage;
    private MaterialButton btnAnalyser;
    private LinearProgressIndicator progress;
    private MaterialCardView cardResultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilMessage = findViewById(R.id.tilMessage);
        btnAnalyser = findViewById(R.id.btnAnalyser);
        progress = findViewById(R.id.progress);
        cardResultat = findViewById(R.id.cardResultat);

        // Menu de la barre (entrées ajoutées aux étapes 5 et 6)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> false);

        // Bouton Coller
        findViewById(R.id.btnColler).setOnClickListener(v -> collerDepuisPressePapiers());

        // Exemples
        findViewById(R.id.chipArnaque).setOnClickListener(v ->
                tilMessage.getEditText().setText(R.string.texte_exemple_arnaque));
        findViewById(R.id.chipNormal).setOnClickListener(v ->
                tilMessage.getEditText().setText(R.string.texte_exemple_normal));

        // ViewModel
        viewModel = new ViewModelProvider(this).get(AnalyseViewModel.class);
        viewModel.getAnalyse().observe(this, resultat -> {
            switch (resultat.etat) {
                case CHARGEMENT:
                    progress.setVisibility(View.VISIBLE);
                    btnAnalyser.setEnabled(false);
                    break;
                case SUCCES:
                    progress.setVisibility(View.GONE);
                    btnAnalyser.setEnabled(true);
                    afficherResultat(resultat.donnees);
                    break;
                case ERREUR:
                    progress.setVisibility(View.GONE);
                    btnAnalyser.setEnabled(true);
                    Snackbar.make(findViewById(R.id.racine), resultat.message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.reessayer, v -> lancerAnalyse())
                            .show();
                    break;
            }
        });

        btnAnalyser.setOnClickListener(v -> lancerAnalyse());
    }

    private void lancerAnalyse() {
        String texte = tilMessage.getEditText().getText().toString().trim();
        if (texte.isEmpty()) {
            tilMessage.setError(getString(R.string.erreur_vide));
            return;
        }
        tilMessage.setError(null);
        cacherClavier();
        viewModel.analyser(texte);
    }

    private void afficherResultat(ReponseAnalyse r) {
        boolean spam = r.isSpam();
        int couleur = ContextCompat.getColor(this, spam ? R.color.danger : R.color.safe);
        int fond = ContextCompat.getColor(this, spam ? R.color.danger_container : R.color.safe_container);

        // Couleurs de la carte
        cardResultat.setCardBackgroundColor(fond);
        cardResultat.setStrokeColor(couleur);

        // Icône
        ImageView ivVerdict = findViewById(R.id.ivVerdict);
        ivVerdict.setImageResource(spam ? R.drawable.ic_dangerous : R.drawable.ic_check_circle);
        ivVerdict.setBackgroundTintList(ColorStateList.valueOf(couleur));
        ImageViewCompat.setImageTintList(ivVerdict,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.on_primary)));

        // Textes
        TextView tvVerdict = findViewById(R.id.tvVerdict);
        tvVerdict.setText(spam ? R.string.verdict_spam : R.string.verdict_fiable);
        tvVerdict.setTextColor(couleur);
        ((TextView) findViewById(R.id.tvConfiance))
                .setText(getString(R.string.confiance_format, r.getConfiance()));
        ((TextView) findViewById(R.id.tvConseil))
                .setText(spam ? R.string.conseil_spam : R.string.conseil_fiable);

        // Jauge animée
        LinearProgressIndicator barre = findViewById(R.id.barreConfiance);
        barre.setIndicatorColor(couleur);
        barre.setProgressCompat(0, false);
        barre.setProgressCompat(r.getConfiance(), true);

        // Apparition : fondu + glissement, puis défilement jusqu'à la carte
        cardResultat.setVisibility(View.VISIBLE);
        cardResultat.setAlpha(0f);
        cardResultat.setTranslationY(48f);
        cardResultat.animate().alpha(1f).translationY(0f).setDuration(350).start();

        NestedScrollView scroll = findViewById(R.id.scroll);
        scroll.post(() -> scroll.smoothScrollTo(0, cardResultat.getTop()));
    }

    private void collerDepuisPressePapiers() {
        ClipboardManager cm = getSystemService(ClipboardManager.class);
        if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip().getItemCount() > 0) {
            ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
            tilMessage.getEditText().setText(item.coerceToText(this));
        } else {
            Snackbar.make(findViewById(R.id.racine), R.string.presse_papiers_vide,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void cacherClavier() {
        InputMethodManager imm = getSystemService(InputMethodManager.class);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
