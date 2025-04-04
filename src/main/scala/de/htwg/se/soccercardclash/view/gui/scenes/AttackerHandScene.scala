package de.htwg.se.soccercardclash.view.gui.scenes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import de.htwg.se.soccercardclash.util.{Events, NoSwapsEvent, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.{GameStatusBar, GameStatusMessages}
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.utils.Styles
import scalafx.scene.Node
import scalafx.scene.text.Text
import scalafx.application.Platform
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.view.gui.components.actionButton.{ActionButtonFactory, RegularSwapButton, ReverseSwapButton}
import de.htwg.se.soccercardclash.view.gui.components.alert.GameAlertFactory
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.SelectablePlayersHandBar

class AttackerHandScene(
                              controller: IController,
                              val playingFieldScene: PlayingFieldScene,
                              val playingField: Option[IPlayingField],
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {
  playingField.foreach(_.add(this))
  controller.add(this)
  this.getStylesheets.add(Styles.attackerHandSceneCss)
  val overlay = new Overlay(this)
  val getPlayingField: IPlayingField = playingField.get
  val gameStatusBar = new GameStatusBar
  val backgroundView = new Region {
    style = "-fx-background-color: black;"
  }

  val attackerHandBar: Option[SelectablePlayersHandBar] = playingField.map { pf =>
    val handBar = new SelectablePlayersHandBar(pf.getRoles.attacker, pf, isLeftSide = true)
    handBar.styleClass.add("selectable-hand-bar")
    handBar
  }

  // ✅ Back to Game button
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) {
    () =>
      controller.notifyObservers(Events.PlayingField)
  }
  backButton.styleClass.add("button")

  val regularSwapButton: Button = ActionButtonFactory.createRegularSwapButton(
    RegularSwapButton(),
    "Regular Swap",
    180,
    50,
    this,
    controller
  )
  val circularSwapButton: Button = ActionButtonFactory.createReverseSwapButton(
    ReverseSwapButton(),
    "Reverse Swap",
    180,
    50,
    this,
    controller
  )
  regularSwapButton.styleClass.add("button")
  // ✅ Button Layout
  val buttonLayout = new HBox {
    styleClass.add("button-layout")
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(regularSwapButton, circularSwapButton, backButton)
  }

  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = attackerHandBar.toSeq :+ buttonLayout
  }

  root = new StackPane {
    styleClass.add("attacker-hand-scene")
    children = Seq(backgroundView, layout, overlay.getPane) // ✅ Add Overlay to Scene
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 AttackerHandScene Received Event: $e")

      e match {
        case NoSwapsEvent(player) =>
          println(s"⚠️ ${player.name} has no Swaps left! Showing Alert in AttackerHandScene...")
          overlay.show(createSwapAlert(player), true) // ✅ Show alert in this scene only

        case _ =>
          SceneManager.update(e) // ✅ Handle other events normally
      }
    })
  }

  // ✅ Method to create an Alert for No Swaps Left
  private def createSwapAlert(player: IPlayer): Node = {
    GameAlertFactory.createAlert(s"${player.name} has no Swaps Left!", overlay, autoHide = false)
  }
}
