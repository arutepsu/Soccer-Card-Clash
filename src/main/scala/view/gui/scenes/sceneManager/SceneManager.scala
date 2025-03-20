package view.gui.scenes.sceneManager

import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import controller.{Events, IController}
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.util.Duration
import util.{Observable, ObservableEvent, Observer}
import view.gui.components.comparison.PauseMenu
import view.gui.scenes.*

object SceneManager extends Observable with Observer {
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800
  private var lastSceneHeight: Double = 600
  var currentScene: Option[Scene] = None
  private var controller: IController = _
  var sceneRegistry: SceneRegistry = _

  def init(primaryStage: Stage, ctrl: IController): Unit = {
    stage = primaryStage
    controller = ctrl
    controller.add(this)

    sceneRegistry = new SceneRegistry(controller, this)
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      if (
        (currentScene.exists(_.getClass == classOf[MainMenuScene]) && e == Events.MainMenu) ||
          (currentScene.exists(_.getClass == classOf[CreatePlayerScene]) && e == Events.CreatePlayers) ||
          (currentScene.exists(_.getClass == classOf[PlayingFieldScene]) && e == Events.PlayingField) ||
          (currentScene.exists(_.getClass == classOf[AttackerHandScene]) && e == Events.AttackerHandCards) ||
          (currentScene.exists(_.getClass == classOf[AttackerDefendersScene]) && e == Events.AttackerDefenderCards) ||
          (currentScene.exists(_.getClass == classOf[PauseMenu]) && e == Events.PauseGame) ||
          (currentScene.exists(_.getClass == classOf[LoadGameScene]) && e == Events.LoadGame)
      ) {
        return
      }

      e match {
        case Events.MainMenu => switchScene(sceneRegistry.getMainMenuScene)
        case Events.CreatePlayers => switchScene(sceneRegistry.getCreatePlayerScene)
        case Events.PlayingField => switchScene(sceneRegistry.getPlayingFieldScene)
        case Events.AttackerHandCards => switchScene(sceneRegistry.getAttackerHandScene)
        case Events.AttackerDefenderCards => switchScene(sceneRegistry.getAttackerDefendersScene)
        case Events.LoadGame => switchScene(sceneRegistry.getLoadGameScene)
        case Events.Quit => controller.quit()
        case _ =>
      }
    })
  }


  private def applySceneSize(): Unit = {
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight
  }

  def refreshCurrentScene(): Unit = {
    Platform.runLater(() => {
      currentScene.foreach(_.root.value.requestLayout())
      notifyObservers()
    })
  }

  def switchScene(newScene: Scene): Unit = {
    
    if (currentScene.contains(newScene)) {
      return
    }

    Platform.runLater(() => {
      val oldSceneOpt = currentScene

      oldSceneOpt match {
        case Some(oldScene: Observer) if controller.subscribers.contains(oldScene) =>
          controller.remove(oldScene)
        case _ =>
      }

      lastSceneWidth = stage.width()
      lastSceneHeight = stage.height()

      val fadeOut = oldSceneOpt match {
        case Some(oldScene) if oldScene.root.value != null =>
          val fadeOutTransition = new FadeTransition(Duration(200), oldScene.root.value)
          fadeOutTransition.toValue = 0.2
          fadeOutTransition
        case _ => null
      }

      if (fadeOut != null) {
        fadeOut.play()
        fadeOut.setOnFinished(_ => applySceneTransition(newScene))
      } else {
        applySceneTransition(newScene)
      }
    })
  }

  private def applySceneTransition(newScene: Scene): Unit = {
    Platform.runLater(() => {
      stage.scene = newScene
      currentScene = Some(newScene)
      applySceneSize()

      newScene match {
        case newObserverScene: Observer if !controller.subscribers.contains(newObserverScene) =>
          println(s"✅ Adding observer: ${newObserverScene.getClass.getSimpleName}")
          controller.add(newObserverScene)
        case _ =>
      }

      if (newScene.root.value != null) {
        val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
        fadeIn.fromValue = 0.2
        fadeIn.toValue = 1.0
        fadeIn.play()
      }

      notifyObservers()
    })
  }
}