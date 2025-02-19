package view.scenes

import scalafx.scene.control.Button
import controller.{ControllerEvents, IController}
import model.cardComponent.ICard
import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, Region, StackPane, VBox}
import util.{ObservableEvent, Observer}
import view.components.gameComponents.{BoostBar, GameStatusBar, SelectablePlayersFieldBar}
import view.components.uiFactory.GameButtonFactory
import view.scenes.sceneManager.SceneManager
import scalafx.application.Platform
import view.scenes.action.{ActionButtonFactory, BoostButton}
case class AttackerDefendersScene(
                                   controller: IController,  // ✅ Added Controller
                                   playingField: Option[PlayingField],
                                   windowWidth: Double,
                                   windowHeight: Double,
                                 ) extends Scene(windowWidth, windowHeight) with Observer {

  val attackerDefenderField: Option[SelectablePlayersFieldBar] = playingField.map { pf =>
    new SelectablePlayersFieldBar(pf.getAttacker, pf)
  }
  val gameStatusBar = new GameStatusBar
  val playerGoalkeeper: Option[ICard] = attackerDefenderField.flatMap(_.getGoalkeeperCard)
  val playerDefenders: Option[Seq[ICard]] = attackerDefenderField.map(_.getDefenderCards)

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
    BoostButton(),
    "Boost Card",
    180,
    50,
    this,
    controller
  )

  val buttonLayout = new HBox {
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
    children = Seq(
      backgroundView,
      layout
    )
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 GUI Received Event: $e")

      SceneManager.update(e)
    })
  }
}
