# Soundbox Backend

Bienvenue dans le **backend** du projet **Soundbox**, une plateforme interactive permettant aux utilisateurs de créer, organiser et personnaliser leurs propres **boîtes à sons**.

Ce backend, développé en **Java Spring Boot**, gère toute la logique métier, les utilisateurs, la base de données et la communication avec un **serveur Python externe** pour la conversion audio.

Le frontend Angular est disponible ici : [**soundbox-frontend**](https://github.com/KhalilBDJ/soundbox-frontend)

---

## 🧩 Fonctionnalités principales

- 🔐 Gestion des utilisateurs (inscription, connexion, JWT / cookies)
- 📂 Gestion des soundboards et des sons par utilisateur
- 🗃️ Stockage des sons (via PostgreSQL + système de fichiers)
- ✂️ Fonctionnalité de **cropping audio**
- 🔗 Ajout de sons à partir de liens **YouTube, TikTok, Instagram**, etc.
- 🤖 **Communication avec un microservice Python** pour la conversion vidéo → audio

---

## 🛠️ Stack technique

- **Backend principal** : Java 23 + Spring Boot
- **Frameworks** : Spring Security, Spring Data JPA, Spring Web, Validation
- **Base de données** : PostgreSQL (via Docker recommandé)
- **Authentification** : JWT via vérification mail
- **Conversion audio** : serveur Python externe avec `yt-dlp`

---

## 🧱 Architecture

```bash
soundbox-backend/
│
├── controller/           # Points d'entrée API REST
├── service/              # Logique métier
├── model/                # Entités JPA (User, Sound, Soundboard...)
├── repository/           # Interfaces JPA
├── dto/                  # Objets de transfert de données
├── config/               # Configuration sécurité, CORS, etc.
└── utils/                # Méthodes d'upload, conversion, etc.
