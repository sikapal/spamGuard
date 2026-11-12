package com.supptic.spamguard.model;

import com.google.gson.annotations.SerializedName;

public class ReponseAnalyse {

    private boolean spam;
    private String label;

    @SerializedName("probabilite_spam")
    private double probabiliteSpam;

    public boolean isSpam() { return spam; }
    public String getLabel() { return label; }
    public double getProbabiliteSpam() { return probabiliteSpam; }

    /** Confiance dans le verdict, en % : 0.97 de spam -> 97 ; 0.03 de spam -> 97 aussi. */
    public int getConfiance() {
        double p = spam ? probabiliteSpam : 1 - probabiliteSpam;
        return (int) Math.round(p * 100);
    }
}
