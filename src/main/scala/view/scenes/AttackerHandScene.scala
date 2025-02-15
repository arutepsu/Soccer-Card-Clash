package view.scenes

import controller.IController
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{StackPane, VBox, HBox, Region}
import view.components.gameComponents.SelectablePlayersHandBar
import view.components.uiFactory.GameButtonFactory
import view.utils.Styles

case class AttackerHandScene(
                              controller: IController,
                              playingField: PlayingField,
                              windowWidth: Double,
                              windowHeight: Double,
                              moveToGamePlayerScene: () => Unit
                            ) extends Scene(windowWidth, windowHeight) {

  this.getStylesheets.add(Styles.attackerHandSceneCss) // ✅ Load external CSS

  // ✅ Create a black background
  val backgroundView = new Region {
    style = "-fx-background-color: black;"
    prefWidth = windowWidth
    prefHeight = windowHeight
  }

  // ✅ Display attacker's selectable hand
  val attackerHandBar = new SelectablePlayersHandBar(playingField.getAttacker, playingField, isLeftSide = true)
  attackerHandBar.styleClass.add("selectable-hand-bar") // ✅ Apply styling

  // ✅ Back to Game button
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) { () =>
    moveToGamePlayerScene()
  }
  backButton.styleClass.add("button") // ✅ Apply styling

  // ✅ Swap Card Button
  val swapButton: Button = GameButtonFactory.createGameButton(
    text = "Swap Card",
    width = 180,
    height = 50
  ) { () =>
    attackerHandBar.selectedCardIndex match {
      case Some(index) =>
        println(s"🔄 Swapping card at index: $index")
        controller.swapAttackerCard(index) // ✅ Swap logic
        playingField.notifyObservers() // ✅ Ensure UI updates
        attackerHandBar.updateHand() // ✅ Refresh UI after swapping
      case None =>
        println("❌ No card selected to swap!")
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

  // ✅ Main Layout
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = Seq(attackerHandBar, buttonLayout)
  }

  root = new StackPane {
    styleClass.add("attacker-hand-scene") // ✅ Apply main styling to root
    children = Seq(backgroundView, layout)
  }
}
