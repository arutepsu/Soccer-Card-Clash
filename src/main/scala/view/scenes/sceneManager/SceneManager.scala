package view.scenes.sceneManager

import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.util.Duration
import scalafx.application.Platform
import scalafx.Includes._
import util.Observable
import util.Observer
//object SceneManager {
//  private var stage: Stage = _
//  private var lastSceneWidth: Double = 800  // Default width
//  private var lastSceneHeight: Double = 600 // Default height
//
//  def init(primaryStage: Stage): Unit = {
//    stage = primaryStage
//    stage.width = lastSceneWidth
//    stage.height = lastSceneHeight
//    stage.show()
//  }
//
//  def switchScene(newScene: Scene): Unit = {
//    val oldSceneOpt = Option(stage.scene) // ✅ Get current scene safely
//    lastSceneWidth = stage.width()
//    lastSceneHeight = stage.height()
//
//    oldSceneOpt match {
//      case Some(oldScene) if oldScene.root.value != null => // ✅ Check if old scene exists and has a root
//        val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
//        fadeOut.toValue = 0.2
//        fadeOut.interpolator = Interpolator.EaseOut
//
//        fadeOut.setOnFinished(_ => Platform.runLater(() => {
//          stage.scene = newScene
//          applySceneSize()
//
//          val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
//          fadeIn.fromValue = 0.2
//          fadeIn.toValue = 1.0
//          fadeIn.interpolator = Interpolator.EaseIn
//          fadeIn.play()
//        }))
//
//        fadeOut.play()
//
//      case _ => // ✅ First scene (or no previous scene)
//        stage.scene = newScene
//        applySceneSize()
//    }
//  }
//
//  private def applySceneSize(): Unit = {
//    stage.width = lastSceneWidth
//    stage.height = lastSceneHeight
//  }
//
//
//  def refreshCurrentScene(): Unit = {
//    currentScene.foreach(_.root.requestLayout()) // ✅ Force scene refresh
//  }
//}

//package view.scenes // ✅ Ensure this matches the correct package path

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import util.Observable

//object SceneManager extends Observable { // ✅ SceneManager is now Observable
//  private var stage: Stage = _
//  private var lastSceneWidth: Double = 800  // Default width
//  private var lastSceneHeight: Double = 600 // Default height
//  private var currentScene: Option[Scene] = None
//
//  def init(primaryStage: Stage): Unit = {
//    stage = primaryStage
//  }
//
//  def switchScene(newScene: Scene): Unit = {
//    Platform.runLater(() => {
//      val oldSceneOpt = Option(stage.scene)
//      lastSceneWidth = stage.width()
//      lastSceneHeight = stage.height()
//
//      oldSceneOpt match {
//        case Some(oldScene) if oldScene.root.value != null =>
//          val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
//          fadeOut.toValue = 0.2
//          fadeOut.interpolator = Interpolator.EaseOut
//
//          fadeOut.setOnFinished(_ => Platform.runLater(() => {
//            stage.scene = newScene
//            currentScene = Some(newScene)
//            applySceneSize()
//
//            val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
//            fadeIn.fromValue = 0.2
//            fadeIn.toValue = 1.0
//            fadeIn.interpolator = Interpolator.EaseIn
//            fadeIn.play()
//
//            notifyObservers() // ✅ Notify GUI and TUI when scene changes
//          }))
//
//          fadeOut.play()
//
//        case _ =>
//          stage.scene = newScene
//          currentScene = Some(newScene)
//          applySceneSize()
//          notifyObservers() // ✅ Ensure observers (TUI/GUI) are notified
//      }
//    })
//  }
//
//  private def applySceneSize(): Unit = {
//    stage.width = lastSceneWidth
//    stage.height = lastSceneHeight
//  }
//
//  def refreshCurrentScene(): Unit = {
//    Platform.runLater(() => {
//      currentScene.foreach(_.root.value.requestLayout())
//      notifyObservers() // ✅ Ensure TUI updates when GUI changes scenes
//    })
//  }
//}
//package sceneManager
import controller.IController
import controller.ControllerEvents
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.animation.{FadeTransition, Interpolator}
import scalafx.util.Duration
import util.{Observable, ObservableEvent, Observer}
import view.scenes._
import view.scenes.CreatePlayerCard
object SceneManager extends Observable with Observer { // ✅ SceneManager is now also an Observer
  private var stage: Stage = _
  private var lastSceneWidth: Double = 800
  private var lastSceneHeight: Double = 600
  private var currentScene: Option[Scene] = None
  private var controller: IController = _ // Store reference to controller

