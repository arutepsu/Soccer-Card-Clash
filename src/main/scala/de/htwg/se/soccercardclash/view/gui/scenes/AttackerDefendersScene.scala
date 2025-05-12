package de.htwg.se.soccercardclash.view.gui.scenes

import scalafx.scene.control.Button
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.util.{Events, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{BoostBar, GameStatusBar, PlayersFieldBar}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.SelectableFieldCardRenderer
import scalafx.scene.Node
import scalafx.scene.text.Text
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, BoostButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import scalafx.scene.image.Image
import scalafx.scene.layout.*
import scalafx.Includes.*
import javafx.scene.layout.{BackgroundPosition, BackgroundRepeat, BackgroundSize}

class AttackerDefendersScene(
                              controller: IController,
                              val playingFieldScene: PlayingFieldScene,
                              val playingField: Option[IGameState],
                              windowWidth: Double,
                              windowHeight: Double
                                  ) extends Scene(windowWidth, windowHeight) with Observer {


  playingFieldScene.contextHolder.add(this)
  val gameStatusBar = new GameStatusBar
  
  val backgroundView = new Region {
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


  val attackerDefenderField: Option[PlayersFieldBar] = playingField.map { pf =>
    val renderer = new SelectableFieldCardRenderer(() => pf) // ✅ always uses up-to-date state
    val fieldBar = new PlayersFieldBar(
      player = pf.getRoles.attacker,
      getGameState = () => pf,
      renderer = renderer
    )
    fieldBar.styleClass.add("selectable-field-bar")
    fieldBar
  }


  val overlay = new Overlay(this)

  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) {
    () =>
      controller.notifyObservers(Events.PlayingField)
      playingFieldScene.updateDisplay()
  }
  backButton.styleClass.add("button")

  val boostButton: Button = ActionButtonFactory.createBoostButton(
    BoostButton(),
    "Boost Card",
    180,
    50,
    this,
    controller
  )
  boostButton.styleClass.add("button")

  val buttonLayout = new HBox {
    styleClass.add("button-layout")
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(boostButton, backButton)
  }

  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = attackerDefenderField.toSeq :+ buttonLayout
  }

  root = new StackPane {
    styleClass.add("attacker-defenders-scene")
    children = Seq(backgroundView, layout, overlay.getPane) // ✅ Add Overlay
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 AttackerDefendersScene Received Event: $e")

      e match {
        case Events.NoBoostsEvent(player) =>
          println(s"⚠️ ${player.name} has no Boosts left! Showing Alert in AttackerDefendersScene...")
          overlay.show(createBoostAlert(player), true) // ✅ Show alert in this scene only

        case _ =>
          SceneManager.update(e) // ✅ Handle other events normally
      }
    })
  }

  private def createBoostAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Boosts Left!", overlay, autoHide = true)
  }
}
