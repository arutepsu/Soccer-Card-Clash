package view.gui.scenes

import controller.{Events, IController, NoSwapsEvent}
import model.playingFiledComponent.IPlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneView.GameStatusBar
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.sceneManager.SceneManager
import view.gui.overlay.Overlay
import view.gui.utils.Styles
import scalafx.scene.Node
import scalafx.scene.text.Text
import scalafx.application.Platform
import model.playerComponent.IPlayer
import view.gui.components.sceneView.cardBar.SelectablePlayersHandBar
import view.gui.actionButtons.{ActionButtonFactory, RegularSwapButton, ReverseSwapButton}
import view.gui.components.comparison.GameAlertFactory
case class AttackerHandScene(
                              controller: IController,
                              playingFieldScene: PlayingFieldScene,
                              playingField: Option[IPlayingField],
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {
  playingField.foreach(_.add(this))
  controller.add(this)
  this.getStylesheets.add(Styles.attackerHandSceneCss)
  val overlay = new Overlay(this)
  var getPlayingField: IPlayingField = playingField.get
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
