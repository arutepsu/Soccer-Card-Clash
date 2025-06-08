package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.util.StateEvent.NoBoostsEvent
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, BoostButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.*
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{HasContextHolder, Styles}
import javafx.scene.layout.{BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.image.Image
import scalafx.scene.layout.*
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

class AttackerDefendersScene(
                              controller: IController,
                              val contextHolder: IGameContextHolder,
                            ) extends GameScene with HasContextHolder {
  controller.add(this)

  val attackerBar = new AttackerBar(controller, this)

  this.getStylesheets.add(Styles.attackerFieldSceneCss)
  val overlay = new Overlay(this)
  val attackerDefenderField: Option[PlayersFieldBar] = {
    val pf = contextHolder.get.state
    Some(new PlayersFieldBar(
      player = pf.getRoles.attacker,
      getGameState = () => pf,
      renderer = new SelectableFieldCardRenderer(() => pf)
    ))
  }
  val playerBarWrapper = new BorderPane {
    top = attackerBar
    styleClass += "label"
  }
  private val backgroundView = new Region {
    styleClass += "attacker-field-scene"
  }

  private val backButton: Button = GameButtonFactory.createGameButton("Back to Game", 200, 100) {
    () =>
      GlobalObservable.notifyObservers(SceneSwitchEvent.PlayingField)
  }

  private val boostButton: Button = ActionButtonFactory.createBoostButton(
    BoostButton(), "Boost Card", 200, 100, this, controller
  )

  private val infoButton: Button = GameButtonFactory.createGameButton("Info", 200, 100) {
    () => DialogFactory.showInfoDialog("Swap Instructions", "BOOST_INFO", overlay)
  }
  private val buttonLayout = new HBox {
    alignment = Pos.Center
    spacing = 15
    children = Seq(boostButton, infoButton, backButton)
  }
  private val layoutCenter = new VBox {
    alignment = Pos.Center
    spacing = 20
    padding = Insets(20)
    children = attackerDefenderField.toSeq :+ buttonLayout
  }
  private val fullLayout = new BorderPane {
    padding = Insets(30)
    top = playerBarWrapper
    center = layoutCenter
  }

  override def getContextHolder: IGameContextHolder = contextHolder

  root = new StackPane {
    children = Seq(backgroundView, fullLayout, overlay.getPane)
  }

  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.NoBoostsEvent(player) =>
      overlay.show(createBoostAlert(player), true)
    case _ =>

  override def handleGameAction(e: GameActionEvent): Unit = {
    e match {
      case GameActionEvent.BoostDefender | GameActionEvent.BoostGoalkeeper => attackerBar.refreshActionStates()
      case _ =>
    }
  }

  private def createBoostAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Boosts Left!", overlay, autoHide = true)
  }

}
