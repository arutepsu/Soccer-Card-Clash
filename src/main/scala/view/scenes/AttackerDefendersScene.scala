package view.scenes

import scalafx.scene.control.Button
import controller.{ControllerEvents, IController}
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{Observer, ObservableEvent}
import view.components.gameComponents.{BoostBar, SelectablePlayersFieldBar}
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import model.cardComponent.base.Card
import scalafx.application.Platform
case class AttackerDefendersScene(
                                   controller: IController,  // ✅ Added Controller
                                   playingField: Option[PlayingField],
                                   windowWidth: Double,
                                   windowHeight: Double,
                                 ) extends Scene(windowWidth, windowHeight) with Observer {

  // ✅ Ensure playingField is not null before accessing it
  val attackerDefenderField: Option[SelectablePlayersFieldBar] = playingField.map { pf =>
    new SelectablePlayersFieldBar(pf.getAttacker, pf)
  }

  val playerGoalkeeper: Option[Card] = attackerDefenderField.flatMap(_.getGoalkeeperCard)
//  val playerDefenders: Seq[Card] = attackerDefenderField.map(_.getDefenderCards).getOrElse(Seq())
  val playerDefenders: Option[Seq[Card]] = attackerDefenderField.map(_.getDefenderCards)

  // ✅ Create a black background
  val backgroundView = new Region {
    style = "-fx-background-color: black;"
  }

  // ✅ Back to Game button
  val backButton: Button = GameButtonFactory.createGameButton(
    text = "Back to Game",
    width = 180,
    height = 50
  ) {
    () =>
      controller.notifyObservers(ControllerEvents.PlayingField)
  }

  val boostButton: Button = GameButtonFactory.createGameButton(
    text = "Boost",
    width = 180,
    height = 50
  ) {
    () =>
      attackerDefenderField match {
        case Some(field) =>
          if (field.isGoalkeeperSelected) { // ✅ Boost goalkeeper if selected
            println("⚽ Boosting Goalkeeper!")
            controller.boostGoalkeeper()
          } else field.selectedCardIndex match { // ✅ Boost only the selected defender
            case Some(index) =>
              println(s"⚡ Boosting defender at index: $index")
              controller.boostDefender(index)
            case None =>
              println("⚠️ No defender selected for boosting!")
          }

          // ✅ Notify Observers & Refresh UI if playingField exists
          playingField.foreach(_.notifyObservers())
          field.updateBar()

        case None => println("❌ No valid attacker/defender field to boost!")
      }
  }

  // ✅ Button Layout (Includes BoostBar and Back Button)
  val buttonLayout = new HBox {
    alignment = Pos.CENTER
    spacing = 15
    children = Seq(boostButton, backButton)
  }

  // ✅ Main Layout (Displays Attacker's Field + Buttons) only if playingField is valid
  val layout = new VBox {
    alignment = Pos.CENTER
    spacing = 20
    padding = Insets(20)
    children = attackerDefenderField.toSeq :+ buttonLayout
  }

  // ✅ Apply background to root
  root = new StackPane {
    children = Seq(
      backgroundView, // ✅ Black Background
      layout
    )
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")

      // ✅ Instead of switching scenes manually, notify SceneManager
      SceneManager.update(e)
    })
  }
}
