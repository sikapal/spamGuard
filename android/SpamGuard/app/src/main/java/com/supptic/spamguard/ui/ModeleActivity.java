package com.supptic.spamguard.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.supptic.spamguard.R;
import com.supptic.spamguard.model.InfosModele;
import com.supptic.spamguard.viewmodel.AnalyseViewModel;

import java.util.Locale;

public class ModeleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modele);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        View contenu = findViewById(R.id.contenu);
        View progress = findViewById(R.id.progress);
        View layoutErreur = findViewById(R.id.layoutErreur);

        AnalyseViewModel viewModel = new ViewModelProvider(this).get(AnalyseViewModel.class);
        viewModel.getInfos().observe(this, resultat -> {
            progress.setVisibility(resultat.etat == com.supptic.spamguard.data.Resultat.Etat.CHARGEMENT
                    ? View.VISIBLE : View.GONE);
            switch (resultat.etat) {
                case CHARGEMENT:
                    layoutErreur.setVisibility(View.GONE);
                    break;
                case SUCCES:
                    contenu.setVisibility(View.VISIBLE);
                    afficher(resultat.donnees);
                    break;
                case ERREUR:
                    ((TextView) findViewById(R.id.tvErreur)).setText(resultat.message);
                    layoutErreur.setVisibility(View.VISIBLE);
                    break;
            }
        });

        findViewById(R.id.btnReessayer).setOnClickListener(v -> viewModel.chargerInfos());
        if (viewModel.getInfos().getValue() == null) viewModel.chargerInfos();   // pas après une rotation
    }

    private void afficher(InfosModele m) {
        ((TextView) findViewById(R.id.tvAlgorithme)).setText(m.getAlgorithme());
        ((TextView) findViewById(R.id.tvDate)).setText(getString(R.string.date_format, m.getDateEntrainement()));

        remplirTuile(R.id.tuileExactitude, R.string.metrique_exactitude, R.string.aide_exactitude, m.getExactitude());
        remplirTuile(R.id.tuilePrecision, R.string.metrique_precision, R.string.aide_precision, m.getPrecisionSpam());
        remplirTuile(R.id.tuileRappel, R.string.metrique_rappel, R.string.aide_rappel, m.getRappelSpam());
        remplirTuile(R.id.tuileF1, R.string.metrique_f1, R.string.aide_f1, m.getF1Spam());

        ((TextView) findViewById(R.id.tvNbEntrainement))
                .setText(getString(R.string.nb_entrainement_format, m.getNbEntrainement()));
        ((TextView) findViewById(R.id.tvNbTest))
                .setText(getString(R.string.nb_test_format, m.getNbTest()));
    }

    /** Remplit une tuile incluse : 0.9781 -> "97,8 %" + jauge à 98. */
    private void remplirTuile(int idTuile, int libelle, int aide, double valeur) {
        View tuile = findViewById(idTuile);
        ((TextView) tuile.findViewById(R.id.tvValeur))
                .setText(String.format(Locale.FRANCE, "%.1f %%", valeur * 100));
        ((TextView) tuile.findViewById(R.id.tvLibelle)).setText(libelle);
        ((TextView) tuile.findViewById(R.id.tvAide)).setText(aide);
        ((LinearProgressIndicator) tuile.findViewById(R.id.barre))
                .setProgressCompat((int) Math.round(valeur * 100), true);
    }
}
