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
  def create(overlayWidth: Double, overlayHeight: Double): ScrollPane = new ScrollPane {

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
}