  /** ✅ Initialize SceneManager */
  def init(primaryStage: Stage, ctrl: IController): Unit = {
    stage = primaryStage
    controller = ctrl // Store controller reference
    controller.add(this) // ✅ Register SceneManager as an Observer
  }

  /** ✅ Switch Scene with Fade Transition */
  def switchScene(newScene: Scene): Unit = {
    Platform.runLater(() => {
      val oldSceneOpt = Option(stage.scene)
      lastSceneWidth = stage.width()
      lastSceneHeight = stage.height()

      oldSceneOpt match {
        case Some(oldScene) if oldScene.root.value != null =>
          val fadeOut = new FadeTransition(Duration(200), oldScene.root.value)
          fadeOut.toValue = 0.2
          fadeOut.interpolator = Interpolator.EaseOut

          fadeOut.setOnFinished(_ => Platform.runLater(() => {
            stage.scene = newScene
            currentScene = Some(newScene)
            applySceneSize()

            val fadeIn = new FadeTransition(Duration(500), newScene.root.value)
            fadeIn.fromValue = 0.2
            fadeIn.toValue = 1.0
            fadeIn.interpolator = Interpolator.EaseIn
            fadeIn.play()

            notifyObservers() // ✅ Notify other observers when scene changes
          }))

          fadeOut.play()

        case _ =>
          stage.scene = newScene
          currentScene = Some(newScene)
          applySceneSize()
          notifyObservers() // ✅ Ensure observers (TUI/GUI) are notified
      }
    })
  }

  /** ✅ Adjust Scene Size */
  private def applySceneSize(): Unit = {
    stage.width = lastSceneWidth
    stage.height = lastSceneHeight
  }

  /** ✅ Refresh Current Scene */
  def refreshCurrentScene(): Unit = {
    Platform.runLater(() => {
      currentScene.foreach(_.root.value.requestLayout())
      notifyObservers() // ✅ Ensure observers (TUI/GUI) are notified
    })
  }

  private def mainMenuScene: Scene = new MainMenuScene(controller).mainMenuScene()

  // ✅ Create Player Scene (Ensures consistency)
  private def createPlayerScene: Scene = new Scene {
    root = new CreatePlayerCard(controller) // ✅ Set as root of Scene
  }


  // ✅ Load Game Scene (Restored safely)
//  private def loadGameScene: Scene = new LoadGameScene(controller)

  // ✅ Playing Field Scene
  private def playingFieldScene: Scene = new PlayingFieldScene(controller, 800, 600)

  // ✅ Use Factory Pattern for Attacker Scenes
  private def attackerHandScene: Scene = AttackerSceneFactory.createAttackerHandScene(controller, Option(controller.getPlayingField), 800, 600)

  private def attackerDefendersScene: Scene = AttackerSceneFactory.createAttackerDefendersScene(controller, Option(controller.getPlayingField), 800, 600)

  /** ✅ Observer Pattern: Handle Scene Changes */
  override def update(e: ObservableEvent): Unit = {
    Platform.runLater(() => {
      println(s"🔄 SceneManager Handling Event: $e")

      e match {
        case ControllerEvents.MainMenu =>
          println("📌 Switching to Main Menu!")
          switchScene(mainMenuScene)

        case ControllerEvents.CreatePlayer =>
          println("🎮 Switching to Create Player Scene!")
          switchScene(createPlayerScene)
//
//        case ControllerEvents.LoadGame =>
//          println("📂 Loading a Saved Game!")
//          switchScene(loadGameScene) // ✅ Now correctly reinstated

        case ControllerEvents.StartGame =>
          println("⚽ Starting Game!")
          switchScene(createPlayerScene)

        case ControllerEvents.PlayingField =>
          println("⚽ Starting Game!")
          switchScene(playingFieldScene)

        case ControllerEvents.AttckerHand =>
          println("⚽ Starting Game!")
          switchScene(attackerHandScene)

        case ControllerEvents.AttackerDefenderField =>
          println("⚽ Starting Game!")
          switchScene(attackerDefendersScene)

        case ControllerEvents.Quit =>
          println("❌ Exiting Game!")
          sys.exit(0)

        case _ =>
          println(s"🔕 Ignoring unhandled event: $e")
      }
    })
  }
}

