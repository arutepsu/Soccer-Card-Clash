package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.{IPlayerFactory, PlayerDeserializer}

class PlayingFieldModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IGameCardsFactory]).to(classOf[GameCardsFactory])
    bind(classOf[IActionManagerFactory]).to(classOf[ActionManagerFactory])
    bind(classOf[IRolesFactory]).to(classOf[RolesFactory])
    bind(classOf[IScoresFactory]).to(classOf[ScoresFactory])
    bind(classOf[IHandCardsFactory]).toConstructor(
      classOf[HandCardsFactory]
        .getConstructor(classOf[IHandCardsQueueFactory])
    )

    bind(classOf[IAttackManager]).to(classOf[AttackManager]).in(classOf[Singleton])
    bind(classOf[IBoostManager]).to(classOf[BoostManager]).in(classOf[Singleton])
    bind(classOf[ISwapManager]).to(classOf[SwapManager]).in(classOf[Singleton])

    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])
    bind(classOf[IPlayerActionManager]).to(classOf[PlayerActionManager])
  }
}
