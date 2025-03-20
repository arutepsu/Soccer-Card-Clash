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
  private var _loadGameScene: Option[LoadGameScene] = None

  private def clearScenes(): Unit = {
    if (_createPlayerScene.isEmpty && _playingFieldScene.isEmpty) {
      return
    }
    _createPlayerScene = None
    _playingFieldScene = None
    _attackerHandScene = None
    _attackerDefendersScene = None
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
      _playingFieldScene = Some(new PlayingFieldScene(controller, 800, 600))
    }
    _playingFieldScene.get
  }


  def getAttackerDefendersScene: AttackerDefendersScene = {
    // 🗑️ Delete old instance before creating a new one
    clearHandAndDefenderScenes()

    _attackerDefendersScene = Some(new AttackerDefendersScene(
      controller,
      getPlayingFieldScene,
      Some(controller.getCurrentGame.getPlayingField),
      800,
      600
    ))
    controller.add(_attackerDefendersScene.get)
    _attackerDefendersScene.get
  }

  def getAttackerHandScene: AttackerHandScene = {
    // 🗑️ Delete old instance before creating a new one
    clearHandAndDefenderScenes()

    _attackerHandScene = Some(new AttackerHandScene(
      controller,
      getPlayingFieldScene,
      Some(controller.getCurrentGame.getPlayingField),
      800,
      600
    ))
    controller.add(_attackerHandScene.get)
    _attackerHandScene.get
  }

  def clearHandAndDefenderScenes(): Unit = {
    if (_attackerHandScene.nonEmpty || _attackerDefendersScene.nonEmpty) {
      println("🗑️ DEBUG: Removing AttackerHandScene and AttackerDefendersScene from memory and observers")

      // 🔥 Remove from observers list BEFORE setting to None
      _attackerHandScene.foreach { scene =>
        if (controller.subscribers.contains(scene)) {
          println(s"❌ Removing observer: ${scene.getClass.getSimpleName}")
          controller.remove(scene)
        }
      }

      _attackerDefendersScene.foreach { scene =>
        if (controller.subscribers.contains(scene)) {
          println(s"❌ Removing observer: ${scene.getClass.getSimpleName}")
          controller.remove(scene)
        }
      }

      // 🔥 Set instances to None (delete from memory)
      _attackerHandScene = None
      _attackerDefendersScene = None

      println("✅ DEBUG: AttackerHandScene and AttackerDefendersScene cleared!")
    }
  }




}
