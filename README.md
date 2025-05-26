<p align="center">
<img src="src/main/resources/images/data/logo/logoCut.png" alt="Logo" height="400"/>
</p>

[![Coverage Status](https://coveralls.io/repos/github/arutepsu/Soccer-Card-Clash/badge.svg?branch=main&cachebust=1)](https://coveralls.io/github/arutepsu/Soccer-Card-Clash?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c8252a455c9a41f881a18a2e319642b1)](https://app.codacy.com/gh/arutepsu/Soccer-Card-Clash/dashboard)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/arutepsu/Soccer-Card-Clash?color=blue)
![Last commit](https://img.shields.io/github/last-commit/arutepsu/Soccer-Card-Clash?color=yellow)
[![Scala CI](https://github.com/arutepsu/Soccer-Card-Clash/actions/workflows/scala.yml/badge.svg)](https://github.com/arutepsu/Soccer-Card-Clash/actions/workflows/scala.yml)
![Repo size](https://img.shields.io/github/repo-size/arutepsu/Soccer-Card-Clash?color=orange)
![Scala](https://img.shields.io/badge/Scala-3.4.1-red?logo=scala)
![sbt](https://img.shields.io/badge/sbt-1.9.9-purple?logo=sbt)
![License](https://img.shields.io/github/license/arutepsu/Soccer-Card-Clash?color=lightgrey)
---
# Soccer Card Clash
An unofficial Scala version of the Soccer Card Clash game for Software Engineering classes at HTWG Konstanz.

* Soccer Card Clash is a fast-paced, strategic 2-player card game where soccer meets tactical mind games.
* Outmaneuver your opponent with clever attacks, boosts, and hand manipulation to score goals and win the match.
---
### Game Overview
* Each player controls a hand of soccer-themed player cards.
* Take turns as the attacker or defender.
* Outsmart your opponent by choosing the right action at the right time.
* Score goals by breaking through all defenders and beating the goalkeeper.

[Read Full Game Rules](src/main/resources/docs/GAMERULES.md)

---

## 🧱 Architecture
This game is built using the Model-View-Controller (MVC) architectural pattern, ensuring a clean separation of concerns and modularity.

### 🔹Model

The model contains the immutable core logic of the game:

* Cards, roles, scores, and game state
* Designed for predictability, testability, and safe concurrent use

### 🔹 View
Two synchronized views provide flexible interaction:

* Text-based UI (TUI) for quick testing and interaction via console
* Graphical UI (GUI) built with ScalaFX, offering a richer user experience

### 🔹 Controller
The controller mediates between the model and views:

* Processes commands and applies changes to the game state
* Supports undo/redo functionality
* Dispatches events independently to both TUI and GUI observers



### Project Status
![fun](https://img.shields.io/badge/Fun-100%25-brightgreen)

![AI](https://img.shields.io/badge/Learning_ai-maybe-blue)

![Immutable GameState](https://img.shields.io/badge/Immutable_GameState-Completed-brightgreen)

![Modular MVC Design](https://img.shields.io/badge/Modular_MVC_Design-Completed-brightgreen)

![DI (Guice)](https://img.shields.io/badge/DI_(Guice)-Completed-brightgreen)

![Event System](https://img.shields.io/badge/Event_System-Completed-brightgreen)

![Event System](https://img.shields.io/badge/Undo_Redo-Completed-brightgreen)

<h2 style="text-align: center;">Demo Gameplay</h2>
<div style="display: flex; gap: 0px;">
  <img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExa3hiaTlobWpiZzM5NjRyb3k5Y2Zwb3BpczF2MXdwOXptOWU1MmpweCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/OjGkzFmRiVrOamBDoF/giphy.gif" alt="Demo Game Creation" height="237"/>
  <img src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExOHJiNnYzMzdnN3RnYnV4NTJxbnFhZGN1ZXRyY240czZhcXliZW1rMyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/RfqWgunekLvxE5SnEt/giphy.gif" alt="Demo Gameplay" height="237"/>
</div>


### Screenshots
![menu](src/main/resources/docs/screenshots/mainmenu.png)
![singleplayer](src/main/resources/docs/screenshots/singleplayer.png)
![choseai](src/main/resources/docs/screenshots/choseai.png)
![playingfield](src/main/resources/docs/screenshots/playingfield.png)
![comparison](src/main/resources/docs/screenshots/comparison.png)
![fieldcards](src/main/resources/docs/screenshots/fieldcards.png)
![info](src/main/resources/docs/screenshots/info.png)
![handcards](src/main/resources/docs/screenshots/handcards.png)
![pause](src/main/resources/docs/screenshots/pause.png)

_Developed by [Arutepsu](httzps://github.com/arutepsu)_