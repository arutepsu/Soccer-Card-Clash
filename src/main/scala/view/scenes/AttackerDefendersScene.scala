package view.scenes
import scalafx.scene.control.Button
import controller.Controller
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{StackPane, VBox, HBox, Region}
import view.components.gameComponents.{SelectablePlayersFieldBar, BoostBar}
import view.components.uiFactory.GameButtonFactory

case class AttackerDefendersScene(
                                   playingField: PlayingField,
                                   windowWidth: Double,
                                   windowHeight: Double,
                                   moveToGamePlayerScene: () => Unit
                                 ) extends Scene(windowWidth, windowHeight) {

  // ✅ Display attacker's field (Persistent instance)
  val playerDefenders = new SelectablePlayersFieldBar(playingField.getAttacker, playingField)

  // ✅ Create a black background
  val backgroundView = new Region {
    style = "-fx-background-color: black;"
    prefWidth = windowWidth
    prefHeight = windowHeight
  }

  // ✅ Back to Game button
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) { () =>
    moveToGamePlayerScene() // ✅ Switch back to GamePlayerScene
  }
  val boostButton: Button = GameButtonFactory.createGameButton(
    text = "Boost",
    width = 180,
    height = 50
  ) { () =>
    playerDefenders.selectedCardIndex match {
      case Some(index) =>
        println(s"⚡ Boosting card at index: $index")
        playingField.chooseBoostCard(index) // ✅ Apply game logic for boosting
        playerDefenders.updateField() // ✅ Refresh UI after boost
      case None =>
        println("❌ No card selected to boost!")
    }
  }



  // ✅ Button Layout (Includes BoostBar and Back Button)
  val buttonLayout = new HBox {
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(boostButton, backButton)
  }

  // ✅ Main Layout (Displays Attacker's Field + Buttons)
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = Seq(playerDefenders, buttonLayout)
  }

  // ✅ Apply background to root
  root = new StackPane {
    children = Seq(
      backgroundView, // ✅ Black Background
      layout
    )
  }
}


//  // ✅ Special Actions Bar (e.g., Boost Action)
//  val specialActionsBar = new SpecialActionsBar(controller)
//  val backgroundViewSpecial = new HBox {
//    alignment = Pos.CENTER_LEFT
//    spacing = 20
//    children = Seq(specialActionsBar)
//  }
//
//  // ✅ Boost Action (Updates Defender in the Background)
//  specialActionsBar.setBoostAction { () =>
//    println("🔋 Boosting a card!")
//
//    val defenderCards = playingField.playerDefenders(playingField.getDefender)
//
//    if (defenderCards.nonEmpty) {
//      val defenderFieldBar = new PlayersFieldBar(playingField.getDefender, playingField) // Temp instance
//      defenderFieldBar.selectedDefenderIndex match {
//        case Some(defenderIndex) =>
//          println(s"🔥 Boosting defender at index: $defenderIndex")
//          controller.boostCard(defenderIndex)
//
//          // ✅ Ensure only the attacker's view updates (defender is updated in the model)
//          playerDefenders.updateField()
//          defenderFieldBar.resetSelectedDefender() // Ensure defender selection resets in the background
//
//        case None =>
//          println("⚠️ No defender selected for boost!")
//      }
//    } else {
//      println("⚠️ No defenders available to boost!")
//    }
//  }
