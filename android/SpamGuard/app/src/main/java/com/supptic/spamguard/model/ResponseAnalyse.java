package com.supptic.spamguard.model;

import com.google.gson.annotations.SerializedName;

public class ResponseAnalyse {

    private boolean spam;
    private String label;

    @SerializedName("probabilite_spam")
    private double probabiliteSpam;

    public boolean isSpam() { return spam; }
    public String getLabel() { return label; }
    public double getProbabiliteSpam() { return probabiliteSpam; }

    /** Confiance dans le verdict, en % */
    public int getConfiance() {
        double p = spam ? probabiliteSpam : 1 - probabiliteSpam;
        return (int) Math.round(p * 100);
    }
}
