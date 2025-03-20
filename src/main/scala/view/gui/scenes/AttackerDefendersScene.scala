package view.gui.scenes

import scalafx.scene.control.Button
import controller.{Events, IController, NoBoostsEvent}
import model.cardComponent.ICard
import model.playingFiledComponent.IPlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneView.{BoostBar, GameStatusBar}
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.sceneManager.SceneManager
import scalafx.application.Platform
import view.gui.components.sceneView.cardBar.SelectablePlayersFieldBar
import view.gui.actionButtons.{ActionButtonFactory, BoostButton}
import view.gui.overlay.Overlay
import view.gui.utils.Styles
import scalafx.scene.Node
import scalafx.scene.text.Text
import model.playerComponent.IPlayer
import view.gui.components.comparison.GameAlertFactory
case class AttackerDefendersScene(
                                    controller: IController,
                                    playingFieldScene: PlayingFieldScene,
                                    playingField: Option[IPlayingField],
                                    windowWidth: Double,
                                    windowHeight: Double
                                  ) extends Scene(windowWidth, windowHeight) with Observer {

  playingField.foreach(_.add(this)) // ✅ Add observer to receive eventsv
  controller.add(this)
  var getPlayingField: IPlayingField = playingField.get
  val gameStatusBar = new GameStatusBar

  val backgroundView = new Region {
    style = "-fx-background-color: black;"
  }

  val attackerDefenderField: Option[SelectablePlayersFieldBar] = playingField.map { pf =>
    val fieldBar = new SelectablePlayersFieldBar(pf.getRoles.attacker, pf)
    fieldBar.styleClass.add("selectable-field-bar")
    fieldBar
  }

  // ✅ Overlay for Alerts
  val overlay = new Overlay(this)

  // ✅ Back to Game button
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

  // ✅ Boost Button
  val boostButton: Button = ActionButtonFactory.createBoostButton(
    BoostButton(),
    "Boost Card",
    180,
    50,
    this,
    controller
  )
  boostButton.styleClass.add("button")

  // ✅ Button Layout
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
