package de.htwg.se.soccercardclash.view.gui.components.sceneComponents

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.util.{GameActionEvent, GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.gui.components.dialog.DialogFactory
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import de.htwg.se.soccercardclash.view.gui.components.uiFactory.GameButtonFactory
import de.htwg.se.soccercardclash.view.gui.overlay.Overlay
import de.htwg.se.soccercardclash.view.gui.scenes.PlayingFieldScene
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.application.Platform

class MenuButtonBar(controller: IController, playingFieldScene: PlayingFieldScene, overlay: Overlay) extends VBox {

  val continueButton: Button = GameButtonFactory.createGameButton(
    text = "Continue",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
  }

  val undoButton: Button = GameButtonFactory.createGameButton(
    text = "Undo",
    width = 180,
    height = 60
  ) { () =>
    controller.undo(playingFieldScene.contextHolder.get)
    playingFieldScene.update(GameActionEvent.Undo)
    playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.UNDO_PERFORMED)
  }

  val redoButton: Button = GameButtonFactory.createGameButton(
    text = "Redo",
    width = 180,
    height = 60
  ) { () =>
    controller.redo(playingFieldScene.contextHolder.get)
    playingFieldScene.update(GameActionEvent.Redo)
    playingFieldScene.gameStatusBar.updateStatus(GameStatusMessages.REDO_PERFORMED)
  }

  val saveGameButton: Button = GameButtonFactory.createGameButton(
    text = "Save Game",
    width = 180,
    height = 60
  ) { () =>
    controller.saveGame(playingFieldScene.contextHolder.get)
    DialogFactory.showGameSavedDialog(overlay,false)
  }

  val mainMenuButton: Button = GameButtonFactory.createGameButton(
    text = "Main Menu",
    width = 180,
    height = 60
  ) { () =>
    overlay.hide()
    Future {
      Thread.sleep(300)
      Platform.runLater {
        playingFieldScene.contextHolder.clear()
        PlayerAvatarRegistry.clear()
        GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
      }
    }
  }
}
