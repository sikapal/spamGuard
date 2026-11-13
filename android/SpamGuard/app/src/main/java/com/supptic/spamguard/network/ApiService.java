package com.supptic.spamguard.network;

import com.supptic.spamguard.model.InfosModele;
import com.supptic.spamguard.model.ReponseAnalyse;
import com.supptic.spamguard.model.RequeteAnalyse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/analyser")
    Call<ReponseAnalyse> analyser(@Body RequeteAnalyse requete);

    @GET("api/modele")
    Call<InfosModele> infosModele();
}
