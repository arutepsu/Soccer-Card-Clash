package de.htwg.se.soccercardclash.model.playerComponent.ai
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.SmartBoostWeakestDefenderAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.{BitstormStrategy, DefendraStrategy, MetaAIStrategy, TakaStrategy}

import scala.util.Random
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
object AIPresetRegistry {
  def registerCoreAIs(
                       factory: IPlayerFactory,
                       randomMap: Map[String, IRandomProvider]
                     ): Map[String, IPlayer] = {
    Map(
      "Taka" -> PlayerBuilder()
        .withName("Taka")
        .asAI(new TakaStrategy(randomMap("Taka")))
        .withPolicy(Boost, 2)
        .withPolicy(DoubleAttack, 1)
        .withPolicy(Swap, 1)
        .build(),

      "Bitstorm" -> PlayerBuilder()
        .withName("Bitstorm")
        .asAI(new BitstormStrategy(randomMap("Bitstorm")))
        .withPolicy(Boost, 1)
        .withPolicy(DoubleAttack, 5)
        .withPolicy(Swap, 1)
        .build(),

      "Defendra" -> PlayerBuilder()
        .withName("Defendra")
        .asAI(new DefendraStrategy(randomMap("Defendra")))
        .withPolicy(Boost, 7)
        .withPolicy(DoubleAttack, 1)
        .withPolicy(Swap, 3)
        .build(),

      "MetaAI" -> PlayerBuilder()
        .withName("MetaAI")
        .asAI(new MetaAIStrategy(randomMap("MetaAI")))
        .withPolicy(Boost, 3)
        .withPolicy(DoubleAttack, 2)
        .withPolicy(Swap, 3)
        .build()
    )
  }
}
