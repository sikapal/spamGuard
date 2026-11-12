package com.supptic.spamguard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.supptic.spamguard.data.AnalyseRepository;
import com.supptic.spamguard.data.Resultat;
import com.supptic.spamguard.model.Analyse;
import com.supptic.spamguard.model.InfosModele;
import com.supptic.spamguard.model.ReponseAnalyse;

import java.util.List;

public class AnalyseViewModel extends ViewModel {

    private final AnalyseRepository repository = AnalyseRepository.getInstance();

    private final MutableLiveData<Resultat<ReponseAnalyse>> analyse = new MutableLiveData<>();
    private final MutableLiveData<Resultat<InfosModele>> infos = new MutableLiveData<>();

    // ----- Analyse (MainActivity) -----
    public LiveData<Resultat<ReponseAnalyse>> getAnalyse() { return analyse; }

    public void analyser(String texte) {
        analyse.setValue(Resultat.chargement());
        repository.analyser(texte, analyse::setValue);
    }

    // ----- Historique (HistoriqueActivity) -----
    public LiveData<List<Analyse>> getHistorique() { return repository.getHistorique(); }

    public void viderHistorique() { repository.viderHistorique(); }

    // ----- Infos du modèle (ModeleActivity) -----
    public LiveData<Resultat<InfosModele>> getInfos() { return infos; }

    public void chargerInfos() {
        infos.setValue(Resultat.chargement());
        repository.infosModele(infos::setValue);
    }
}

