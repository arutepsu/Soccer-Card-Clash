package view.gui.components.sceneView

import controller.IController
import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneView.GameStatusBar
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.actionButtons._
import controller.Events
import model.playingFiledComponent.IPlayingField

case class ButtonBar(controller: IController, playingField: IPlayingField, playingFieldScene: PlayingFieldScene, gameStatusBar: GameStatusBar) extends VBox {


  val singleAttackButton: Button = ActionButtonFactory.createAttackButton(
    SingleButton(),
    "Attack",
    150,
    50,
    playingFieldScene,
    controller
  )
  val doubleAttackButton: Button = ActionButtonFactory.createDoubleAttackButton(
    DoubleButton(),
    "Double Attack",
    150,
    50,
    playingFieldScene,
    controller)

  alignment = Pos.CENTER_LEFT
  spacing = 10
  padding = Insets(20)


  val showDefendersButton: Button = GameButtonFactory.createGameButton(
    text = "Show Defenders",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttackerDefenderCards)
  }
  val pause: Button = GameButtonFactory.createGameButton(
    text = "Pause",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.PauseGame)
  }


  val makeSwapButton: Button = GameButtonFactory.createGameButton(
    text = "Make Swap",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttackerHandCards)
  }

  children = Seq(
    pause,
    singleAttackButton,
    doubleAttackButton,
    showDefendersButton,
    makeSwapButton)
}
