package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.util.{AttackResultEvent, ComparedCardsEvent, DoubleComparedCardsEvent, DoubleTieComparisonEvent, Events, GameOver, ScoreEvent, NoDoubleAttacksEvent, ObservableEvent, Observer, TieComparisonEvent}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{ButtonBar, GameStatusBar, PlayersBar, PlayersFieldBar, PlayersHandBar, SelectablePlayersFieldBar}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import scalafx.scene.Node
import scalafx.animation.FadeTransition
import scalafx.util.Duration
import de.htwg.se.soccercardclash.view.gui.components.dialog.{ComparisonDialogHandler, DialogFactory, WinnerDialog}
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import scalafx.scene.text.Text
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import scalafx.application.Platform

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayingFieldScene(
                         controller: IController,
                         windowWidth: Double,
                         windowHeight: Double,
                       ) extends Scene(windowWidth, windowHeight) with Observer {
  this.getStylesheets.add(Styles.playingFieldCss)

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
  private val comparisonHandler = new ComparisonDialogHandler(controller, overlay)

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
  val player1ScoreText = new Label(s"${playingField.getScores.getScorePlayer1}") {
    styleClass += "player-score"
  }

  val player2ScoreText = new Label(s"${playingField.getScores.getScorePlayer2}") {
    styleClass += "player-score"
  }

  val player1ScoreBox = new VBox {
    styleClass += "score-box"
    children = Seq(
      new Label("⚽") {
        styleClass += "score-icon"
      },
      new Label(player1.name) {
        styleClass += "player-name"
      },
      player1ScoreText
    )
  }

  val player2ScoreBox = new VBox {
    styleClass += "score-box"
    children = Seq(
      new Label("⚽") {
        styleClass += "score-icon"
      },
      new Label(player2.name) {
        styleClass += "player-name"
      },
      player2ScoreText
    )
  }


  val scoresBar = new HBox {
    spacing = 50
    alignment = Pos.Center
    children = Seq(player1ScoreBox, player2ScoreBox)
    styleClass += "scores-bar"
  }

  val buttonBar = new ButtonBar(controller, playingField, this, gameStatusBar)


  val playersBar = new PlayersBar(controller, this)

  val mainLayout = new StackPane {
    children = Seq(
      new BorderPane {
        top = new BorderPane {
          center = playersBar
        }

        center = new BorderPane {
          left = buttonBar
          center = new VBox {
            spacing = 10
            alignment = Pos.Center
            children = Seq(gameStatusBar, playerFields)
          }
        }

        bottom = new BorderPane {
          center = playerHands
        }
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

    comparisonHandler.handleComparisonEvent(e)

    e match {
      case NoDoubleAttacksEvent(player) =>
        overlay.show(createDoubleAttackAlert(player), true)

      case Events.RegularAttack =>

        Future {
          Thread.sleep(3000)
          Platform.runLater(() => {
            updateDisplay()
          })
        }
      case Events.DoubleAttack =>

        Future {
          Thread.sleep(3000)
          Platform.runLater(() => {
            updateDisplay()
          })
        }

      case Events.Undo | Events.Redo | Events.BoostDefender | Events.BoostGoalkeeper
           | Events.RegularSwap | Events.ReverseSwap | Events.PlayingField =>
        Future {
          Thread.sleep(100)
          Platform.runLater(() => {
            updateDisplay()
          })
        }

      case Events.MainMenu =>
        if (SceneManager.currentScene.contains(SceneManager.sceneRegistry.getMainMenuScene)) {
          return
        }
        controller.remove(this)
        SceneManager.update(e)

      case ScoreEvent(player) =>
        Future {
          Thread.sleep(4000)
          Platform.runLater(() => {
            println(s"⚽ Goal scored by ${player.name}")
            showGoalScoredDialog(player, autoHide = true)
            updateDisplay()
          })
        }


      case GameOver(winner) =>
        Future {
          Thread.sleep(4000)
          Platform.runLater(() => {
            println(f"🎉 WINNER! : ${winner.name}")
            showGameOverPopup(winner, autoHide = false)
          })
        }
      case _ =>
    }
  }

  private def showGameOverPopup(winner: IPlayer, autoHide: Boolean): Unit = {
    DialogFactory.showGameOverPopup(winner, overlay, controller, autoHide)
  }

  private def showGoalScoredDialog(winner: IPlayer, autoHide: Boolean): Unit = {
  DialogFactory.showGoalScoredDialog(winner, overlay, controller, autoHide)
  }
  private def createDoubleAttackAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Double Attacks Left!", overlay, autoHide = true)
  }

  private def updateFieldBar() : Unit = {
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
    val newDefenderFieldBar = if (defender == currentPlayer1) player1FieldBar else player2FieldBar
    playerFields.children.clear()
    playerFields.children.add(newDefenderFieldBar)
    newDefenderFieldBar.updateBar()

  }

  private def updateHands() : Unit = {
    val currentPlayingField = controller.getCurrentGame.getPlayingField

    if (currentPlayingField == null || currentPlayingField.getDataManager.getPlayerGoalkeeper(
      controller.getCurrentGame.getPlayer1).isEmpty) {
      return
    }

    val currentPlayer1 = controller.getCurrentGame.getPlayer1
    val currentPlayer2 = controller.getCurrentGame.getPlayer2

    val attacker = currentPlayingField.getRoles.attacker
    val defender = currentPlayingField.getRoles.defender

    val newAttackerHandBar = if (attacker == currentPlayer1) player1HandBar else player2HandBar
    playerHands.children.clear()
    playerHands.children.add(newAttackerHandBar)
    newAttackerHandBar.updateBar()
  }
  private def updateAvatars(): Unit = {
    playersBar.refreshOnRoleSwitch()
    playersBar.refreshActionStates()
  }
  private def updateScores() : Unit = {
    val score1 = controller.getCurrentGame.getPlayingField.getScores.getScorePlayer1
    val score2 = controller.getCurrentGame.getPlayingField.getScores.getScorePlayer2
    player1ScoreText.text = s"$score1"
    player2ScoreText.text = s"$score2"
  }
  def updateDisplay(): Unit = {
    updateFieldBar()
    updateHands()
    updateAvatars()
    updateScores()
  }
  
}

