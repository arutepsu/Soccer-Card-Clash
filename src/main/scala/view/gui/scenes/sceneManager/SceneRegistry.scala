package view.gui.scenes.sceneManager
import controller.IController
import controller.Events
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.util.Duration
import util.{Observable, ObservableEvent, Observer}
import view.gui.scenes._
import view.gui.scenes.CreatePlayerScene

class SceneRegistry(controller: IController, sceneManager: SceneManager.type) {

  private var _mainMenuScene: Option[Scene] = None
  private var _createPlayerScene: Option[Scene] = None
  private var _playingFieldScene: Option[PlayingFieldScene] = None
  private var _attackerHandScene: Option[Scene] = None
  private var _attackerDefendersScene: Option[Scene] = None
  private var _menuScene: Option[Scene] = None

  def getMainMenuScene: Scene = {
    if (_mainMenuScene.isEmpty) {
      _mainMenuScene = Some(new MainMenuScene(controller).mainMenuScene())
    }
    _mainMenuScene.get
  }

  def getCreatePlayerScene: Scene = {
    if (_createPlayerScene.isEmpty) {
      _createPlayerScene = Some(new Scene { root = new CreatePlayerScene(controller) })
    }
    _createPlayerScene.get
  }

  def getPlayingFieldScene: PlayingFieldScene = {
    if (_playingFieldScene.isEmpty) {
      _playingFieldScene = Some(new PlayingFieldScene(controller, 800, 600))
    }
    _playingFieldScene.get
  }

  def getAttackerDefendersScene: Scene = {
    // 🔄 Always create a fresh instance to ensure UI updates when roles switch
    _attackerDefendersScene = Some(AttackerSceneFactory.createAttackerDefendersScene(controller, Option(controller.getPlayingField), 800, 600))
    _attackerDefendersScene.get
  }

  def getAttackerHandScene: Scene = {
    // 🔄 Always create a fresh instance for the hand scene as well
    _attackerHandScene = Some(AttackerSceneFactory.createAttackerHandScene(controller, Option(controller.getPlayingField), 800, 600))
    _attackerHandScene.get
  }

  def getMenuScene: Scene = {
    if (_menuScene.isEmpty) {
      _menuScene = Some(MenuScene(controller, getPlayingFieldScene, sceneManager))
    }
    _menuScene.get
  }
}
