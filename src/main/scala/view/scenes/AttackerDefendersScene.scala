package view.scenes

import scalafx.scene.control.Button
import controller.{ControllerEvents, IController}
import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.components.gameComponents.{BoostBar, GameStatusBar, SelectablePlayersFieldBar}
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import model.cardComponent.base.Card
import scalafx.application.Platform
import view.scenes.action.{ActionButtonFactory, BoostButton}
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
  val gameStatusBar = new GameStatusBar
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

  val boostButton: Button = ActionButtonFactory.createBoostButton(
    BoostButton(), // ✅ Use the HandSwapButton action
    "Boost Card",
    180,
    50,
    this,
    controller // ✅ Pass the current scene (AttackerHandScene)
  )

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
