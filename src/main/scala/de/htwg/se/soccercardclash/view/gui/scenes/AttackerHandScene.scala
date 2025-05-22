package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, RegularSwapButton, ReverseSwapButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.*
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{HasContextHolder, Styles}
import scalafx.application.Platform
import scalafx.geometry
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

class AttackerHandScene(
                         controller: IController,
                         val contextHolder: IGameContextHolder,
                       ) extends GameScene with HasContextHolder{
  controller.add(this)
  override def getContextHolder: IGameContextHolder = contextHolder

  this.getStylesheets.add(Styles.attackerHandSceneCss)

  val attackerBar = new AttackerBar(controller, this)

  val attackerHandBar = new PlayersHandBar(
    player = getContextHolder.get.state.getRoles.attacker,
    playingField = getContextHolder.get.state,
    renderer = new SelectableHandCardRenderer(() => getContextHolder.get.state)
  )
  private val overlay = new Overlay(this)
  private val backButton: Button = GameButtonFactory.createGameButton("Back to Game", 200, 100) {
    () => GlobalObservable.notifyObservers(SceneSwitchEvent.PlayingField)
  }

  private val regularSwapButton: Button = ActionButtonFactory.createRegularSwapButton(
    RegularSwapButton(), "Regular Swap", 200, 100, this, controller
  )

  private val circularSwapButton: Button = ActionButtonFactory.createReverseSwapButton(
    ReverseSwapButton(), "Reverse Swap", 200, 100, this, controller
  )
  private val infoButton: Button = GameButtonFactory.createGameButton("Info", 200, 100) {
    () => DialogFactory.showHandInfoDialog("Title", "Message", overlay)
  }

  private val actionButtonLayout = new HBox {
    styleClass.add("button-layout")
    alignment = Pos.Center
    spacing = 15
    children = Seq(regularSwapButton, circularSwapButton)
  }
  private val navButtonLayout = new HBox {
    styleClass.add("button-layout")
    alignment = Pos.Center
    spacing = 15
    children = Seq(backButton, infoButton)
  }

  val playerBarWrapper = new BorderPane {
    top = attackerBar
  }


  private val layout = new VBox {
    alignment = Pos.TopCenter
    spacing = 30
    padding = Insets(30)
    children = Seq(
      playerBarWrapper,
      navButtonLayout,
      attackerHandBar,
      actionButtonLayout
    )
  }

  root = new StackPane {
    styleClass.add("attacker-hand-scene")
    children = Seq(
      layout,
      overlay.getPane
    )
  }

  override def handleGameAction(e: GameActionEvent): Unit = e match
    case GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap =>
      updateDisplay()
    case _ =>

  def updateDisplay(): Unit = {
    val gameState = getContextHolder.get.state
    attackerHandBar.updateBar(gameState)
    attackerBar.refreshActionStates()
  }


  override def handleStateEvent(e: StateEvent): Unit = {
    e match {
      case StateEvent.NoSwapsEvent(player) =>
        overlay.show(createSwapAlert(player), true)
      case _ =>
    }
  }

  private def createSwapAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Swaps Left!", overlay, autoHide = false)
  }
}
