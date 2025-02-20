package view.gui.components.sceneBar

import controller.IController
import view.gui.scenes.PlayingFieldScene
import view.gui.components.sceneBar.GameStatusBar
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.action._
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


  val undoButton: Button = GameButtonFactory.createGameButton(
    text = "Undo",
    width = 150,
    height = 50
  ) { () =>
    controller.undo()
    playingFieldScene.updateDisplay()
    gameStatusBar.updateStatus(GameStatusMessages.UNDO_PERFORMED)
  }

  val redoButton: Button = GameButtonFactory.createGameButton(
    text = "Redo",
    width = 150,
    height = 50
  ) { () =>
    controller.redo()
    playingFieldScene.updateDisplay()
    gameStatusBar.updateStatus(GameStatusMessages.REDO_PERFORMED)
  }

  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.MainMenu)
  }

  val showDefendersButton: Button = GameButtonFactory.createGameButton(
    text = "Show Defenders",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttackerDefenderCards)
  }

  val makeSwapButton: Button = GameButtonFactory.createGameButton(
    text = "Make Swap",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.AttckerHandCards)
  }

  children = Seq(
    singleAttackButton,
    doubleAttackButton,
    undoButton,
    redoButton,
    mainMenuButton,
    showDefendersButton,
    makeSwapButton)
}
