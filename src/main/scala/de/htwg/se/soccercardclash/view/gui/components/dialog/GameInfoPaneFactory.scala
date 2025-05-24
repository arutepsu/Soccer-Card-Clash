package de.htwg.se.soccercardclash.view.gui.components.dialog

import scalafx.geometry.Insets
import scalafx.scene.control.ScrollPane
import scalafx.scene.paint.Color
import scalafx.scene.text.{Text, TextFlow}
import scalafx.geometry.Insets
import scalafx.scene.control.ScrollPane
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.text.{Text, TextFlow}

object GameInfoPaneFactory {
  val generaltitleStyle = "     -fx-font-family: \"Rajdhani\";\n    -fx-font-size: 20px;\n    -fx-fill: #dddddd;\n    -fx-effect: dropshadow(gaussian, rgba(158, 75, 223, 0.8), 4, 0.3, 0, 1);\n    -fx-text-alignment: center;"
  val generalheaderStyle ="    -fx-font-family: \"Rajdhani\";\n    -fx-font-size: 26px;\n    -fx-font-weight: bold;\n    -fx-fill: linear-gradient(from 0% 0% to 100% 100%, #ff00cc, #83018e);\n    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 8, 0.4, 0, 2);\n    -fx-text-alignment: center;"
  def createMainInstructionsDialog(overlayWidth: Double, overlayHeight: Double): ScrollPane = new ScrollPane {

    content = new TextFlow {
      padding = Insets(20)
      lineSpacing = 8
      children = Seq(
        new Text("\nGoal:\n") {
          style = generalheaderStyle
        },
        new Text("Beat all of your opponent’s defenders and their goalkeeper to score a goal. The first player to score 3 goals wins the match.\n\n") {
          style =  generaltitleStyle
        },
        new Text("Gameplay Basics\n") {
          style = generalheaderStyle
        },
        new Text(
          "- Each turn, one player is the attacker, and the other is the defender.\n" +
            "- The attacker always uses the last card in their hand to attack.\n" +
            "- The defender cannot act — they only wait to see the outcome of the attack.\n" +
            "- You cannot attack the goalkeeper until all defender cards have been beaten.\n" +
            "- After the goalkeeper is beaten, a goal is scored, and roles are switched.\n\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Field View\n") {
          style = generalheaderStyle
        },
        new Text(
          "- You will see:\n" +
            "  - The attacker's hand cards.\n" +
            "  - The defender's field cards, which include:\n" +
            "    - Defender cards\n" +
            "    - Goalkeeper\n" +
            "- When roles switch, these views are flipped accordingly.\n\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Attack Rules\n") {
          style = generalheaderStyle
        },
        new Text(
          "- Single Attack: Uses the last card in the attacker’s hand.\n" +
            "- Double Attack: Uses the last two cards from the attacker’s hand.\n" +
            "- If the attack is successful:\n" +
            "  - The beaten defender(s) are added to the end of the attacker’s hand.\n" +
            "  - The attacker continues attacking.\n" +
            "- If the attack fails:\n" +
            "  - Roles switch — the defender becomes the attacker, and vice versa.\n" +
            "- If the cards are equal in value:\n" +
            "  - The next-to-last cards are compared.\n" +
            "  - The stronger pair wins the round.\n\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Buttons on the Playing Field\n") {
          style = generalheaderStyle
        },
        new Text(
          "- Show Defenders:\n" +
            "- Lets the attacker boost their own defender cards.\n" +
            "- Boosting changes the value of those cards.\n\n" +
            "- Make Swap:\n" +
            "- Lets the attacker reorder their hand cards.\n\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Field Cards Mechanics\n") {
          style = generalheaderStyle
        },
        new Text(
          "- When the field is initialized, defender cards are filtered, and the strongest card becomes the goalkeeper.\n" +
            "- After each attack:\n" +
            "  - If multiple defenders were beaten, the remaining field cards are re-evaluated.\n" +
            "  - The new strongest card becomes the goalkeeper automatically.\n"
        ) {
          style = generaltitleStyle
        }
      )
    }
    style = "-fx-background-color: transparent; -fx-background: transparent;"
    fitToWidth = true
    prefViewportWidth = overlayWidth * 0.8
    prefViewportHeight = overlayHeight * 0.6

    maxWidth = overlayWidth * 0.85
    maxHeight = overlayHeight * 0.7

    vbarPolicy = ScrollPane.ScrollBarPolicy.Never
    hbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
  }

