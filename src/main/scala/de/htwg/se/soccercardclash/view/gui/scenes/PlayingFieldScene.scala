package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{ButtonBar, DefaultHandCardRenderer, GameStatusBar, PlayersBar, PlayersFieldBar, PlayersHandBar, SelectableFieldCardRenderer}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.utils.ImageUtils
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
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
                         val contextHolder: IGameContextHolder,
                         windowWidth: Double,
                         windowHeight: Double
                       ) extends Scene(windowWidth, windowHeight) with Observer {

  this.getStylesheets.add(Styles.playingFieldCss)

  // ----- Static references initialized once -----
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
  val player1ScoreText = new Label { styleClass += "player-score" }
  val player2ScoreText = new Label { styleClass += "player-score" }

  val player1ScoreBox = new VBox {
    styleClass += "score-box"
    children = Seq(
      new Label("⚽") { styleClass += "score-icon" },
      new Label(contextHolder.get.state.getPlayer1.name) { styleClass += "player-name" },
      player1ScoreText
    )
  }

  val player2ScoreBox = new VBox {
    styleClass += "score-box"
    children = Seq(
      new Label("⚽") { styleClass += "score-icon" },
      new Label(contextHolder.get.state.getPlayer2.name) { styleClass += "player-name" },
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

  val playerFields = new HBox { alignment = Pos.Center; spacing = 150 }
  val playerHands = new HBox { alignment = Pos.Center; spacing = 300 }

  private val mainLayout = new StackPane {
    children = Seq(
      new BorderPane {
        top = new BorderPane { center = playersBar }
        center = new BorderPane {
          left = buttonBar
          center = new VBox { spacing = 10; alignment = Pos.Center; children = Seq(gameStatusBar, playerFields) }
        }
        bottom = new BorderPane { center = playerHands }
      },
      overlay.getPane
    )
  }

  root = mainLayout

  contextHolder.add(this)
  updateDisplay()
  // ---------------- UPDATE METHODS ----------------
  override def update(e: ObservableEvent): Unit = {
    println(s":received EVENT! $e")
    Future {
      Thread.sleep(100)
      Platform.runLater(() => comparisonHandler.handleComparisonEvent(e))
    }
    e match {

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
        println("🏠 Returning to Main Menu")
        if (!SceneManager.currentScene.contains(SceneManager.sceneRegistry.getMainMenuScene)) {
          controller.remove(this)
          SceneManager.update(e)
        }

      case Events.NoDoubleAttacksEvent(player) =>
        overlay.show(createDoubleAttackAlert(player), true)

      case Events.ScoreEvent(player) =>
        println(s"⚽ Goal scored by ${player.name}")
        Future {
          Thread.sleep(4000)
          Platform.runLater(() => {
            showGoalScoredDialog(player, autoHide = true)
            updateDisplay()
          })
        }
      case Events.GameOver(winner) =>
        println(s"🏆 Game Over! Winner: ${winner.name}")
        Future {
          Thread.sleep(4000)
          Platform.runLater(() => showGameOverPopup(winner, autoHide = false))
        }
      case other =>
        println(s"🔔 Unhandled Events case: $other")
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
    println("!!!!!display updated!!!!!")
    buildViewContext(contextHolder.get.state) match {
      case Some(viewCtx) if viewCtx.hasGoalkeeper1 =>
        updateFieldBars(viewCtx)
        updateHands(viewCtx)
        updateAvatars()
        updateScores(viewCtx)
      case _ =>
        println("⚠️ Skipping update → invalid state or missing goalkeeper")
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