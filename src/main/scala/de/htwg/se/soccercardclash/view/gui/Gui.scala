package de.htwg.se.soccercardclash.view.gui

import com.google.inject.Injector
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.module.SceneModule
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.image.Image
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager

class Gui(
           controller: IController,
           contextHolder: IGameContextHolder,
           parentInjector: Injector
         ) extends JFXApp3 {

  override def start(): Unit = {
    val primary = new PrimaryStage {
      title = "Soccer Card Clash"
      icons.add(new Image(getClass.getResource("/images/data/logo.png").toExternalForm))
      width = 1000
      height = 600
    }

    val sceneInjector = parentInjector.createChildInjector(new SceneModule(primary))

    val sceneManager = sceneInjector.getInstance(classOf[SceneManager])
    GlobalObservable.add(sceneManager)
    Platform.runLater {
      GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
    }

  }
}
