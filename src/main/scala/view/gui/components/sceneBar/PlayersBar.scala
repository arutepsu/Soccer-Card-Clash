package view.gui.components.sceneView

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
  this.getStylesheets.add(Styles.playersBarCss)
  styleClass.add("players-bar")

  updateBar()
  
  def updateAttackerHighlight(): Unit = {
    val currentDefender = controller.getCurrentGame.getPlayingField.getDefender

    children.foreach {
      case node: javafx.scene.layout.VBox =>
        node.getStyleClass.remove("current-player")
        
        node.getChildren.toArray.collectFirst {
          case label: javafx.scene.control.Label if label.getText == currentDefender.name => label
        } match {
          case Some(_) => node.getStyleClass.add("current-player")
          case None => 
        }
    }
  }


  def updateBar(): Unit = {
    children.clear()

    val playingField = controller.getCurrentGame.getPlayingField
    val player1 = playingField.getAttacker
    val player2 = playingField.getDefender

    val players = List(player1, player2)

    players.foreach { p =>
      var profilePicturePath = if (p eq player1) {
        "/images/data/players/player1.jpeg"
      } else {
        "/images/data/players/player2.jpeg"
      }

      val playerAvatar = PlayerAvatar(
        player = p,
        playerIndex = if (p eq player1) 0 else 1,
        scaleAvatar = 0.2,
        scaleFont = 0.1,
        profilePicturePath = profilePicturePath,
      )

      val playerName = new GameLabel(p.name, scalingFactor = 0.5)

      val playerAvatarBox = new VBox {
        styleClass.add("player-avatar-box")
        spacing = 5
        padding = Insets(3)
        children = Seq(playerAvatar, playerName)
      }

      children.add(playerAvatarBox)
    }

    updateAttackerHighlight()
  }


  def refreshOnRoleSwitch(): Unit = {
    updateAttackerHighlight()
  }
}
