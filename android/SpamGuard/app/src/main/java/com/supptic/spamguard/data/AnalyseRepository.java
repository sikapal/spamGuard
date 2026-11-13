package com.supptic.spamguard.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonParser;
import com.supptic.spamguard.model.Analyse;
import com.supptic.spamguard.model.InfosModele;
import com.supptic.spamguard.model.ReponseAnalyse;
import com.supptic.spamguard.model.RequeteAnalyse;
import com.supptic.spamguard.network.ApiService;
import com.supptic.spamguard.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyseRepository {

    private static AnalyseRepository instance;
    private final ApiService api = RetrofitClient.getApi();
    private final MutableLiveData<List<Analyse>> historique = new MutableLiveData<>(new ArrayList<>());

    public static synchronized AnalyseRepository getInstance() {
        if (instance == null) instance = new AnalyseRepository();
        return instance;
    }

    public LiveData<List<Analyse>> getHistorique() { return historique; }

    public void analyser(String texte, Rappel<ReponseAnalyse> rappel) {
        executer(api.analyser(new RequeteAnalyse(texte)), resultat -> {
            if (resultat.etat == Resultat.Etat.SUCCES) {
                // Nouvelle liste (la plus récente en premier) pour que DiffUtil voie le changement
                List<Analyse> liste = new ArrayList<>(historique.getValue());
                liste.add(0, new Analyse(texte, resultat.donnees));
                historique.setValue(liste);
            }
            rappel.surResultat(resultat);
        });
    }

    public void infosModele(Rappel<InfosModele> rappel) {
        executer(api.infosModele(), rappel);
    }

    public void viderHistorique() {
        historique.setValue(new ArrayList<>());
    }

    private <T> void executer(Call<T> appel, Rappel<T> rappel) {
        appel.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    rappel.surResultat(Resultat.succes(response.body()));
                } else {
                    rappel.surResultat(Resultat.erreur(messageErreur(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                rappel.surResultat(Resultat.erreur(
                        "Serveur injoignable. Vérifiez que l'API (uvicorn) est démarrée."));
            }
        });
    }

    private String messageErreur(Response<?> response) {
        try {
            String corps = response.errorBody() != null ? response.errorBody().string() : "";
            return JsonParser.parseString(corps).getAsJsonObject().get("message").getAsString();
        } catch (Exception e) {
            return "Erreur " + response.code();
        }
    }
}
