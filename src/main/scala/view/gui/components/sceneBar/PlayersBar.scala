package view.gui.components.sceneBar

import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import controller.IController
import view.gui.components.GameLabel
import model.playerComponent.IPlayer
import view.gui.components.playerView.PlayerAvatar

import view.gui.utils.Styles

class PlayersBar(
                  controller: IController,
                ) extends HBox {

  spacing = 10
  alignment = Pos.TOP_RIGHT
  this.getStylesheets.add(Styles.playersBarCss) // ✅ Load external CSS
  styleClass.add("players-bar") // ✅ Apply main styling
  var attacker : IPlayer = _
  var defender : IPlayer = _
  updateBar()

  /** Updates the display when the current player (attacker) changes */
  def updateBar(): Unit = {
    children.clear()

    val playingField = controller.getPlayingField
    attacker = playingField.getAttacker
    defender = playingField.getDefender
    val players = List(attacker, defender)

    players.zipWithIndex.foreach { case (p, index) =>
      val newPlayer = p

      val playerAvatar = PlayerAvatar(
        player = newPlayer,
        playerIndex = index,
        scaleAvatar = 0.2,
        scaleFont = 0.1,
        profilePicturePath = s"/images/data/players/player${index + 1}.jpeg",
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
    updateBar()
  }
}
