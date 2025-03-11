package view.gui.scenes.sceneManager

import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import view.gui.scenes.MenuScene
import controller.{Events, IController}
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.util.Duration
import util.{Observable, ObservableEvent, Observer}
import view.gui.scenes.*

object SceneManager extends Observable with Observer {
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800
  private var lastSceneHeight: Double = 600
  private var currentScene: Option[Scene] = None
  private var controller: IController = _
  private var sceneRegistry: SceneRegistry = _

  def init(primaryStage: Stage, ctrl: IController): Unit = {
    stage = primaryStage
    controller = ctrl
    controller.add(this)

    // Initialize scene registry
    sceneRegistry = new SceneRegistry(controller, this)
  }

  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      e match {
        case Events.MainMenu =>
          switchScene(sceneRegistry.getMainMenuScene)

        case Events.CreatePlayers =>
          switchScene(sceneRegistry.getCreatePlayerScene)

        case Events.PlayingField =>
          println("!!!!!!!!!!!!!Pf")
          switchScene(sceneRegistry.getPlayingFieldScene)

        case Events.AttackerHandCards =>
          switchScene(sceneRegistry.getAttackerHandScene)

        case Events.AttackerDefenderCards =>
          switchScene(sceneRegistry.getAttackerDefendersScene)

        case Events.PauseGame =>
          switchScene(sceneRegistry.getMenuScene)

        case Events.LoadGame =>
          println("!!!!!!!!!!!!!Load")
          switchScene(sceneRegistry.getLoadGameScene)

        case Events.Quit =>
          println("❌ Exiting Game!")
          controller.quit()

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
    Platform.runLater(() => {
      val oldSceneOpt = Option(stage.scene)
      lastSceneWidth = stage.width()
      lastSceneHeight = stage.height()

      oldSceneOpt match {
        case Some(oldScene) if oldScene.root.value != null =>
          val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
          fadeOut.toValue = 0.2
          fadeOut.play()

          fadeOut.setOnFinished(_ => Platform.runLater(() => {
            stage.scene = newScene
            currentScene = Some(newScene)
            applySceneSize()

            val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
            fadeIn.fromValue = 0.2
            fadeIn.toValue = 1.0
            fadeIn.play()

            notifyObservers()
          }))

        case _ =>
          stage.scene = newScene
          currentScene = Some(newScene)
          applySceneSize()
          notifyObservers()
      }
    })
  }
}