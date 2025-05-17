package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.util.StateEvent.NoBoostsEvent
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, BoostButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{BoostBar, GameStatusBar, PlayersFieldBar, SelectableFieldCardRenderer}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import javafx.scene.layout.{BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Button
import scalafx.scene.image.Image
import scalafx.scene.layout.*
import scalafx.scene.text.Text

class AttackerDefendersScene(
                              controller: IController,
                              val contextHolder: IGameContextHolder,
                            ) extends GameScene {

  val gameStatusBar = new GameStatusBar
  val overlay = new Overlay(this)
  val attackerDefenderField: Option[PlayersFieldBar] = {
    val pf = contextHolder.get.state
    Some(new PlayersFieldBar(
      player = pf.getRoles.attacker,
      getGameState = () => pf,
      renderer = new SelectableFieldCardRenderer(() => pf)
    ) {
      styleClass.add("selectable-field-bar")
    })
  }
  private val backgroundView = new Region {
    val image = new Image(getClass.getResource("/images/data/images/field.jpg").toExternalForm)
    background = new Background(Array(
      new BackgroundImage(
        image,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(1.0, 1.0, true, true, true, false)
      )
    ))
  }
  private val backButton: Button = GameButtonFactory.createGameButton("Back to Game", 180, 50) {
    () =>
      GlobalObservable.notifyObservers(SceneSwitchEvent.PlayingField)
  }

  private val boostButton: Button = ActionButtonFactory.createBoostButton(
    BoostButton(), "Boost Card", 180, 50, this, controller
  )

  backButton.styleClass.add("button")
  boostButton.styleClass.add("button")

  private val buttonLayout = new HBox {
    styleClass.add("button-layout")
    alignment = Pos.Center
    spacing = 15
    children = Seq(boostButton, backButton)
  }

  private val layout = new VBox {
    alignment = Pos.Center
    spacing = 20
    padding = Insets(20)
    children = attackerDefenderField.toSeq :+ buttonLayout
  }


  root = new StackPane {
    style = "-fx-background-color: black;"
    children = Seq(backgroundView, layout, overlay.getPane)
  }

  override def handleStateEvent(e: StateEvent): Unit = e match
    case StateEvent.NoBoostsEvent(player) =>
      overlay.show(createBoostAlert(player), true)
    case _ =>

  private def createBoostAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Boosts Left!", overlay, autoHide = true)
  }

}
