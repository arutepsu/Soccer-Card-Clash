package de.htwg.se.soccercardclash.view.gui

import com.google.inject.Injector
import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.module.SceneModule
import de.htwg.se.soccercardclash.util.*
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import de.htwg.se.soccercardclash.view.gui.utils.{Assets, CardImageRegistry}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.image.Image
import scalafx.stage.Stage

class Gui(
           controller: IController,
           contextHolder: IGameContextHolder,
           parentInjector: Injector
         ) extends JFXApp3 {

  override def start(): Unit = {
    val primary = new PrimaryStage {
      title = "Soccer Card Clash"
      icons.add(Assets.appLogo)
      width = 1000
      height = 600
    }

    val sceneInjector = parentInjector.createChildInjector(new SceneModule(primary))

    CardImageRegistry.preloadAll()
    PlayerAvatarRegistry.preloadAvatars()
    
    val sceneManager = sceneInjector.getInstance(classOf[SceneManager])
    GlobalObservable.add(sceneManager)
    Platform.runLater {
      GlobalObservable.notifyObservers(SceneSwitchEvent.MainMenu)
    }

  }
}
