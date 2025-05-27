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
      "Taka" -> factory.createAIPlayer(
        "Taka", new TakaStrategy(randomMap("Taka")),
        Map(Boost -> 2, DoubleAttack -> 1, Swap -> 1)
      ),
      "Bitstorm" -> factory.createAIPlayer(
        "Bitstorm", new BitstormStrategy(randomMap("Bitstorm")),
        Map(Boost -> 1, DoubleAttack -> 5, Swap -> 1)
      ),
      "Defendra" -> factory.createAIPlayer(
        "Defendra", new DefendraStrategy(randomMap("Defendra")),
        Map(Boost -> 7, DoubleAttack -> 1, Swap -> 3)
      ),
      "MetaAI" -> factory.createAIPlayer(
        "MetaAI", new MetaAIStrategy(randomMap("MetaAI")),
        Map(Boost -> 3, DoubleAttack -> 2, Swap -> 3)
      )
    )
  }
}
