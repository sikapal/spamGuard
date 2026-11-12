package com.supptic.spamguard.model;

import com.google.gson.annotations.SerializedName;

public class InfosModele {

    private String algorithme;
    private double exactitude;
    @SerializedName("precision_spam")    private double precisionSpam;
    @SerializedName("rappel_spam")       private double rappelSpam;
    @SerializedName("f1_spam")           private double f1Spam;
    @SerializedName("nb_entrainement")   private int nbEntrainement;
    @SerializedName("nb_test")           private int nbTest;
    @SerializedName("date_entrainement") private String dateEntrainement;

    public String getAlgorithme() { return algorithme; }
    public double getExactitude() { return exactitude; }
    public double getPrecisionSpam() { return precisionSpam; }
    public double getRappelSpam() { return rappelSpam; }
    public double getF1Spam() { return f1Spam; }
    public int getNbEntrainement() { return nbEntrainement; }
    public int getNbTest() { return nbTest; }
    public String getDateEntrainement() { return dateEntrainement; }
}
