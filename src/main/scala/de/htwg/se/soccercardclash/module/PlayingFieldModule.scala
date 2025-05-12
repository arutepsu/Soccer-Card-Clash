package de.htwg.se.soccercardclash.module

//import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.*
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.scoringStrategy.base.*
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import com.google.inject.{AbstractModule, Provides, Singleton}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.attackStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueueFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{DataManagerFactory, HandCardsFactory, IDataManagerFactory, IHandCardsFactory, IRolesFactory, IScoresFactory, RolesFactory, ScoresFactory}

class PlayingFieldModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IDataManagerFactory]).to(classOf[DataManagerFactory])
    bind(classOf[IActionManagerFactory]).to(classOf[ActionManagerFactory])
    bind(classOf[IRolesFactory]).to(classOf[RolesFactory])
    bind(classOf[IScoresFactory]).to(classOf[ScoresFactory])

//    bind(classOf[IPlayingFieldFactory]).to(classOf[PlayingFieldFactory])

    bind(classOf[IHandCardsFactory]).toConstructor(
      classOf[HandCardsFactory]
        .getConstructor(classOf[IHandCardsQueueFactory])
    )

    bind(classOf[IAttackManager]).to(classOf[AttackManager]).in(classOf[Singleton])
    bind(classOf[IBoostManager]).to(classOf[BoostManager]).in(classOf[Singleton])
    bind(classOf[ISwapManager]).to(classOf[SwapManager]).in(classOf[Singleton])

    bind(classOf[IScoringStrategy]).to(classOf[StandardScoring])
    bind(classOf[IPlayerActionManager]).to(classOf[PlayerActionManager])


//    bind(classOf[PlayingFieldDeserializer])
//      .toConstructor(classOf[PlayingFieldDeserializer]
//        .getConstructor(classOf[IPlayingFieldFactory], classOf[PlayerDeserializer]))
  }
}
