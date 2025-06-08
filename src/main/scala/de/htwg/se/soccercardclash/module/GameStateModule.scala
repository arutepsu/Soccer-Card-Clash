package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.swap.*
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
class GameStateModule extends AbstractModule {

  override def configure(): Unit = {
    
    bind(classOf[IGameCardsFactory]).to(classOf[GameCardsFactory])
    
    bind(classOf[IRolesFactory]).to(classOf[RolesFactory])
    
    bind(classOf[IScoresFactory]).to(classOf[ScoresFactory])
    
    bind(classOf[IHandCardsFactory]).toConstructor(
      classOf[HandCardsFactory]
        .getConstructor(classOf[IHandCardsQueueFactory])
    )
    bind(classOf[IDefenderFieldRefillStrategy]).to(classOf[DefenderFieldRefillStrategy])
    bind(classOf[IFieldRefillStrategy]).to(classOf[FieldRefillStrategy])
    bind(classOf[IRefillStrategy]).to(classOf[StandardRefillStrategy])
    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])
    bind(classOf[IRevertBoostStrategyFactory]).to(classOf[RevertBoostStrategyFactory])
    bind(classOf[IPlayerActionManager]).to(classOf[PlayerActionManager])
  }
}
