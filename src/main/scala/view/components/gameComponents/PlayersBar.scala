package view.components.gameComponents

import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import controller.Controller
import view.components.GameLabel
import model.playerComponent.Player
import view.components.playerComponents.PlayerAvatar

import view.utils.Styles

class PlayersBar(
                  controller: Controller,
                  moveToGamePlayerScene: (index: Int) => Unit
                ) extends HBox {

  spacing = 10
  alignment = Pos.TOP_RIGHT
  this.getStylesheets.add(Styles.playersBarCss) // ✅ Load external CSS
  styleClass.add("players-bar") // ✅ Apply main styling

  updatePlayersDisplay()

  /** Updates the display when the current player (attacker) changes */
  def updatePlayersDisplay(): Unit = {
    children.clear()

    val playingField = controller.getPlayingField
    val attacker = playingField.getAttacker
    val defender = playingField.getDefender
    val players = List(attacker, defender)

    players.zipWithIndex.foreach { case (p, index) =>
      val newPlayer = p

      val playerAvatar = PlayerAvatar(
        player = newPlayer,
        playerIndex = index,
        scaleAvatar = 0.2,
        scaleFont = 0.1,
        profilePicturePath = s"/view/data/players/player${index + 1}.jpeg",
        onPlayerNameButtonClick = () => moveToGamePlayerScene(index)
      )

      val playerAvatarBox = new VBox {
        styleClass.add("player-avatar-box") // ✅ Apply avatar box styling
        spacing = 5
        padding = Insets(3)
        children = Seq(playerAvatar)
      }

      if (newPlayer == attacker) {
        val currentPlayerText = new GameLabel("Attacker", scalingFactor = 0.5)
        playerAvatarBox.children.add(currentPlayerText)
        playerAvatarBox.styleClass.add("current-player") // ✅ Apply highlight for attacker
      }

      children.add(playerAvatarBox)
    }
  }

  /** Call this method whenever roles switch */
  def refreshOnRoleSwitch(): Unit = {
    updatePlayersDisplay()
  }
}
