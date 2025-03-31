package de.htwg.se.soccercardclash.view.gui.scenes

import scalafx.scene.control.Button
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.util.{Events, NoBoostsEvent, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{BoostBar, GameStatusBar}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.application.Platform
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.scene.Node
import scalafx.scene.text.Text
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, BoostButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.SelectablePlayersFieldBar
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.Includes._
import javafx.scene.layout.{BackgroundRepeat, BackgroundPosition, BackgroundSize}

class AttackerDefendersScene(
                                    controller: IController,
                                    val playingFieldScene: PlayingFieldScene,
                                    val playingField: Option[IPlayingField],
                                    windowWidth: Double,
                                    windowHeight: Double
                                  ) extends Scene(windowWidth, windowHeight) with Observer {

  playingField.foreach(_.add(this))
  controller.add(this)
  val getPlayingField: IPlayingField = playingField.get
  val gameStatusBar = new GameStatusBar

//  val backgroundView = new Region {
//    style = "-fx-background-color: black;"
//  }
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


  val attackerDefenderField: Option[SelectablePlayersFieldBar] = playingField.map { pf =>
    val fieldBar = new SelectablePlayersFieldBar(pf.getRoles.attacker, pf)
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
        case NoBoostsEvent(player) =>
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
