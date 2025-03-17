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

  private var _mainMenuScene: Option[MainMenuScene] = None
  private var _createPlayerScene: Option[CreatePlayerScene] = None
  private var _playingFieldScene: Option[PlayingFieldScene] = None
  private var _attackerHandScene: Option[AttackerHandScene] = None
  private var _attackerDefendersScene: Option[AttackerDefendersScene] = None
  private var _menuScene: Option[MenuScene] = None
  private var _loadGameScene: Option[LoadGameScene] = None

  private def clearScenes(): Unit = {
    if (_createPlayerScene.isEmpty && _playingFieldScene.isEmpty) {
      return
    }


    _createPlayerScene = None
    _playingFieldScene = None
    _attackerHandScene = None
    _attackerDefendersScene = None
    _menuScene = None
    _loadGameScene = None

  }


  def getMainMenuScene: MainMenuScene = {
    clearScenes()
    if (_mainMenuScene.isEmpty) {
      _mainMenuScene = Some(new MainMenuScene(controller))
    }
    _mainMenuScene.get
  }


  def getLoadGameScene: LoadGameScene = {
    if (_loadGameScene.isEmpty) {
      _loadGameScene = Some(new LoadGameScene(controller))
    }
    _loadGameScene.get
  }

  def getCreatePlayerScene: CreatePlayerScene = {
    if (_createPlayerScene.isEmpty) {
      _createPlayerScene = Some(new CreatePlayerScene(controller))
    }
    _createPlayerScene.get
  }
  
  def getPlayingFieldScene: PlayingFieldScene = {
    if (_playingFieldScene.isEmpty) {
      _playingFieldScene = Some(AttackerSceneFactory.createPlayingFieldScene(controller))
    }
    _playingFieldScene.get
  }


  def getAttackerDefendersScene: AttackerDefendersScene = {
    _attackerDefendersScene = Some(
      AttackerSceneFactory.createAttackerDefendersScene(
        controller, getPlayingFieldScene, Option(controller.getCurrentGame.getPlayingField), 800, 600
      )
    )
    _attackerDefendersScene.get
  }

  def getAttackerHandScene: AttackerHandScene = {
      _attackerHandScene = Some(AttackerSceneFactory.createAttackerHandScene(
        controller,
        getPlayingFieldScene,
        Option(controller.getCurrentGame.getPlayingField),
        800, 600
      ))
      _attackerHandScene.get
  }


  def getMenuScene: MenuScene = {
    if (_menuScene.isEmpty) {
      _menuScene = Some(MenuScene(controller, getPlayingFieldScene, sceneManager))
    }
    _menuScene.get
  }
}
