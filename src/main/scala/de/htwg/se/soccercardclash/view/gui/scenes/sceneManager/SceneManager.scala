package de.htwg.se.soccercardclash.view.gui.scenes.sceneManager

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.dialog.PauseDialog
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStartupDataHolder
import de.htwg.se.soccercardclash.view.gui.scenes.*
import de.htwg.se.soccercardclash.view.gui.utils.CardImageRegistry
import scalafx.Includes.*
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.stage.Stage
import scalafx.util.Duration

class SceneManager(
                    controller: IController,
                    contextHolder: IGameContextHolder,
                    startupDataHolder: GameStartupDataHolder,
                    stage: scalafx.stage.Stage,
                    windowWidth: Double = 1000,
                    windowHeight: Double = 600
                  ) extends Observable with Observer {


  private var currentScene: Option[GameScene] = None
  private var lastSceneWidth: Double = windowWidth
  private var lastSceneHeight: Double = windowHeight
  stage.minWidth = windowWidth
  stage.minHeight = windowHeight

  override def update(e: ObservableEvent): Unit = {
    e match {
      case switch: SceneSwitchEvent => handleSceneSwitch(switch)
      case _ =>
    }
  }
  private def handleSceneSwitch(e: SceneSwitchEvent): Unit = {
    val oldSceneOpt = currentScene

    val newScene: GameScene = e match {
      case SceneSwitchEvent.MainMenu => new MainMenuScene(controller)
      case SceneSwitchEvent.Multiplayer => new CreateMultiplayerScene(controller, contextHolder)
      case SceneSwitchEvent.SinglePlayer => new CreateSinglePlayerScene(controller, contextHolder, startupDataHolder)
      case SceneSwitchEvent.AISelection => new AISelectionScene(controller, contextHolder, startupDataHolder)
      case SceneSwitchEvent.LoadGame => new LoadGameScene(controller, contextHolder)
      case SceneSwitchEvent.PlayingField => new PlayingFieldScene(controller, contextHolder)
      case SceneSwitchEvent.AttackerHandCards => new AttackerHandScene(controller, contextHolder)
      case SceneSwitchEvent.AttackerDefenderCards => new AttackerDefendersScene(controller, contextHolder)
      case unknown => throw new MatchError(s"Unhandled SceneSwitchEvent: $unknown")
    }

    oldSceneOpt.foreach(_.deactivate())
    currentScene = Some(newScene)
    applySceneTransition(oldSceneOpt, newScene)

    notifyObservers(SceneChangedEvent(e))
  }


  private def applySceneSize(): Unit = {
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight

  }
  private def playFadeIn(scene: Scene): Unit = {
    Option(scene.root.value).foreach { rootNode =>
      val fadeIn = new FadeTransition(Duration(500), rootNode)
      fadeIn.setFromValue(0.2)
      fadeIn.setToValue(1.0)
      fadeIn.play()
    }
  }

  private def applySceneTransition(oldSceneOpt: Option[GameScene], newScene: GameScene): Unit = {
    Platform.runLater {
      lastSceneWidth = stage.width()
      lastSceneHeight = stage.height()

      val maybeFadeOut = for {
        oldScene <- oldSceneOpt
        rootNode <- Option(oldScene.root.value)
      } yield {
        val fade = new FadeTransition(Duration(200), rootNode)
        fade.setToValue(0.2)
        fade
      }

      maybeFadeOut match {
        case Some(fadeOut) =>
          fadeOut.play()
          fadeOut.setOnFinished(_ => {
            newScene.activate()
            applySceneSize()
            stage.scene = newScene
            playFadeIn(newScene)
          })

        case None =>
          newScene.activate()
          applySceneSize()
          stage.scene = newScene
          playFadeIn(newScene)
      }
    }
  }
}
