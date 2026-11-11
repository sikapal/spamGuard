===== Naive Bayes =====
              precision    recall  f1-score   support

         ham      0.987     0.999     0.993       966
        spam      0.993     0.913     0.951       149

    accuracy                          0.987      1115
   macro avg      0.990     0.956     0.972      1115
weighted avg      0.988     0.987     0.987      1115

Matrice de confusion (lignes = réalité, colonnes = prédiction) [ham, spam] :
[[965   1]
 [ 13 136]]

===== Régression logistique =====
              precision    recall  f1-score   support

         ham      0.990     0.991     0.990       966
        spam      0.939     0.933     0.936       149

    accuracy                          0.983      1115
   macro avg      0.964     0.962     0.963      1115
weighted avg      0.983     0.983     0.983      1115

Matrice de confusion (lignes = réalité, colonnes = prédiction) [ham, spam] :
[[957   9]
 [ 10 139]]
