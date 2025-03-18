package view.gui.scenes

import controller.{AttackResultEvent, ComparedCardsEvent, DoubleComparedCardsEvent, DoubleTieComparisonEvent, Events, IController, TieComparisonEvent}
import sceneManager.SceneManager
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneView.*
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.utils.ImageUtils
import scalafx.stage.Stage
import view.gui.utils.Styles
import scalafx.application.Platform
import view.gui.components.sceneView.ButtonBar
import view.gui.components.sceneView.cardBar.{PlayersFieldBar, PlayersHandBar, SelectablePlayersFieldBar}
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
import model.cardComponent.ICard
import scalafx.scene.Node
import scalafx.animation.FadeTransition
import scalafx.util.Duration
import view.gui.components.comparison.ComparisonHandler
import view.gui.overlay.Overlay

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayingFieldScene(
                         controller: IController,
                         windowWidth: Double,
                         windowHeight: Double,
                       ) extends Scene(windowWidth, windowHeight) with Observer {
  this.getStylesheets.add(Styles.playingFieldCss)

  println(s"✅ PlayingFieldScene registered as observer.")
  if (controller.getCurrentGame.getPlayingField == null) {
    throw new IllegalStateException("PlayingFieldScene initialized before game was started!")
  }

  def playingField: IPlayingField = controller.getCurrentGame.getPlayingField
  playingField.add(this)
  def player1: IPlayer = controller.getCurrentGame.getPlayer1

  def player2: IPlayer = controller.getCurrentGame.getPlayer2


  val player1HandBar = new PlayersHandBar(player1, playingField, isLeftSide = true)
  val player2HandBar = new PlayersHandBar(player2, playingField, isLeftSide = false)
  var player1FieldBar = new PlayersFieldBar(player1, playingField)
  var player2FieldBar = new PlayersFieldBar(player2, playingField)


  def attacker: IPlayer = playingField.getRoles.attacker

  def defender: IPlayer = playingField.getRoles.defender


  val attackerHandBar = if (attacker == player1) player1HandBar else player2HandBar
  val defenderFieldBar = if (defender == player1) player1FieldBar else player2FieldBar

  val gameStatusBar = new GameStatusBar

  val overlay = new Overlay(this)
  private val comparisonHandler = new ComparisonHandler(controller, overlay, updateDisplay)

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

//  root = new StackPane {
//        children = Seq(
//          new HBox {
//            alignment = Pos.CENTER_LEFT
//            spacing = 20
//            children = Seq(
//              buttonBar,
//              new VBox {
//                padding = Insets(10)
//                alignment = Pos.CENTER
//                children = Seq(
//                  playersBar,
//                  gameStatusBar,
//                  playerFields,
//                  playerHands,
//                  player1ScoreLabel,
//                  player2ScoreLabel,
//                )
//              }
//            )
//          }
//        )
//      }

  val mainLayout = new StackPane {
    children = Seq(
      new HBox {
        alignment = Pos.CENTER_LEFT
        children = Seq(
          buttonBar,
          new VBox {
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
      },
      overlay.getPane
    )
  }

  root = mainLayout

  private var lastAttackingCard: Option[ICard] = None
  private var lastAttackingCard1: Option[ICard] = None
  private var lastAttackingCard2: Option[ICard] = None
  private var lastDefendingCard: Option[ICard] = None
  private var lastExtraAttackerCard: Option[ICard] = None
  private var lastExtraDefenderCard: Option[ICard] = None
  private var lastAttackSuccess: Option[Boolean] = None

  override def update(e: ObservableEvent): Unit = {
    println(s"✅ PlayingFieldScene received event: $e")
    comparisonHandler.handleComparisonEvent(e)
    e match {
      case Events.MainMenu =>
        if (SceneManager.currentScene.contains(SceneManager.sceneRegistry.getMainMenuScene)) {
          return
        }
        controller.remove(this)
        SceneManager.update(e)

      case Events.Undo | Events.Redo | Events.BoostDefender | Events.BoostGoalkeeper | Events.RegularSwap | Events.CircularSwap =>
        updateDisplay()

      case _ =>

    }
  }
  private def updateDisplayAfterOverlay(): Unit = {
    Future {
      Thread.sleep(3000)
      Platform.runLater(() => updateDisplay())
    }
  }


  def updateDisplay(): Unit = {

    val currentPlayingField = controller.getCurrentGame.getPlayingField

    if (currentPlayingField == null || currentPlayingField.getDataManager.getPlayerGoalkeeper(
      controller.getCurrentGame.getPlayer1).isEmpty) {
      return
    }

    val currentPlayer1 = controller.getCurrentGame.getPlayer1
    val currentPlayer2 = controller.getCurrentGame.getPlayer2

    val attacker = currentPlayingField.getRoles.attacker
    val defender = currentPlayingField.getRoles.defender

    player1FieldBar = new PlayersFieldBar(currentPlayer1, currentPlayingField)
    player2FieldBar = new PlayersFieldBar(currentPlayer2, currentPlayingField)
    player1FieldBar.updateBar()
    player2FieldBar.updateBar()
    val newAttackerHandBar = if (attacker == currentPlayer1) player1HandBar else player2HandBar
    val newDefenderFieldBar = if (defender == currentPlayer1) player1FieldBar else player2FieldBar

    playerFields.children.clear()
    playerFields.children.add(newDefenderFieldBar)

    playerHands.children.clear()
    playerHands.children.add(newAttackerHandBar)
    newAttackerHandBar.updateBar()
    newDefenderFieldBar.updateBar()

    playersBar.refreshOnRoleSwitch()

    val score1 = currentPlayingField.getScores.getScorePlayer1
    val score2 = currentPlayingField.getScores.getScorePlayer2

    player1ScoreLabel.text = s"${currentPlayer1.name} Score: $score1"
    player2ScoreLabel.text = s"${currentPlayer2.name} Score: $score2"

  }

  private def resetLastCards(): Unit = {
    lastAttackingCard = None
    lastAttackingCard1 = None
    lastAttackingCard2 = None
    lastDefendingCard = None
    lastExtraAttackerCard = None
    lastExtraDefenderCard = None
    lastAttackSuccess = None
  }
}

