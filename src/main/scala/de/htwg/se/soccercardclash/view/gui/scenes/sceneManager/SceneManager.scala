package de.htwg.se.soccercardclash.view.gui.scenes.sceneManager

import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.Events.CardBoosted
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.util.Duration
import de.htwg.se.soccercardclash.util.{Events, Observable, ObservableEvent, Observer}
import de.htwg.se.soccercardclash.view.gui.components.dialog.PauseDialog
import de.htwg.se.soccercardclash.view.gui.scenes.*
import de.htwg.se.soccercardclash.view.gui.utils.CardImageLoader


object SceneManager extends Observable with Observer {
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800
  private var lastSceneHeight: Double = 600
  var currentScene: Option[Scene] = None
  private var controller: IController = _
  private var contextHolder: IGameContextHolder = _
  var sceneRegistry: SceneRegistry = _

  def init(primaryStage: Stage, ctrl: IController, holder: IGameContextHolder): Unit = {
    stage = primaryStage
    controller = ctrl
    contextHolder = holder
    controller.add(this)

    sceneRegistry = new SceneRegistry(controller, this, contextHolder)
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
        case _: LoadGameScene if e == Events.LoadGame => true
        case _ => false
      }

      if (!sceneMatchesEvent) {
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
//      notifyObservers()
    })
  }

  def switchScene(newScene: Scene): Unit = {
    if (currentScene.contains(newScene)) return

      Platform.runLater(() => {
        val oldSceneOpt = currentScene

        newScene match {
          case _: AttackerHandScene | _: AttackerDefendersScene =>
            sceneRegistry.clearHandAndDefenderScenes()
          case _ =>
        }

        if (newScene.isInstanceOf[PlayingFieldScene]) {
          sceneRegistry.clearHandAndDefenderScenes()
        }

        oldSceneOpt.foreach {
          case oldObserver: Observer if controller.subscribers.contains(oldObserver) =>
            controller.remove(oldObserver)
          case _ =>
        }

        lastSceneWidth = stage.width()
        lastSceneHeight = stage.height()

        val maybeFadeOut = for {
          oldScene <- oldSceneOpt
          rootNode <- Option(oldScene.root.value)
        } yield {
          val fade = new FadeTransition(Duration(200), rootNode)
          fade.toValue = 0.2
          fade
        }

        maybeFadeOut match {
          case Some(fadeOut) =>
            fadeOut.play()
            fadeOut.setOnFinished(_ => {
              CardImageLoader.clearCache()
              applySceneTransition(newScene)
            })
          case None =>
            CardImageLoader.clearCache()
            applySceneTransition(newScene)
        }
      })
  }


  private def applySceneTransition(newScene: Scene): Unit = {
    Platform.runLater(() => {
      stage.scene = newScene
      currentScene = Some(newScene)
      applySceneSize()

      controller.subscribers.filter(_.getClass == newScene.getClass).foreach { duplicateObserver =>
        controller.remove(duplicateObserver)
      }

      newScene match {
        case newObserverScene: Observer if !controller.subscribers.contains(newObserverScene) =>
          controller.add(newObserverScene)
        case _ =>
      }

      if (newScene.root.value != null) {
        val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
        fadeIn.fromValue = 0.2
        fadeIn.toValue = 1.0
        fadeIn.play()
      }

//      notifyObservers()
      controller.printAllObservers()

    })
  }


}