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
  private var _loadGameScene: Option[Scene] = None

  private def clearScenes(): Unit = {
    println("Clearing all scenes...")

    _createPlayerScene = None
    println("✅ Cleared _createPlayerScene")

    _playingFieldScene = None
    println("✅ Cleared _playingFieldScene")

    _attackerHandScene = None
    println("✅ Cleared _attackerHandScene")

    _attackerDefendersScene = None
    println("✅ Cleared _attackerDefendersScene")

    _menuScene = None
    println("✅ Cleared _menuScene")

    _loadGameScene = None
    println("✅ Cleared _loadGameScene")

    println("Resetting controller...")
//    controller.reset()
    println("✅ Controller reset completed")
  }

  def getMainMenuScene: Scene = {
    clearScenes()
    if (_mainMenuScene.isEmpty) {
      _mainMenuScene = Some(new MainMenuScene(controller).mainMenuScene())
    }
    _mainMenuScene.get
  }

  def getLoadGameScene: Scene = {
    // ✅ Always create a new instance to ensure the scene refreshes correctly
    _loadGameScene = Some(new LoadGameScene(controller))
    _loadGameScene.get
  }
  def getCreatePlayerScene: Scene = {
    _createPlayerScene = Some(new Scene { root = new CreatePlayerScene(controller) })
    _createPlayerScene.get
  }

  def getPlayingFieldScene: PlayingFieldScene = {
    if (_playingFieldScene.isEmpty) {
      _playingFieldScene = Some(new PlayingFieldScene(controller, 800, 600))
    }
    _playingFieldScene.get
  }

  def getAttackerDefendersScene: Scene = {
    _attackerDefendersScene = Some(AttackerSceneFactory.createAttackerDefendersScene(controller, Option(controller.getPlayingField), 800, 600))
    _attackerDefendersScene.get
  }

  def getAttackerHandScene: Scene = {
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
