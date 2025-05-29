package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node as FXNode
import scalafx.scene.control.Label
import scalafx.scene.layout.*

class PlayersBar(controller: IController, scene: PlayingFieldScene) extends HBox {

  spacing = 10
  alignment = Pos.TOP_CENTER
  this.getStylesheets.add(Styles.playersBarCss)
  styleClass.add("players-bar")
  private var actionsLabels: Map[IPlayer, Label] = Map()
  private var playerScoreLabels: Map[IPlayer, Label] = Map()
  private var playerBoxes: Seq[VBox] = Seq.empty

  updateBar()

  def updateBar(): Unit = {
    children.clear()
    actionsLabels = Map()
    playerScoreLabels = Map()
    playerBoxes = Seq.empty

    val playingField = scene.contextHolder.get.state
    val attacker = playingField.getRoles.attacker
    val defender = playingField.getRoles.defender

    val avatar1 = PlayerAvatarRegistry.getAvatarView(attacker, scale = 0.07f)
    val avatar2 = PlayerAvatarRegistry.getAvatarView(defender, scale = 0.07f)

    val scoreLabel1 = new Label(s"${playingField.getScores.getScore(attacker)}") {
      styleClass += "player-score"
    }
    val scoreLabel2 = new Label(s"${playingField.getScores.getScore(defender)}") {
      styleClass += "player-score"
    }

    playerScoreLabels += (attacker -> scoreLabel1)
    playerScoreLabels += (defender -> scoreLabel2)

    val actionLabel1 = new Label("") {
      styleClass += "player-actions"
    }
    val actionLabel2 = new Label("") {
      styleClass += "player-actions"
    }
    actionsLabels += (attacker -> actionLabel1)
    actionsLabels += (defender -> actionLabel2)

    val nameLabel1 = new Label(attacker.name) {
      styleClass += "player-name"
    }
    val nameLabel2 = new Label(defender.name) {
      styleClass += "player-name"
    }

    val avatarBox1 = new VBox {
      styleClass += "player-avatar-box"
      spacing = 5
      alignment = Pos.CENTER
      children = Seq(avatar1)
    }

    val avatarBox2 = new VBox {
      styleClass += "player-avatar-box"
      spacing = 5
      alignment = Pos.CENTER
      children = Seq(avatar2)
    }

    val actionBox1 = new VBox {
      alignment = Pos.CENTER
      spacing = 5
      children = Seq(nameLabel1, actionLabel1)
    }
    val actionBox2 = new VBox {
      alignment = Pos.CENTER
      spacing = 5
      children = Seq(nameLabel2, actionLabel2)
    }

    val scoreBox = new VBox {
      alignment = Pos.CENTER
      spacing = 5

      val scoresTitle = new Label("Scores") {
        styleClass += "scores-title"
      }

      val spacer = new Region {
        HBox.setHgrow(this, Priority.ALWAYS)
      }

      val scoreRow = new HBox {
        alignment = Pos.CENTER
        spacing = 20
        children = Seq(scoreLabel1, spacer, scoreLabel2)
      }

      children = Seq(scoresTitle, scoreRow)
    }

    children.addAll(
      avatarBox1,
      actionBox1,
      scoreBox,
      actionBox2,
      avatarBox2
    )

    playerBoxes = Seq(avatarBox1, avatarBox2)

    refreshActionStates()
  }

  def refreshActionStates(): Unit = {
    val playingField = scene.contextHolder.get.state
    val players = List(playingField.getRoles.attacker, playingField.getRoles.defender)

    players.foreach { player =>
      actionsLabels.get(player).foreach { label =>
        val lines = player.actionStates.map {
          case (action, CanPerformAction(n)) => s"${action.toString}: $n"
          case (action, OutOfActions)        => s"${action.toString}: 0"
        }
        label.text = lines.mkString("\n")
      }
    }
  }

  def refreshScores(): Unit = {
    val scores = scene.contextHolder.get.state.getScores
    playerScoreLabels.foreach { case (player, label) =>
      label.text = s"${scores.getScore(player)}"
    }
  }
  def refreshOnRoleSwitch(): Unit = {
    refreshActionStates()
    refreshScores()
  }
}
