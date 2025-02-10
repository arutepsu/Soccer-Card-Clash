package view.components.gameComponents

import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import controller.Controller
import view.components.GameLabel
import model.playerComponent.Player
import view.components.playerComponents.PlayerAvatar

class PlayersBar(
                  controller: Controller,
                  moveToGamePlayerScene: (index: Int) => Unit
                ) extends HBox {

  spacing = 10
  alignment = Pos.TOP_RIGHT
  updatePlayersDisplay()

  /** Updates the display when the current player (attacker) changes */
  def updatePlayersDisplay(): Unit = {
    children.clear() // Clear existing UI elements

    val playingField = controller.getPlayingField
    val attacker = playingField.getAttacker
    val defender = playingField.getDefender

    val players = List(attacker, defender)

    players.zipWithIndex.foreach { case (p, index) =>
      val newPlayer = p // Assign `p` to a local variable

      val playerAvatar = PlayerAvatar(
        player = newPlayer,
        playerIndex = index,
        scaleAvatar = 0.2,  // ✅ Adjusted avatar scale to make images smaller
        scaleFont = 0.1,    // ✅ Slightly reduce font size as well for better proportions
        profilePicturePath = s"/view/data/players/player${index + 1}.jpeg", // ✅ Set profile image
        onPlayerNameButtonClick = () => moveToGamePlayerScene(index)
      )

      val playerAvatarBox = new VBox {
        spacing = 5
        padding = Insets(3)
        children = Seq(playerAvatar)
      }

      // Highlight attacker as "Current Player"
      if (newPlayer == attacker) {
        val currentPlayerText = new GameLabel("Attacker", scalingFactor = 0.5)
        playerAvatarBox.children.add(currentPlayerText)
        playerAvatarBox.styleClass.add("current-player")
      }

      children.add(playerAvatarBox)
    }
  }

  /** Call this method whenever roles switch */
  def refreshOnRoleSwitch(): Unit = {
    updatePlayersDisplay()
  }
}
