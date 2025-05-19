package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.*
import scalafx.scene.Node as FXNode

class PlayersBar(controller: IController, scene: PlayingFieldScene) extends HBox {

  spacing = 10
  alignment = Pos.TOP_CENTER
  this.getStylesheets.add(Styles.playersBarCss)
  styleClass.add("players-bar")
  private var actionsLabels: Map[IPlayer, Label] = Map()
  private var playerScoreLabels: Map[IPlayer, Label] = Map()
  private var playerBoxes: Seq[VBox] = Seq.empty

  updateBar()

//  def updateAttackerHighlight(): Unit = {
//    val currentDefender = scene.contextHolder.get.state.getRoles.defender
//
//    playerBoxes.foreach { vbox =>
//      vbox.styleClass.remove("current-player")
//
//      vbox.children.collectFirst {
//        case label: Label if label.text.value == currentDefender.name => label
//      } match {
//        case Some(_) => vbox.styleClass.add("current-player")
//        case None    => // ignore
//      }
//    }
//  }

  def updateBar(): Unit = {
    children.clear()
    actionsLabels = Map()
    playerScoreLabels = Map()
    playerBoxes = Seq.empty

    val playingField = scene.contextHolder.get.state
    val player1 = playingField.getRoles.attacker
    val player2 = playingField.getRoles.defender

    val avatar1 = PlayerAvatarRegistry.getAvatarView(player1, scale = 0.07f)
    val avatar2 = PlayerAvatarRegistry.getAvatarView(player2, scale = 0.07f)

    // Score labels
    val scoreLabel1 = new Label(s"${playingField.getScores.getScorePlayer1}") {
      styleClass += "player-score"
    }
    val scoreLabel2 = new Label(s"${playingField.getScores.getScorePlayer2}") {
      styleClass += "player-score"
    }
    playerScoreLabels += (player1 -> scoreLabel1)
    playerScoreLabels += (player2 -> scoreLabel2)

    // Action labels
    val actionLabel1 = new Label("") {
      styleClass += "player-actions"
    }
    val actionLabel2 = new Label("") {
      styleClass += "player-actions"
    }
    actionsLabels += (player1 -> actionLabel1)
    actionsLabels += (player2 -> actionLabel2)

    // Player name labels
    val nameLabel1 = new Label(player1.name) {
      styleClass += "player-name"
    }
    val nameLabel2 = new Label(player2.name) {
      styleClass += "player-name"
    }

    // Avatar boxes
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

    // Action + name boxes
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

    // Score-only box (centered between players)
    val scoreBox = new VBox {
      alignment = Pos.CENTER
      spacing = 5

      val scoresTitle = new Label("Scores") {
        styleClass += "scores-title" // Optional: for styling
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



    // Add to layout
    children.addAll(
      avatarBox1,
      actionBox1,
      scoreBox,
      actionBox2,
      avatarBox2
    )

    // Track for highlighting
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
    val roles = scene.contextHolder.get.state.getRoles
    playerScoreLabels.get(roles.attacker).foreach(_.text = s"${scores.getScorePlayer1}")
    playerScoreLabels.get(roles.defender).foreach(_.text = s"${scores.getScorePlayer2}")
  }

  def refreshOnRoleSwitch(): Unit = {
//    updateAttackerHighlight()
    refreshActionStates()
    refreshScores()
  }
}
