package com.supptic.spamguard.data;

public class Resultat<T> {
    public enum Etat { CHARGEMENT, SUCCES, ERREUR }

    public final Etat etat;
    public final T donnees;
    public final String message;

    private Resultat(Etat etat, T donnees, String message) {
        this.etat = etat; this.donnees = donnees; this.message = message;
    }

    public static <T> Resultat<T> chargement()       { return new Resultat<>(Etat.CHARGEMENT, null, null); }
    public static <T> Resultat<T> succes(T donnees)  { return new Resultat<>(Etat.SUCCES, donnees, null); }
    public static <T> Resultat<T> erreur(String msg) { return new Resultat<>(Etat.ERREUR, null, msg); }
}
