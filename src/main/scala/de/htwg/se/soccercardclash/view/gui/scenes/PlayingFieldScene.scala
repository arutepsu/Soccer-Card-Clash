package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.dialog.{ComparisonDialogHandler, DialogFactory, WinnerDialog}
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.*
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.{SceneManager, UIAction, UIActionScheduler}
import de.htwg.se.soccercardclash.view.gui.utils.{BoostImage, Styles}
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.*
import scalafx.scene.text.{Font, Text}
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage
import scalafx.util.Duration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class PlayingFieldScene(
                         controller: IController,
                         val contextHolder: IGameContextHolder,
                       ) extends GameScene {
  this.getStylesheets.add(Styles.generalCss)

  Font.loadFont(getClass.getResourceAsStream("/fonts/Rajdhani/Rajdhani-Regular.ttf"), 20)
  val overlay = new Overlay(this)
  val comparisonHandler = new ComparisonDialogHandler(controller, contextHolder, overlay)

  val renderer = new SelectableFieldCardRenderer(() => contextHolder.get.state)

  var currentDefenderFieldBar: Option[PlayersFieldBar] = None
  val player1ScoreText = new Label {
    styleClass += "player-score"
  }
  val player2ScoreText = new Label {
    styleClass += "player-score"
  }
  val actionButtonBar = ActionButtonBar(controller, contextHolder.get.state, this, overlay)
  val navButtonBar = NavButtonBar(controller, contextHolder.get.state, this)
  val playersBar = new PlayersBar(controller, this)

  val playerFields = new HBox {
    alignment = Pos.Center;
    spacing = 150
  }
  val playerHands = new HBox {
    alignment = Pos.Center;
    spacing = 300
  }

  val palyersBar = new HBox {
    spacing = 50
    alignment = Pos.TOP_CENTER
    children = Seq(playersBar)
  }

  val inputBlocker = new Region {
    style = "-fx-background-color: rgba(0,0,0,0);"
    pickOnBounds = true
    mouseTransparent = false
    visible = false
  }
  StackPane.setAlignment(inputBlocker, Pos.CENTER)

  private val attackerAvatarBox = new VBox {
    alignment = Pos.TOP_CENTER
  }

  private val mainLayout = new StackPane {
    children = Seq(
      new VBox {
        spacing = 30
        children = Seq(
          new BorderPane {
            top = new BorderPane {
              center = new VBox {
                padding = Insets(40, 0, 0, 0)
                alignment = Pos.CENTER
                children = Seq(new HBox {
                  alignment = Pos.CENTER
                  children = Seq(playersBar)
                })
              }
            }
          },
          new BorderPane {
            center = new VBox {
              spacing = 10
              alignment = Pos.CENTER
              children = Seq(
                new HBox {
                  spacing = 5
                  alignment = Pos.Center
                  children = Seq(navButtonBar, playerFields, actionButtonBar)
                },
                new HBox {
                  alignment = Pos.Center
                  children = Seq(playerHands, attackerAvatarBox)
                }
              )
            }
          }
            )
      },
      overlay.getPane,
      inputBlocker
    )
    styleClass += "root"
  }
  root = mainLayout
  private var pendingAITurn = false
  controller.add(this)
  updateDisplay(contextHolder.get)


  private val scheduler = UIActionScheduler()

  override def handleGameAction(e: GameActionEvent): Unit = {
    val overlayAction = comparisonHandler.createOverlayAction(e)
    val delay = e match {
      case GameActionEvent.RegularAttack | GameActionEvent.DoubleAttack => 3000
      case GameActionEvent.Undo | GameActionEvent.Redo |
           GameActionEvent.BoostDefender | GameActionEvent.BoostGoalkeeper |
           GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap => 100
      case _ => 0
    }

    val updateAction = UIAction.delayed(delay) { updateDisplay(contextHolder.get) }
    scheduler.runSequence((overlayAction.toSeq :+ updateAction)*)
    comparisonHandler.resetLastCards()
  }


  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.ScoreEvent(player) =>
      scheduler.runSequence(
        UIAction.delayed(4000) {
          showGoalScoredDialog(player, autoHide = true)
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
        Platform.runLater(() => comparisonHandler.handleComparisonEvent(e))

      case AIEvent.NextAIEvent =>
        pendingAITurn = true

      case event: SceneSwitchEvent =>

      case gameAction: GameActionEvent =>
        handleGameAction(gameAction)
    }

    super.update(e)
  }
  private def delayedUpdate(ms: Int): Unit = delayed(ms)(updateDisplay(contextHolder.get))

  private def delayed(ms: Int)(action: => Unit): Unit = {
    Future {
      Thread.sleep(ms)
      Platform.runLater(action)
    }
  }
  def updateDisplay(viewCtx: GameContext): Unit = {
    updateFieldBars(viewCtx)
    updateHands(viewCtx)
    updateAvatars()
    updateScores(viewCtx)


    val avatar: ImageView = PlayerAvatarRegistry.getAvatarView(
      player = contextHolder.get.state.getRoles.attacker,
      scale = 0.04
    )
    avatar.styleClass += "neon-avatar"

    val titleLabel = new Label("Attacker:") {
      styleClass += "attacker-label"
      padding = Insets(0, 10, 0, 10)
    }

    val nameLabel = new Label(contextHolder.get.state.getRoles.attacker.name) {
      styleClass += "attacker-name"
      padding = Insets(0, 10, 0, 10)
    }

    new HBox(10, titleLabel, avatar, nameLabel) {
      alignment = Pos.CENTER_RIGHT
    }
    attackerAvatarBox.children.setAll(titleLabel, avatar, nameLabel)

    if (pendingAITurn) {
          val attacker = contextHolder.get.state.getRoles.attacker
          pendingAITurn = false

          attacker match {
            case ai: Player if ai.isAI =>
              inputBlocker.visible = true
              scheduler.runSequence(
                UIAction.delayed(4000) {
                  handleAITurn()
                }
              )
            case _ =>  inputBlocker.visible = false
          }
        }
  }

  private def updateFieldBars(ctx: GameContext): Unit = {
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
  }

  private def updateHands(contextHolder: GameContext): Unit = {
    val newHandBar =
      new PlayersHandBar(
        contextHolder.state.getRoles.attacker,
        contextHolder.state,
        renderer = DefaultHandCardRenderer
      )
    newHandBar.alignmentInParent = Pos.Center

    playerHands.children.setAll(newHandBar)
    newHandBar.updateBar(contextHolder.state)
  }


  private def updateScores(ctx: GameContext): Unit = {
    player1ScoreText.text = s"${ctx.state.getScores.getScore(ctx.state.getRoles.attacker)}"
    player2ScoreText.text = s"${ctx.state.getScores.getScore(ctx.state.getRoles.defender)}"
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
    if (overlay.getPane.isVisible) return

    val ctx = contextHolder.get
    ctx.state.getRoles.attacker match {
      case player: Player if player.isAI =>
        inputBlocker.visible = true
        val strategy = player.playerType.asInstanceOf[AI].strategy
        val currentCtx = contextHolder.get
        val action = strategy.decideAction(currentCtx, player)
        val (newCtx, _) = controller.executeAIAction(action, currentCtx)
        contextHolder.set(newCtx)
        inputBlocker.visible = false

      case _ =>
        inputBlocker.visible = false
    }
  }


}