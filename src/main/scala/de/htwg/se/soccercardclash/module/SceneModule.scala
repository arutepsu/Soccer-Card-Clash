package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Scopes}
import de.htwg.se.soccercardclash.view.gui.scenes.sceneManager.SceneManager
import scalafx.stage.Stage
import de.htwg.se.soccercardclash.controller.*

import javax.inject.Singleton


class SceneModule(stage: Stage) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Stage]).toInstance(stage)
  }

  @Provides
  def provideSceneManager(
                           controller: IController,
                           contextHolder: IGameContextHolder,
                           stage: Stage
                         ): SceneManager =
    new SceneManager(controller, contextHolder, stage)
}
