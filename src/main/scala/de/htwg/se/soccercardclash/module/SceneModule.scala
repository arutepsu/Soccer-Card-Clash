package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Scopes, Singleton}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.view.gui.components.sceneComponents.GameStartupDataHolder



class SceneModule(stage: Stage) extends AbstractModule {
  override def configure(): Unit = {
    
    bind(classOf[Stage]).toInstance(stage)
    
    bind(classOf[GameStartupDataHolder]).in(classOf[Singleton])
    
  }

  @Provides
  def provideSceneManager(
                           controller: IController,
                           contextHolder: IGameContextHolder,
                           startupDataHolder: GameStartupDataHolder,
                           stage: Stage
                         ): SceneManager =
    new SceneManager(controller, contextHolder, startupDataHolder, stage)
}
