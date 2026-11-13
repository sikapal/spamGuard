package com.supptic.spamguard.model;

public class Analyse {

    private final String texte;
    private final ResponseAnalyse reponse;
    private final long horodatage;

    public Analyse(String texte, ResponseAnalyse reponse) {
        this.texte = texte;
        this.reponse = reponse;
        this.horodatage = System.currentTimeMillis();
    }

    public String getTexte() { return texte; }
    public ResponseAnalyse getReponse() { return reponse; }
    public long getHorodatage() { return horodatage; }
}
