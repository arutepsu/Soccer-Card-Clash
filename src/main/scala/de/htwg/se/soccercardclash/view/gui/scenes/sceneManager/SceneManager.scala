package de.htwg.se.soccercardclash.view.gui.scenes.sceneManager

import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import de.htwg.se.soccercardclash.controller.{Events, IController}
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.util.Duration
import de.htwg.se.soccercardclash.util.{Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.dialog.PauseDialog
import de.htwg.se.soccercardclash.view.gui.scenes.*

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

  import scala.util.boundary, boundary.break

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      val sceneMatchesEvent = currentScene.exists {
        case _: MainMenuScene if e == Events.MainMenu => true
        case _: CreatePlayerScene if e == Events.CreatePlayers => true
        case _: PlayingFieldScene if e == Events.PlayingField => true
        case _: AttackerHandScene if e == Events.AttackerHandCards => true
        case _: AttackerDefendersScene if e == Events.AttackerDefenderCards => true
//        case _: PauseDialog if e == Events.PauseGame => true
        case _: LoadGameScene if e == Events.LoadGame => true
        case _ => false
      }

      if (!sceneMatchesEvent) { // ✅ Skip scene switch only if event matches current scene
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
      // 🗑️ If switching to Hand or Defender scenes, delete old instances
      newScene match {
        case _: AttackerHandScene | _: AttackerDefendersScene =>
          println("🗑️ DEBUG: Clearing old Hand & Defender scene instances")
          sceneRegistry.clearHandAndDefenderScenes()
        case _ =>
      }

      // 🗑️ If switching back to PlayingFieldScene, also delete Hand & Defender scenes
      if (newScene.isInstanceOf[PlayingFieldScene]) {
        println("🗑️ DEBUG: Clearing AttackerHandScene and AttackerDefendersScene instances")
        sceneRegistry.clearHandAndDefenderScenes()
      }
      // 🔥 Remove previous observer before switching
      oldSceneOpt.foreach {
        case oldObserver: Observer if controller.subscribers.contains(oldObserver) =>
          println(s"❌ Removing observer: ${oldObserver.getClass.getSimpleName}")
          controller.remove(oldObserver)
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

      // 🔥 Ensure old instances of the same scene are removed before adding the new one
      controller.subscribers.filter(_.getClass == newScene.getClass).foreach { duplicateObserver =>
        println(s"❌ Removing duplicate observer: ${duplicateObserver.getClass.getSimpleName}") // Debugging
        controller.remove(duplicateObserver)
      }

      // ✅ Add the new scene as an observer only if it's not already present
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