"""Entraîne le détecteur de SMS spam et sauvegarde le meilleur modèle."""
import csv
import json
from datetime import datetime
from pathlib import Path

import joblib
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (accuracy_score, classification_report, confusion_matrix,
                             f1_score, precision_score, recall_score)
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline

DOSSIER = Path(__file__).resolve().parent
FICHIER_DATA = DOSSIER / "data" / "SMSSpamCollection"
DOSSIER_MODELE = DOSSIER / "modele"

# ---------------------------------------------------------------
# 1. Charger et explorer
# ---------------------------------------------------------------
df = pd.read_csv(FICHIER_DATA, sep="\t", header=None, names=["label", "texte"],
                 quoting=csv.QUOTE_NONE, encoding="utf-8")

print(f"{len(df)} messages chargés")
print(df["label"].value_counts(normalize=True).round(3))
print(df.sample(5, random_state=1).to_string())

# ---------------------------------------------------------------
# 2. Séparer entraînement (80 %) / test (20 %)
#    stratify : même proportion de spams dans les deux parties
# ---------------------------------------------------------------
X_train, X_test, y_train, y_test = train_test_split(
    df["texte"], df["label"], test_size=0.2, random_state=42, stratify=df["label"])

# ---------------------------------------------------------------
# 3. Deux modèles candidats (même TF-IDF, classifieur différent)
# ---------------------------------------------------------------
candidats = {
    "Naive Bayes": Pipeline([
        ("tfidf", TfidfVectorizer(lowercase=True, ngram_range=(1, 2), min_df=2)),
        ("clf", MultinomialNB(alpha=0.1)),
    ]),
    "Régression logistique": Pipeline([
        ("tfidf", TfidfVectorizer(lowercase=True, ngram_range=(1, 2), min_df=2)),
        ("clf", LogisticRegression(max_iter=1000, class_weight="balanced")),
    ]),
}

resultats = {}
for nom, modele in candidats.items():
    modele.fit(X_train, y_train)
    y_pred = modele.predict(X_test)
    resultats[nom] = (modele, y_pred, f1_score(y_test, y_pred, pos_label="spam"))

    print(f"\n===== {nom} =====")
    print(classification_report(y_test, y_pred, digits=3))
    print("Matrice de confusion (lignes = réalité, colonnes = prédiction) [ham, spam] :")
    print(confusion_matrix(y_test, y_pred, labels=["ham", "spam"]))

# ---------------------------------------------------------------
# 4. Garder le modèle qui a le meilleur F1 sur la classe spam
# ---------------------------------------------------------------
meilleur_nom = max(resultats, key=lambda n: resultats[n][2])
meilleur, y_pred, _ = resultats[meilleur_nom]
print(f"\n>>> Modèle retenu : {meilleur_nom}")

# ---------------------------------------------------------------
# 5. Sauvegarder le modèle et ses métriques
# ---------------------------------------------------------------
DOSSIER_MODELE.mkdir(exist_ok=True)
joblib.dump(meilleur, DOSSIER_MODELE / "spam_model.joblib")

metriques = {
    "algorithme": meilleur_nom,
    "exactitude": round(float(accuracy_score(y_test, y_pred)), 4),
    "precision_spam": round(float(precision_score(y_test, y_pred, pos_label="spam")), 4),
    "rappel_spam": round(float(recall_score(y_test, y_pred, pos_label="spam")), 4),
    "f1_spam": round(float(f1_score(y_test, y_pred, pos_label="spam")), 4),
    "nb_entrainement": len(X_train),
    "nb_test": len(X_test),
    "date_entrainement": datetime.now().strftime("%Y-%m-%d %H:%M"),
}
(DOSSIER_MODELE / "metrics.json").write_text(
    json.dumps(metriques, ensure_ascii=False, indent=2), encoding="utf-8")
print(json.dumps(metriques, ensure_ascii=False, indent=2))

# ---------------------------------------------------------------
# 6. Essai sur des messages jamais vus
# ---------------------------------------------------------------
index_spam = list(meilleur.classes_).index("spam")
exemples = [
    "WINNER!! You have won a 1000 cash prize. Call 09061701461 now to claim.",
    "Hey, are we still meeting at the library at 5pm?",
    "URGENT: your account is blocked. Send your PIN to unlock it.",
    "Can you buy some bread on your way home please",
]
print("\nP(spam)  Message")
for texte in exemples:
    p_spam = meilleur.predict_proba([texte])[0][index_spam]
    print(f"{p_spam:6.1%}  {texte}")
