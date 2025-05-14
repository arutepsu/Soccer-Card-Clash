package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.dialog.{ComparisonDialogHandler, DialogFactory, WinnerDialog}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.*
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{ImageUtils, Styles}
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage
import scalafx.util.Duration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class PlayingFieldScene(
                         controller: IController,
                         val contextHolder: IGameContextHolder,
                       ) extends GameScene {
  this.getStylesheets.add(Styles.playingFieldCss)


  val overlay = new Overlay(this)
  val comparisonHandler = new ComparisonDialogHandler(controller, contextHolder, overlay)
  val gameStatusBar = new GameStatusBar

  val player1HandBar = new PlayersHandBar(
    contextHolder.get.state.getPlayer1,
    contextHolder.get.state,
    isLeftSide = true,
    renderer = DefaultHandCardRenderer
  )

  val player2HandBar = new PlayersHandBar(
    contextHolder.get.state.getPlayer2,
    contextHolder.get.state,
    isLeftSide = false,
    renderer = DefaultHandCardRenderer
  )
  val renderer = new SelectableFieldCardRenderer(() => contextHolder.get.state)

  var currentDefenderFieldBar: Option[PlayersFieldBar] = None
  val player1ScoreText = new Label {
    styleClass += "player-score"
  }
  val player2ScoreText = new Label {
    styleClass += "player-score"
  }

  val player1ScoreBox = new VBox {
    styleClass += "score-box"
    children = Seq(
      new Label("⚽") {
        styleClass += "score-icon"
      },
      new Label(contextHolder.get.state.getPlayer1.name) {
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
      new Label(contextHolder.get.state.getPlayer2.name) {
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

  val buttonBar = new ButtonBar(controller, contextHolder.get.state, this, gameStatusBar)
  val playersBar = new PlayersBar(controller, this)

  val playerFields = new HBox {
    alignment = Pos.Center; spacing = 150
  }
  val playerHands = new HBox {
    alignment = Pos.Center; spacing = 300
  }

  private val mainLayout = new StackPane {
    children = Seq(
      new BorderPane {
        top = new BorderPane {
          center = playersBar
        }
        center = new BorderPane {
          left = buttonBar
          center = new VBox {
            spacing = 10; alignment = Pos.Center; children = Seq(gameStatusBar, playerFields)
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

  controller.add(this)
  updateDisplay()

  override def handleGameAction(e: GameActionEvent): Unit = e match
    case GameActionEvent.RegularAttack | GameActionEvent.DoubleAttack =>
      delayedUpdate(3000)
    case GameActionEvent.Undo | GameActionEvent.Redo |
         GameActionEvent.BoostDefender | GameActionEvent.BoostGoalkeeper |
         GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap =>
      delayedUpdate(100)
    case _ =>

  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.ScoreEvent(player) =>
      delayed(4000) {
        showGoalScoredDialog(player, autoHide = true)
        updateDisplay()
      }

    case StateEvent.GameOver(winner) =>
      delayed(4000) {
        showGameOverPopup(winner, autoHide = false)
      }

    case StateEvent.NoDoubleAttacksEvent(player) =>
      overlay.show(createDoubleAttackAlert(player), true)

    case _ =>

  override def update(e: ObservableEvent): Unit = {
    Future {
      Thread.sleep(100)
      Platform.runLater(() => comparisonHandler.handleComparisonEvent(e))
    }
    super.update(e)
  }

  private def delayedUpdate(ms: Int): Unit = delayed(ms)(updateDisplay())

  private def delayed(ms: Int)(action: => Unit): Unit = {
    Future {
      Thread.sleep(ms)
      Platform.runLater(action)
    }
  }
  private def buildViewContext(state: IGameState): Option[PlayingFieldViewContext] = {
    if (state == null) return None
    val player1 = state.getPlayer1
    val player2 = state.getPlayer2
    val dataManager = state.getDataManager
    val hasGoalkeeper1 = dataManager.getPlayerGoalkeeper(player1).nonEmpty
    Some(PlayingFieldViewContext(
      state,
      player1,
      player2,
      state.getRoles.attacker,
      state.getRoles.defender,
      dataManager,
      state.getScores.getScorePlayer1,
      state.getScores.getScorePlayer2,
      hasGoalkeeper1
    ))
  }

  def updateDisplay(): Unit = {
    buildViewContext(contextHolder.get.state) match {
      case Some(viewCtx) if viewCtx.hasGoalkeeper1 =>
        updateFieldBars(viewCtx)
        updateHands(viewCtx)
        updateAvatars()
        updateScores(viewCtx)
      case _ =>
    }
  }

  private def updateFieldBars(ctx: PlayingFieldViewContext): Unit = {
    val defender = ctx.state.getRoles.defender

    val defenderRenderer = new SelectableFieldCardRenderer(() => ctx.state)

    val newDefenderFieldBar = new PlayersFieldBar(
      player = defender,
      getGameState = () => ctx.state,
      renderer = defenderRenderer
    )

    playerFields.children.clear()
    playerFields.children.add(newDefenderFieldBar)

    currentDefenderFieldBar = Some(newDefenderFieldBar)
    newDefenderFieldBar.updateGameStatus()
  }

  private def updateHands(ctx: PlayingFieldViewContext): Unit = {
    val newAttackerHandBar =
      new PlayersHandBar(
        ctx.attacker,
        ctx.state, isLeftSide = ctx.attacker == ctx.player1,
        renderer = DefaultHandCardRenderer)
    playerHands.children.clear()
    playerHands.children.add(newAttackerHandBar)
    newAttackerHandBar.updateBar(ctx.state)
  }


  private def updateScores(ctx: PlayingFieldViewContext): Unit = {
    player1ScoreText.text = s"${ctx.score1}"
    player2ScoreText.text = s"${ctx.score2}"
  }

  private def updateAvatars(): Unit = {
    playersBar.refreshOnRoleSwitch()
    playersBar.refreshActionStates()
  }

  private def showGameOverPopup(winner: IPlayer, autoHide: Boolean): Unit = {
    DialogFactory.showGameOverPopup(winner, overlay, controller, contextHolder, autoHide)
  }

  private def showGoalScoredDialog(winner: IPlayer, autoHide: Boolean): Unit = {
    DialogFactory.showGoalScoredDialog(winner, overlay, controller, contextHolder, autoHide)
  }

  private def createDoubleAttackAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Double Attacks Left!", overlay, autoHide = true)
  }
}