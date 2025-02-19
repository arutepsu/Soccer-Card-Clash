package view.scenes

import controller.{ControllerEvents, IController}
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.components.gameComponents.{GameStatusBar, SelectablePlayersHandBar}
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import view.utils.Styles
import scalafx.application.Platform
import view.scenes.action.{ActionButtonFactory, CircularSwapButton, RegularSwapButton}
case class AttackerHandScene(
                              controller: IController,
                              playingField: Option[PlayingField],
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {

  this.getStylesheets.add(Styles.attackerHandSceneCss) // ✅ Load external CSS
  var getPlayingField: PlayingField = playingField.get
  val gameStatusBar = new GameStatusBar
  // ✅ Create a black background
  val backgroundView = new Region {
    style = "-fx-background-color: black;"
  }

  // ✅ Display attacker's selectable hand only if playingField exists
  val attackerHandBar: Option[SelectablePlayersHandBar] = playingField.map { pf =>
    val handBar = new SelectablePlayersHandBar(pf.getAttacker, pf, isLeftSide = true)
    handBar.styleClass.add("selectable-hand-bar") // ✅ Apply styling
    handBar
  }

  // ✅ Back to Game button
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) {
    () =>
      controller.notifyObservers(ControllerEvents.PlayingField) // ✅ Fixed typo 'notifayObservers'
  }
  backButton.styleClass.add("button") // ✅ Apply styling

  // ✅ Swap Card Button
  val regularSwapButton: Button = ActionButtonFactory.createRegularSwapButton(
    RegularSwapButton(), // ✅ Use the HandSwapButton action
    "Regular Swap",
    180,
    50,
    this,
    controller// ✅ Pass the current scene (AttackerHandScene)
  )
//  circularSwapButton.styleClass.add("button") // ✅ Apply styling
  val circularSwapButton: Button = ActionButtonFactory.createCircularSwapButton(
    CircularSwapButton(), // ✅ Use the HandSwapButton action
    "Circular Swap",
    180,
    50,
    this,
    controller // ✅ Pass the current scene (AttackerHandScene)
  )
  regularSwapButton.styleClass.add("button") // ✅ Apply styling
  // ✅ Button Layout
  val buttonLayout = new HBox {
    styleClass.add("button-layout") // ✅ Apply styling
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(regularSwapButton, circularSwapButton, backButton)
  }

  // ✅ Main Layout (Only add attackerHandBar if it exists)
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = attackerHandBar.toSeq :+ buttonLayout
  }

  root = new StackPane {
    styleClass.add("attacker-hand-scene") // ✅ Apply main styling to root
    children = Seq(backgroundView, layout)
  }


  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")

      // ✅ Instead of switching scenes manually, notify SceneManager
      SceneManager.update(e)
    })
  }
}
