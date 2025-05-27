package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Singleton}
import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.controller.base.Controller
import de.htwg.se.soccercardclash.controller.command.*
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.service.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.util.IGameContextHolder
import de.htwg.se.soccercardclash.util.Observable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

class TestControllerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
    bind(classOf[Observable]).to(classOf[IController])
    bind(classOf[ICommandFactory]).to(classOf[CommandFactory])
    bind(classOf[IActionManager]).to(classOf[ActionManager])
    bind(classOf[IGameService]).toInstance(mock(classOf[IGameService]))
    bind(classOf[IGameInitializer]).toInstance(mock(classOf[IGameInitializer]))
    bind(classOf[IGamePersistence]).toInstance(mock(classOf[IGamePersistence]))
    bind(classOf[Map[String, IPlayer]]).toInstance(Map.empty)
    bind(classOf[IGameContextHolder]).toInstance(mock(classOf[IGameContextHolder]))
    bind(classOf[IPlayerActionManager]).toInstance(mock(classOf[IPlayerActionManager]))
    bind(classOf[IAttackManager]).toInstance(mock(classOf[IAttackManager]))
    bind(classOf[IBoostManager]).toInstance(mock(classOf[IBoostManager]))
    bind(classOf[ISwapManager]).toInstance(mock(classOf[ISwapManager]))
  }
}