  def createSwapInstructionsDialog(overlayWidth: Double, overlayHeight: Double): ScrollPane = new ScrollPane {

    content = new TextFlow {
      padding = Insets(20)
      lineSpacing = 8
      children = Seq(
        new Text("Regular Swap:\n") {
          style = generalheaderStyle
        },
        new Text(
            "- Select a card from your hand.\n" +
            "- Click the \"Swap\" button.\n" +
            "- The selected card will be swapped with the last card in your hand.\n"

        ) {
          style = generaltitleStyle
        },
        new Text("Reverse Swap\n") {
          style = generalheaderStyle
        },
        new Text(
            "  - Click the \"Reverse Swap\" button.\n" +
            "  - All cards in your hand will be reversed instantly.\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Action Tracker\n") {
          style = generalheaderStyle
        },
        new Text(
          "- At the top of the screen, you can view the number of actions remaining in your turn.\n" +
            "- Swapping (both regular and reverse) consumes one action, just like:\n" +
            "  - Boost\n" +
            "  - Defend\n" +
            "  - Double Attack\n" +
            "  - Roles switch — the defender becomes the attacker, and vice versa.\n" +
            "- If no actions are left, an alert will be shown and further swaps (or any other actions) will be blocked for the turn.\n"
        ) {
          style = generaltitleStyle
        },
          new Text("Use your actions strategically to turn the game in your favor!\n") {
            style = generalheaderStyle
          },
      )
    }
    style = "-fx-background-color: transparent; -fx-background: transparent;"
    fitToWidth = true
    prefViewportWidth = overlayWidth * 0.8
    prefViewportHeight = overlayHeight * 0.6

    maxWidth = overlayWidth * 0.85
    maxHeight = overlayHeight * 0.7

    vbarPolicy = ScrollPane.ScrollBarPolicy.Never
    hbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
  }

  def createBoostInstructionsDialog(overlayWidth: Double, overlayHeight: Double): ScrollPane = new ScrollPane {
    content = new TextFlow {
      padding = Insets(20)
      lineSpacing = 8
      children = Seq(
        new Text("Boosting:\n") {
          style = generalheaderStyle
        },
        new Text(
          "- You can boost either a defender or the goalkeeper.\n" +
            "- Boosting increases their strength based on the value of the boost card.\n" +
            "- Lower-valued cards give higher boosts.\n\n" +
            "  Boost Values:\n" +
            "  - Two → +6\n" +
            "  - Three → +5\n" +
            "  - Four → +5\n" +
            "  - Five → +4\n" +
            "  - Six → +4\n" +
            "  - Seven → +3\n" +
            "  - Eight → +3\n" +
            "  - Nine → +2\n" +
            "  - Ten → +2\n" +
            "  - Jack → +1\n" +
            "  - Queen → +1\n" +
            "  - King → +1\n" +
            "  - Ace → +0\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Note:\n") {
          style = generalheaderStyle
        },
        new Text(
          "- Boosting counts as one action.\n" +
            "- If no actions are left for your turn, boosting will be disabled and an alert will be shown.\n"
        ) {
          style = generaltitleStyle
        },
        new Text("Use boosts strategically to strengthen your defense and control the game!\n") {
          style = generalheaderStyle
        }
      )
    }

    style = "-fx-background-color: transparent; -fx-background: transparent;"
    fitToWidth = true
    prefViewportWidth = overlayWidth * 0.8
    prefViewportHeight = overlayHeight * 0.6
    maxWidth = overlayWidth * 0.85
    maxHeight = overlayHeight * 0.7
    vbarPolicy = ScrollPane.ScrollBarPolicy.Never
    hbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
  }
  def createGameInfoDialog(overlayWidth: Double, overlayHeight: Double): ScrollPane = new ScrollPane {
    content = new TextFlow {
      padding = Insets(20)
      lineSpacing = 8
      children = Seq(
        new Text("About the Game\n") {
          style = generalheaderStyle
        },
        new Text(
          """|**Soccer Card Clash** is a fast-paced, strategic 2-player card game where soccer meets tactical mind games!
             |
             |Each player takes turns attacking and defending using a hand of soccer-themed cards.
             |Every card represents a player with unique strengths. You must outmaneuver your opponent by choosing the right card at the right moment!
             |
             |Roles switch after each round: today's attacker becomes tomorrow's defender. Strategic use of boosts and goalkeeper plays can shift the tide in your favor.
             |
             |Victory goes to the player who scores the most successful attacks by the end of the match.
             |
             |Are you ready to kick off and clash?
             |""".stripMargin
        ) {
          style = generaltitleStyle
        }
      )
    }

    style = "-fx-background-color: transparent; -fx-background: transparent;"
    fitToWidth = true
    prefViewportWidth = overlayWidth * 0.8
    prefViewportHeight = overlayHeight * 0.6
    maxWidth = overlayWidth * 0.85
    maxHeight = overlayHeight * 0.7
    vbarPolicy = ScrollPane.ScrollBarPolicy.Never
    hbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
  }

}
