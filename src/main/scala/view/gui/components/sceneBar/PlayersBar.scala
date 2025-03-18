package view.gui.components.sceneView

import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import controller.IController
import view.gui.components.GameLabel
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.*
import view.gui.components.playerView.PlayerAvatar
import view.gui.utils.Styles
import scalafx.scene.control.Label
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.Insets
import scalafx.geometry.Pos


class PlayersBar(controller: IController) extends HBox {

  spacing = 10
  alignment = Pos.TOP_RIGHT
  this.getStylesheets.add(Styles.playersBarCss)
  styleClass.add("players-bar")

  // ✅ Store action labels for each player
  private var actionsLabels: Map[IPlayer, Label] = Map()

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
    actionsLabels = Map()

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
      
      val actionsLabel = new Label("") {
        styleClass.add("player-actions")
      }
      actionsLabels += (p -> actionsLabel)

      val playerAvatarBox = new VBox {
        styleClass.add("player-avatar-box")
        spacing = 5
        padding = Insets(3)
        children = Seq(playerAvatar, playerName, actionsLabel)
      }

      children.add(playerAvatarBox)
    }

    updateAttackerHighlight()
    refreshActionStates() 
  }

  def refreshActionStates(): Unit = {
    val playingField = controller.getCurrentGame.getPlayingField
    val players = List(playingField.getAttacker, playingField.getDefender)

    players.foreach { player =>
      actionsLabels.get(player).foreach { actionsLabel =>
        val remainingActions = player.actionStates.map {
          case (action, CanPerformAction(remainingUses)) => s"${action.toString}: $remainingUses"
          case (action, OutOfActions) => s"${action.toString}: 0"
        }
        actionsLabel.text = remainingActions.mkString("\n") // ✅ Now updates dynamically!
      }
    }
  }

  def refreshOnRoleSwitch(): Unit = {
    updateAttackerHighlight()
    refreshActionStates()
  }
}
