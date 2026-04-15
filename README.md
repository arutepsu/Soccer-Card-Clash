<p align="center">
<img src="src/main/resources/images/data/logo/logoCut.png" alt="Logo" height="400"/>
</p>

---

[![Coverage Status](https://coveralls.io/repos/github/arutepsu/Soccer-Card-Clash/badge.svg?branch=main)](https://coveralls.io/github/arutepsu/Soccer-Card-Clash?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c8252a455c9a41f881a18a2e319642b1)](https://app.codacy.com/gh/arutepsu/Soccer-Card-Clash/dashboard)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/arutepsu/Soccer-Card-Clash?color=blue)
![Last commit](https://img.shields.io/github/last-commit/arutepsu/Soccer-Card-Clash?color=yellow)
![Repo size](https://img.shields.io/github/repo-size/arutepsu/Soccer-Card-Clash?color=orange)
![License](https://img.shields.io/github/license/arutepsu/Soccer-Card-Clash?color=lightgrey)
---
# Soccer Card Clash

An unofficial Scala version of the Soccer Card Clash game for Software Engineering classes at 
Konstanz University of Applied Sciences.

🌐 **Web Version:** [Soccer-Card-Clash-Web](https://github.com/arutepsu/Soccer-Card-Clash-Web)

* 🎮 Soccer Card Clash is a fast-paced, strategic 2-player card game where soccer meets tactical mind games.
* 🧠 Outmaneuver your opponent with clever attacks, boosts, and hand manipulation to score goals and win the match.

---
## ⚽ Game Overview
* 🃏 Each player controls a hand of soccer-themed player cards.
* 🔄 Take turns as the attacker or defender.
* 🧠 Outsmart your opponent by choosing the right action at the right time.
* 🎯 Score goals by breaking through all defenders and beating the goalkeeper.

### 📖[Read Full Game Rules](src/main/resources/docs/GAMERULES.md)

---
<h2 style="text-align: center;">▶️ Demo Gameplay</h2>
<div style="display: flex; gap: 0px;">
  <img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExa3hiaTlobWpiZzM5NjRyb3k5Y2Zwb3BpczF2MXdwOXptOWU1MmpweCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/OjGkzFmRiVrOamBDoF/giphy.gif" alt="Demo Game Creation" height="237"/>
  <img src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExOHJiNnYzMzdnN3RnYnV4NTJxbnFhZGN1ZXRyY240czZhcXliZW1rMyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/RfqWgunekLvxE5SnEt/giphy.gif" alt="Demo Gameplay" height="237"/>
</div>

---
## ✨ Features
### 🤖 Singleplayer Mode
Play against various AI opponents, each with its own unique strategy and behavior.

### 🧠 Multiple AI Strategies
Challenge AIs with different difficulty levels and tactics — from cautious defenders to aggressive attackers.

### 🤝 Multiplayer Mode
Play with friends in local multiplayer matches.

### 🎨 Unique Cyberpunk Design
Dive into a cyberpunk-inspired world with bold visuals, neon effects, and a futuristic interface.

### ⚔️ Strategic Gameplay
Mix and match actions like Swap, Boost, and Double Attack to outsmart your opponent.

--- 

## 🧱 Architecture
This game is built using the Model-View-Controller (MVC) architectural pattern, ensuring a clean separation of concerns and modularity.

### 🧠 Model

The **model** contains the immutable core logic of the game:

- Card logic, roles, scores, and game state transitions
- Built with immutability for safe concurrency and functional testing
- Implements key design patterns like **Memento**, **Strategy**, and **State**

### 🖼️ View

Two synchronized views allow flexible interaction:

- 🖥️ **GUI** (ScalaFX) – interactive and animated graphical interface
- 📝 **TUI** – text-based interface ideal for fast testing and debugging
- Both views act as **observers** and update automatically via event dispatching

### 🎮 Controller

The **controller** coordinates between user input and model logic:

- Translates actions into executable **commands**
- Applies changes using an **UndoManager** and updates a shared `GameContext`
- Forwards emitted events to registered observers using a **Mediator** (`EventDispatcher`)

---

### 📐 Design Patterns

This project applies several classic design patterns to ensure **clean architecture**, **testability**, and **flexible extensibility** across the model, controller, and view layers.

### 👉 [See full documentation here](src/main/resources/docs/PATTERNS.md)

---
## 💻 Technologies Used
![Scala](https://img.shields.io/badge/Scala-3.4.1-red?logo=scala)
![sbt](https://img.shields.io/badge/sbt-1.9.9-purple?logo=sbt)
![ScalaFX](https://img.shields.io/badge/ScalaFX-22.0.0-violet?logo=scala)
![DI](https://img.shields.io/badge/DI-Google_Juice-orange?logo=sbt)
![Test](https://img.shields.io/badge/Test-ScalaTest%203.2.14-brightgreen)
![Docker](https://img.shields.io/badge/Docker%20-3.8-teal)
![AI](https://img.shields.io/badge/Images%20by-DeepAI-blue)

---

## 📸 Screenshots
![menu](src/main/resources/docs/screenshots/mainmenu.png)
![singleplayer](src/main/resources/docs/screenshots/singleplayer.png)
![choseai](src/main/resources/docs/screenshots/choseai.png)
![playingfield](src/main/resources/docs/screenshots/playingfield.png)
![comparison](src/main/resources/docs/screenshots/comparison.png)
![fieldcards](src/main/resources/docs/screenshots/fieldcards.png)
![info](src/main/resources/docs/screenshots/info.png)
![handcards](src/main/resources/docs/screenshots/handcards.png)
![pause](src/main/resources/docs/screenshots/pause.png)

---

# 🐞 Current Bugs to Fix

- [ ] Fix AI players to consistently continue attacking instead of stopping or idling; add fallback logic when they halt.
- [ ] Fix card comparison display when the card values are the same.
- [ ] Replace the Queen of Spades image with a proper `.png` version.
- [ ] Fix Docker.

---

# 🚀 Getting Started
Want to jump into the game? Just clone this repository and launch it locally:

```bash
git clone https://github.com/arutepsu/Soccer-Card-Clash.git
cd soccer-card-clash
```
# 🛠️ How to Use
This is a standard sbt project. Here are the most common commands:

### 🔨 Compile the project
```bash
sbt compile
```
### 🎮 Start the game
```bash
sbt run
```
### 🧪 Run tests
```bash
sbt test
```

---

### 📫 Get in Touch
Questions? Feedback? Found a bug?
Feel free to reach out via email: arutepsu@gmail.com
or open an issue right here on GitHub.

Let the Card Clash begin! ⚽🔥⚔️

---

#### _👨‍💻 Developed with passion by_ [Arutepsu](httzps://github.com/arutepsu)

---