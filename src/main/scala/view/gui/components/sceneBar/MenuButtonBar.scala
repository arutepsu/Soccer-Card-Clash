package view.gui.components.sceneBar

import controller.{Events, IController}
import model.playingFiledComponent.IPlayingField
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import view.gui.components.uiFactory.GameButtonFactory
import view.gui.scenes.PlayingFieldScene
import view.gui.scenes.sceneManager.SceneManager

class MenuButtonBar(controller: IController, playingFieldScene: PlayingFieldScene, sceneManager: SceneManager.type ) extends VBox {

  val continueButton: Button = GameButtonFactory.createGameButton(
    text = "Continue",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.PlayingField)
  }

  val undoButton: Button = GameButtonFactory.createGameButton(
    text = "Undo",
    width = 150,
    height = 50
  ) { () =>
    controller.undo()
    playingFieldScene.update(Events.Undo)
    playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.UNDO_PERFORMED)
  }

  val redoButton: Button = GameButtonFactory.createGameButton(
    text = "Redo",
    width = 150,
    height = 50
  ) { () =>
    controller.redo()
    playingFieldScene.update(Events.Redo)
//    playingFieldScene.updateDisplay()
    playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REDO_PERFORMED)
  }

  val saveGameButton: Button = GameButtonFactory.createGameButton(
    text = "Save Game",
    width = 180,
    height = 50
  ) { () =>
    controller.saveGame()
  }

  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 50
  ) { () =>
    controller.notifyObservers(Events.MainMenu)
    controller.resetGame()
  }
}
