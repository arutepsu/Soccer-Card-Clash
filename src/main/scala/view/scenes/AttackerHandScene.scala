package view.scenes

import controller.{ControllerEvents, IController}
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{Observer, ObservableEvent}
import view.components.gameComponents.SelectablePlayersHandBar
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import view.utils.Styles
import scalafx.application.Platform
case class AttackerHandScene(
                              controller: IController,
                              playingField: Option[PlayingField],
                              windowWidth: Double,
                              windowHeight: Double,
                            ) extends Scene(windowWidth, windowHeight) with Observer {

  this.getStylesheets.add(Styles.attackerHandSceneCss) // ✅ Load external CSS

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
  val swapButton: Button = GameButtonFactory.createGameButton(
    text = "Swap Card",
    width = 180,
    height = 50
  ) { () =>
    attackerHandBar match {
      case Some(handBar) =>
        handBar.selectedCardIndex match {
          case Some(index) =>
            println(s"🔄 Swapping card at index: $index")
            controller.swapAttackerCard(index) // ✅ Swap logic
            playingField.foreach(_.notifyObservers()) // ✅ Ensure UI updates only if playingField exists
            handBar.updateBar() // ✅ Refresh UI after swapping
          case None =>
            println("❌ No card selected to swap!")
        }
      case None => println("❌ No valid attacker hand available!")
    }
  }
  swapButton.styleClass.add("button") // ✅ Apply styling

  // ✅ Button Layout
  val buttonLayout = new HBox {
    styleClass.add("button-layout") // ✅ Apply styling
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(swapButton, backButton)
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
