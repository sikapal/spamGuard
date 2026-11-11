
import json
from pathlib import Path
from typing import Annotated

import joblib
from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from pydantic import BaseModel, StringConstraints

# ---------------------------------------------------------------
# Chargement du modèle UNE seule fois, au démarrage du serveur
# ---------------------------------------------------------------
DOSSIER_MODELE = Path(__file__).resolve().parent.parent / "ml" / "modele"
modele = joblib.load(DOSSIER_MODELE / "spam_model.joblib")
metriques = json.loads((DOSSIER_MODELE / "metrics.json").read_text(encoding="utf-8"))
INDEX_SPAM = list(modele.classes_).index("spam")

app = FastAPI(title="SpamGuard API", version="1.0",
              description="Détection de SMS frauduleux (TP Sup'Ptic)")


# ---------------------------------------------------------------
# Format des données échangées (= le contrat d'API)
# ---------------------------------------------------------------
class RequeteAnalyse(BaseModel):
    texte: Annotated[str, StringConstraints(strip_whitespace=True, min_length=1, max_length=1000)]


class ReponseAnalyse(BaseModel):
    spam: bool
    label: str
    probabilite_spam: float


# ---------------------------------------------------------------
# Erreur 422 avec un message lisible (même format que MiniShop)
# ---------------------------------------------------------------
@app.exception_handler(RequestValidationError)
async def erreur_validation(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=422,
        content={"status": 422,
                 "message": "Le texte doit contenir entre 1 et 1000 caractères."})


# ---------------------------------------------------------------
# Routes
# ---------------------------------------------------------------
@app.get("/api/sante")
def sante():
    return {"statut": "ok"}


@app.get("/api/modele")
def infos_modele():
    return metriques


@app.post("/api/analyser", response_model=ReponseAnalyse)
def analyser(requete: RequeteAnalyse):
    p_spam = float(modele.predict_proba([requete.texte])[0][INDEX_SPAM])
    est_spam = p_spam >= 0.5
    return ReponseAnalyse(
        spam=est_spam,
        label="spam" if est_spam else "ham",
        probabilite_spam=round(p_spam, 4),
    )
