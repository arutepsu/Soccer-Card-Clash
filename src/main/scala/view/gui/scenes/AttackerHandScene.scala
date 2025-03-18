package view.gui.scenes

import controller.{Events, IController}
import model.playingFiledComponent.IPlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.gui.components.sceneView.GameStatusBar
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.sceneManager.SceneManager
import view.gui.utils.Styles
import scalafx.application.Platform
import view.gui.components.sceneView.cardBar.SelectablePlayersHandBar
import view.gui.actionButtons.{ActionButtonFactory, CircularSwapButton, RegularSwapButton}
case class AttackerHandScene(
                              controller: IController,
                              playingFieldScene: PlayingFieldScene,
                              playingField: Option[IPlayingField],
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {
  controller.add(this)
  this.getStylesheets.add(Styles.attackerHandSceneCss)
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
      playingFieldScene.updateDisplay()
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
  val circularSwapButton: Button = ActionButtonFactory.createCircularSwapButton(
    CircularSwapButton(),
    "Circular Swap",
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
    children = Seq(backgroundView, layout)
  }


  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")

      SceneManager.update(e)
    })
  }
}
