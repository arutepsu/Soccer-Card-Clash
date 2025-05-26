![Logo](src/main/resources/images/data/logo/logoCut.png)
[![Coverage Status](https://coveralls.io/repos/github/arutepsu/Soccer-Card-Clash/badge.svg?branch=main&cachebust=1)](https://coveralls.io/github/arutepsu/Soccer-Card-Clash?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c8252a455c9a41f881a18a2e319642b1)](https://app.codacy.com/gh/arutepsu/Soccer-Card-Clash/dashboard)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/arutepsu/Soccer-Card-Clash?color=blue)
![Last commit](https://img.shields.io/github/last-commit/arutepsu/Soccer-Card-Clash?color=yellow)
[![Scala CI](https://github.com/arutepsu/Soccer-Card-Clash/actions/workflows/scala.yml/badge.svg)](https://github.com/arutepsu/Soccer-Card-Clash/actions/workflows/scala.yml)
![Repo size](https://img.shields.io/github/repo-size/arutepsu/Soccer-Card-Clash?color=orange)
![Scala](https://img.shields.io/badge/Scala-3.4.1-red?logo=scala)
![sbt](https://img.shields.io/badge/sbt-1.9.9-purple?logo=sbt)
![License](https://img.shields.io/github/license/arutepsu/Soccer-Card-Clash?color=lightgrey)

# Soccer Card Clash
An unofficial Scala version of the Soccer Card Clash game for Software Engineering classes at HTWG Konstanz.

Soccer Card Clash is a fast-paced, strategic 2-player card game where soccer meets tactical mind games.
Outmaneuver your opponent with clever attacks, boosts, and hand manipulation to score goals and win the match.

### Game Overview
* Each player controls a hand of soccer-themed player cards.
* Take turns as the attacker or defender.
* Outsmart your opponent by choosing the right action at the right time.
* Score goals by breaking through all defenders and beating the goalkeeper.

### Game Rules

* Players alternate roles each round.
* Attacker always uses the last card(s) in hand.
* Defender does not act — only endures the attack.
* Once all defender cards are defeated, the goalkeeper becomes the final line of defense.
* Beating the goalkeeper scores a goal and switches roles.


### Attack Types
#### Single Attack: 
* Uses the last card in the attacker’s hand.

#### Double Attack: 
* Uses the last two cards in the attacker’s hand.

### Attack Outcomes

#### Success:

* Beaten defender(s) are added to the end of the attacker's hand.

* Attacker continues attacking.

#### Failure:

* Roles switch — the defender becomes the new attacker.

#### Tie:

* The next-to-last cards are compared.

* The stronger combination wins the round.

### Boosting
* The attacker can boost their own defender cards or goalkeeper.

* Boosting increases card strength, based on the boosting card's value:

Boost Card	Boost Value
* Two	            +6
* Three	        +5
* Four	        +5
* Five	        +4
* Six	            +4
* Seven	        +3
* Eight	        +3
* Nine	        +2
* Ten	            +2
* Jack	        +1
* Queen	        +1
* King	        +1
* Ace	+0
* 
* Boosting counts as one action per turn.
* 
* If no actions remain, boosting is disabled and a warning is displayed.

### Swap Mechanics

#### Regular Swap
* Select a card from your hand.
* Swap it with the last card in your hand.

#### Reverse Swap
* Reverses the entire hand order instantly.

#### Notes on Actions
* Every swap (regular or reverse) consumes one action.
* Once the turn's actions are used up, no further actions can be performed.




### Project Status
![fun](https://img.shields.io/badge/Fun-100%25-brightgreen)

![AI](https://img.shields.io/badge/Learning_ai-maybe-blue)

![Immutable GameState](https://img.shields.io/badge/Immutable_GameState-Completed-brightgreen)

![Modular MVC Design](https://img.shields.io/badge/Modular_MVC_Design-Completed-brightgreen)

![DI (Guice)](https://img.shields.io/badge/DI_(Guice)-Completed-brightgreen)

![Event System](https://img.shields.io/badge/Event_System-Completed-brightgreen)

![Event System](https://img.shields.io/badge/Undo_Redo-Completed-brightgreen)

### Demo Game Creation with AI
![Demo](https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExbXUxNjk2OG03cDRzZnZ4NGlrZHQxdDBtbG8xdTI5ejJicTl6YWZ5bSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/NWgaxXgGqVNcwUO5AN/giphy.gif)
### Demo Gameplay
![Demo](https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExOHJiNnYzMzdnN3RnYnV4NTJxbnFhZGN1ZXRyY240czZhcXliZW1rMyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/RfqWgunekLvxE5SnEt/giphy.gif)

### Screenshots
![menu](src/main/resources/screenshots/mainmenu.png)
![singleplayer](src/main/resources/screenshots/singleplayer.png)
![choseai](src/main/resources/screenshots/choseai.png)
![playingfield](src/main/resources/screenshots/playingfield.png)
![comparison](src/main/resources/screenshots/comparison.png)
![fieldcards](src/main/resources/screenshots/fieldcards.png)
![info](src/main/resources/screenshots/info.png)
![handcards](src/main/resources/screenshots/handcards.png)
![pause](src/main/resources/screenshots/pause.png)
_Developed by [Arutepsu](httzps://github.com/arutepsu)_