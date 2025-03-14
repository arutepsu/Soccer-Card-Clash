package view.gui.scenes

import controller.IController
import sceneManager.SceneManager
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneBar.*
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.utils.ImageUtils
import scalafx.stage.Stage
import view.gui.utils.Styles
import controller.Events
import scalafx.application.Platform
import view.gui.components.sceneBar.ButtonBar
import view.gui.components.sceneBar.cardBar.{PlayersFieldBar, PlayersHandBar, SelectablePlayersFieldBar}
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
case class PlayingFieldScene(
                              controller: IController,
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {

  this.getStylesheets.add(Styles.playingFieldCss)
  controller.add(this)

  if (controller.getPlayingField == null) {
    throw new IllegalStateException("PlayingFieldScene initialized before game was started!")
  }
  def playingField: IPlayingField = controller.getPlayingField
  def player1: IPlayer = controller.getPlayer1
  def player2: IPlayer = controller.getPlayer2


  val player1HandBar = new PlayersHandBar(player1, playingField, isLeftSide = true)
  val player2HandBar = new PlayersHandBar(player2, playingField, isLeftSide = false)
  val player1FieldBar = new PlayersFieldBar(player1, playingField)
  val player2FieldBar = new PlayersFieldBar(player2, playingField)

  def attacker: IPlayer = playingField.getRoles.attacker

  def defender: IPlayer = playingField.getRoles.defender


  val attackerHandBar = if (attacker == player1) player1HandBar else player2HandBar
  val defenderFieldBar = if (defender == player1) player1FieldBar else player2FieldBar

  val gameStatusBar = new GameStatusBar

  val playerFields = new HBox {
    alignment = Pos.CENTER
    spacing = 150
    children = Seq(defenderFieldBar)
  }

  val playerHands = new HBox {
    alignment = Pos.CENTER
    spacing = 300
    children = Seq(attackerHandBar)
  }

  val player1ScoreLabel = new Label {
    text = s"${player1.name} Score: ${playingField.getScores.getScorePlayer1}"
    styleClass.add("player-score-label")
  }

  val player2ScoreLabel = new Label {
    text = s"${player2.name} Score: ${playingField.getScores.getScorePlayer2}"
    styleClass.add("player-score-label")
  }
  val buttonBar = new ButtonBar(controller, playingField, this, gameStatusBar)


  val playersBar = new PlayersBar(controller)

  root = new StackPane {
    children = Seq(
      new HBox {
        alignment = Pos.CENTER_LEFT
        spacing = 20
        children = Seq(
          buttonBar,
          new VBox {
            padding = Insets(10)
            alignment = Pos.CENTER
            children = Seq(
              playersBar,
              gameStatusBar,
              playerFields,
              playerHands,
              player1ScoreLabel,
              player2ScoreLabel,
            )
          }
        )
      }
    )
  }
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => updateDisplay())
  }

  def updateDisplay(): Unit = {
    // ✅ Always get the latest playing field and players
    val currentPlayingField = controller.getPlayingField
    val currentPlayer1 = controller.getPlayer1
    val currentPlayer2 = controller.getPlayer2

    val attacker = currentPlayingField.getRoles.attacker
    val defender = currentPlayingField.getRoles.defender

    println(s"PF attacker: ${attacker.name}, PF defender: ${defender.name}")

    val newAttackerHandBar = if (attacker == currentPlayer1) player1HandBar else player2HandBar
    val newDefenderFieldBar = if (defender == currentPlayer1) player1FieldBar else player2FieldBar

    playerFields.children.clear()
    playerFields.children.add(newDefenderFieldBar)

    playerHands.children.clear()
    playerHands.children.add(newAttackerHandBar)

    playersBar.refreshOnRoleSwitch()

    val score1 = currentPlayingField.getScores.getScorePlayer1
    val score2 = currentPlayingField.getScores.getScorePlayer2

    player1ScoreLabel.text = s"${currentPlayer1.name} Score: $score1"
    player2ScoreLabel.text = s"${currentPlayer2.name} Score: $score2"

    // Highlight the leading player
    newAttackerHandBar.updateBar()
    newDefenderFieldBar.updateBar()
  }
}