package com.supptic.spamguard.model;

public class Analyse {

    private final String texte;
    private final ReponseAnalyse reponse;
    private final long horodatage;

    public Analyse(String texte, ReponseAnalyse reponse) {
        this.texte = texte;
        this.reponse = reponse;
        this.horodatage = System.currentTimeMillis();
    }

    public String getTexte() { return texte; }
    public ReponseAnalyse getReponse() { return reponse; }
    public long getHorodatage() { return horodatage; }
}
