# Tranotsika - Application Android Native (Java)

Application mobile native Android de recherche et réservation de logements à louer, développée selon les maquettes des écrans **Accueil** et **Détail**.

---

## 📱 Fonctionnalités et Écrans

### 1. Écran Accueil (`MainActivity`)
- **En-tête de localisation** : Affichage dynamique de la localisation ("Abidjan, Côte d'Ivoire") avec bouton de notification.
- **Barre de recherche** : Recherche instantanée par mot-clé (nom, adresse, ville).
- **Filtres par Puces / Chips** : Filtrez les logements par type (*Appartement, Villa, Studio, Duplex*).
- **Liste "Recommandés pour vous"** : `RecyclerView` de `CardView` avec :
  - Illustration du bien avec angles arrondis.
  - Badge flottant du prix (ex: `250 000 Ar/mois`).
  - Bouton cœur / favori interactif avec persistance immédiate dans les fichiers XML.
  - Titre, adresse avec icône pin, et caractéristiques en bleu (`3 Ch. • 2 SdB • 140 m²`).
- **Barre de navigation inférieure** : 5 onglets (*Accueil*, *Favoris*, *Publier*, *Messages*, *Profil*). L'onglet *Favoris* filtre automatiquement les biens marqués comme suivis.

### 2. Écran Détail (`DetailActivity`)
- **Image Hero immersive** avec boutons flottants en en-tête : *Retour*, *Partager*, et *Favori*.
- **Titre & Prix** alignés sur la même ligne (ex: `Duplex Moderne Riviera` et `250k AR/m`).
- **Badges de caractéristiques** : *3 Chambres*, *2 SdB*, *120 m²*, *Meublé*.
- **Section Description** complète du logement.
- **Carte Profil de l'Agent** : Photo de l'agent (*Amadou Koné*), son statut (*Agent Certifié Fediko*), et aperçu du secteur.
- **Actions directes** :
  - **Contacter** : Lance un appel téléphonique vers l'agent.
  - **Réserver** : Envoie une demande de réservation avec notification instantanée.

---

## 🗄️ Stockage Local en XML (`XmlManager.java`)

Les données des logements et l'état des favoris sont stockés sous forme de fichiers XML dans le stockage interne de l'application (`context.getFilesDir()`) :
- `tranotsika_data.xml` : Contient l'intégralité du catalogue des logements.
- `favoris.xml` : Contient l'historique et les résumés des logements favoris.

L'analyseur utilisé est le **DOM Parser** (`DocumentBuilderFactory`, `Document`, `Element`, `Transformer`) :
- `chargerMaisons(Context)` : Parse le fichier XML et reconstruit la liste d'objets Java `Maison`.
- `setFavori(Context, String id, boolean estFavori)` : Recherche le nœud correspondant par ID, modifie la valeur de `<estFavori>` et réécrit le fichier XML de manière indentée.
- `chargerFavoris(Context)` : Retourne uniquement les logements suivis.
- `sauvegarderMaison(Context, Maison)` : Ajoute ou met à jour une maison dans le fichier XML.

---

## 🌐 Communication Réseau par Sockets (`SocketClient.java`)

Pour répondre aux contraintes réseau d'Android :
- La connexion TCP `java.net.Socket` est exécutée sur un thread d'arrière-plan via un **`ExecutorService`**, évitant strictement l'exception `NetworkOnMainThreadException`.
- Les résultats et événements sont retransmis à l'interface graphique grâce à un **`Handler(Looper.getMainLooper())`** et l'interface `SocketResponseCallback`.
- Possibilité d'échanger des requêtes/réponses aux formats **JSON** ou **XML**.
- Gestion des timeouts de connexion et de lecture (6000 ms).

---

## 📂 Structure du Projet

```
Tranotsika/
├── build.gradle
├── settings.gradle
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/tranotsika/app/
        │   ├── model/
        │   │   └── Maison.java
        │   ├── data/
        │   │   └── XmlManager.java
        │   ├── network/
        │   │   └── SocketClient.java
        │   └── ui/
        │       ├── MainActivity.java
        │       ├── DetailActivity.java
        │       └── MaisonAdapter.java
        └── res/
            ├── drawable/
            │   ├── ic_*.xml (Icônes vectorielles : pin, loupe, filtres, retour, etc.)
            │   ├── bg_*.xml (Styles de formes : capsules, badges de prix, boutons)
            │   └── house_sample_*.xml (Illustrations graphiques des logements)
            ├── layout/
            │   ├── activity_main.xml (Écran Accueil)
            │   ├── activity_detail.xml (Écran Détail)
            │   └── item_maison.xml (Carte de logement pour RecyclerView)
            └── values/
                ├── colors.xml (Palette de couleurs conforme à la maquette)
                ├── strings.xml
                └── themes.xml
```

---

## 🚀 Ouverture et Lancement dans Android Studio

1. Lancez **Android Studio**.
2. Cliquez sur **Open** et sélectionnez le dossier `Tranotsika`.
3. Laissez Gradle synchroniser les dépendances.
4. Lancez l'application sur un émulateur ou un smartphone physique Android.
