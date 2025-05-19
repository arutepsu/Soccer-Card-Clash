package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, RegularSwapButton, ReverseSwapButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages, PlayersHandBar, SelectableHandCardRenderer}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

class AttackerHandScene(
                         controller: IController,
                         val contextHolder: IGameContextHolder,
                       ) extends GameScene {

  this.getStylesheets.add(Styles.attackerHandSceneCss)

  val attackerHandBar = new PlayersHandBar(
    player = contextHolder.get.state.getRoles.attacker,
    playingField = contextHolder.get.state,
    renderer = new SelectableHandCardRenderer(() => contextHolder.get.state)
  )
  private val overlay = new Overlay(this)
  private val backButton: Button = GameButtonFactory.createGameButton("Back to Game", 180, 50) {
    () => GlobalObservable.notifyObservers(SceneSwitchEvent.PlayingField)
  }

  private val regularSwapButton: Button = ActionButtonFactory.createRegularSwapButton(
    RegularSwapButton(), "Regular Swap", 180, 50, this, controller
  )

  private val circularSwapButton: Button = ActionButtonFactory.createReverseSwapButton(
    ReverseSwapButton(), "Reverse Swap", 180, 50, this, controller
  )
  private val infoButton: Button = GameButtonFactory.createGameButton("Info", 180, 50) {
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

  private val layout = new VBox {
    alignment = Pos.Center
    spacing = 30
    padding = Insets(30)
    children = Seq(navButtonLayout, attackerHandBar, actionButtonLayout)
  }

  root = new StackPane {
    styleClass.add("attacker-hand-scene")
    children = Seq(
      new Region {
//        style = "-fx-background-color: black;"
      },
      layout,
      overlay.getPane
    )
  }

  override def handleGameAction(e: GameActionEvent): Unit = e match
    case GameActionEvent.Reverted =>
      updateDisplay()
    case GameActionEvent.RegularSwap | GameActionEvent.ReverseSwap =>
      updateDisplay()
    case _ =>

  def updateDisplay(): Unit = {
    val gameState = contextHolder.get.state
    attackerHandBar.updateBar(gameState)
    
  }

  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.NoSwapsEvent(player) =>
      println("now swaps!")
      overlay.show(createSwapAlert(player), true)
    case _ =>

  private def createSwapAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Swaps Left!", overlay, autoHide = false)
  }
}
