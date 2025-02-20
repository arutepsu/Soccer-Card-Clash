package view.gui.components.sceneBar

import scalafx.scene.layout.HBox
import scalafx.scene.control.Label
import scalafx.geometry.Pos
import scalafx.scene.paint.Color
import scalafx.scene.effect.DropShadow

class GameStatusBar extends HBox {

  alignment = Pos.CENTER
  spacing = 10

  private val statusLabel = new Label {
    text = GameStatusMessages.GAME_STARTED
    style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;"
  }

  children.add(statusLabel)

  /** Updates the game status message dynamically */
  def updateStatus(message: String, attacker: String = "", defender: String = ""): Unit = {
    // Replace placeholders with actual names if provided
    val formattedMessage = message.replace("{attacker}", attacker).replace("{defender}", defender)

    statusLabel.text = formattedMessage
    statusLabel.effect = new DropShadow(10, Color.YELLOW) // Highlight effect for visibility
  }
}
