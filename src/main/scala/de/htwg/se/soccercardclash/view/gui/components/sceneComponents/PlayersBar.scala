package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.{Insets, Pos}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.scene.control.Label
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, VBox}
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.Scene
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameLabel
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene


class PlayersBar(controller: IController, scene: PlayingFieldScene) extends HBox {

  spacing = 10
  alignment = Pos.TOP_RIGHT
  this.getStylesheets.add(Styles.playersBarCss)
  styleClass.add("players-bar")

  private var actionsLabels: Map[IPlayer, Label] = Map()
  private var playerScoreLabels: Map[IPlayer, Label] = Map()

  updateBar()

  def updateAttackerHighlight(): Unit = {
    val currentDefender = scene.contextHolder.get.state.getRoles.defender

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
    playerScoreLabels = Map()

    val playingField = scene.contextHolder.get.state
    val player1 = playingField.getRoles.attacker
    val player2 = playingField.getRoles.defender

    val players = List(player1, player2)

    players.foreach { p =>
      val avatarView = PlayerAvatarRegistry.getAvatarView(p, scale = 0.07f)

      val playerName = new GameLabel(p.name, scalingFactor = 0.5)

      val actionsLabel = new Label("") {
        styleClass.add("player-actions")
      }
      actionsLabels += (p -> actionsLabel)

      val scoreValue = if (p eq player1)
        playingField.getScores.getScorePlayer1
      else
        playingField.getScores.getScorePlayer2

      val scoreLabel = new Label(s"Score: $scoreValue") {
        styleClass.add("player-score")
      }
      playerScoreLabels += (p -> scoreLabel)

      val playerAvatarBox = new VBox {
        styleClass.add("player-avatar-box")
        spacing = 2
        padding = Insets(1)
        alignment = Pos.TOP_CENTER
        children = Seq(avatarView, playerName, scoreLabel, actionsLabel)
      }

      children.add(playerAvatarBox)
    }

    updateAttackerHighlight()
    refreshActionStates()
  }

  def refreshActionStates(): Unit = {
    val playingField = scene.contextHolder.get.state
    val players = List(playingField.getRoles.attacker, playingField.getRoles.defender)

    players.foreach { player =>
      actionsLabels.get(player).foreach { actionsLabel =>
        val remainingActions = player.actionStates.map {
          case (action, CanPerformAction(remainingUses)) => s"${action.toString}: $remainingUses"
          case (action, OutOfActions) => s"${action.toString}: 0"
        }
        actionsLabel.text = remainingActions.mkString("\n")
      }
    }
  }

  def refreshScores(): Unit = {
    val scores = scene.contextHolder.get.state.getScores
    playerScoreLabels.get(scene.contextHolder.get.state.getRoles.attacker)
      .foreach(_.text = s"Score: ${scores.getScorePlayer1}")
    playerScoreLabels.get(scene.contextHolder.get.state.getRoles.defender)
      .foreach(_.text = s"Score: ${scores.getScorePlayer2}")
  }

  def refreshOnRoleSwitch(): Unit = {
    updateAttackerHighlight()
    refreshActionStates()
    refreshScores()
  }
}
