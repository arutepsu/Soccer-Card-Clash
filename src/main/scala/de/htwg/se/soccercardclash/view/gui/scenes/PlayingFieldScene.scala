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
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.{SceneManager, UIAction, UIActionScheduler}
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
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.model.playerComponent.base.AI

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class PlayingFieldScene(
                         controller: IController,
                         val contextHolder: IGameContextHolder,
                       ) extends GameScene {
  this.getStylesheets.add(Styles.generalCss)


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
  private var pendingAITurn = false
  controller.add(this)
  updateDisplay()


  private val scheduler = UIActionScheduler() // using given ExecutionContext

  override def handleGameAction(e: GameActionEvent): Unit = {
    val overlayAction = comparisonHandler.createOverlayAction(e)
    val delay = e match {
      case GameActionEvent.RegularAttack | GameActionEvent.DoubleAttack => 3000
      case GameActionEvent.Undo | GameActionEvent.Redo |
           GameActionEvent.BoostDefender | GameActionEvent.BoostGoalkeeper |
           GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap => 100
      case _ => 0
    }

    val updateAction = UIAction.delayed(delay) { updateDisplay() }
    scheduler.runSequence((overlayAction.toSeq :+ updateAction)*)
    comparisonHandler.resetLastCards()
  }


  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.ScoreEvent(player) =>
      scheduler.runSequence(
        UIAction.delayed(0) {
          showGoalScoredDialog(player, autoHide = true)
        },
        UIAction.delayed(4000) {
          updateDisplay()
        }
      )


    case StateEvent.GameOver(winner) =>
      scheduler.runSequence(
        UIAction.delayed(4000) {
          showGameOverPopup(winner, autoHide = false)
        }
      )


    case StateEvent.NoDoubleAttacksEvent(player) =>
      overlay.show(createDoubleAttackAlert(player), true)

    case _ =>

  override def update(e: ObservableEvent): Unit = {
    e match {
      case _: StateEvent =>
        // Safe: always on JavaFX thread
        Platform.runLater(() => comparisonHandler.handleComparisonEvent(e))

      case TurnEvent.NextTurnEvent =>
        pendingAITurn = true

      case gameAction: GameActionEvent =>
        handleGameAction(gameAction)
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

        if (pendingAITurn) {
          val attacker = contextHolder.get.state.getRoles.attacker
          pendingAITurn = false // consume it

          attacker match {
            case ai: Player if ai.isAI =>
              scheduler.runSequence(
                UIAction.delayed(3000) {
                  handleAITurn()
                }
              )
            case _ => 
          }
        }

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
  def handleAITurn(): Unit = {
    println("!!!!!!!!!!!!!ai moves!")
    val ctx = contextHolder.get
    ctx.state.getRoles.attacker match {
      case player: Player if player.isAI =>
        val strategy = player.playerType.asInstanceOf[AI].strategy
        val currentCtx = contextHolder.get
        val action = strategy.decideAction(currentCtx, player)
        val (newCtx, _) = controller.executePlayerAction(action, currentCtx)
        contextHolder.set(newCtx)

      case _ =>
    }
  }

}