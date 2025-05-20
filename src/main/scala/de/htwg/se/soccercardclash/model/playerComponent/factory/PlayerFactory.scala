package de.htwg.se.soccercardclash.model.playerComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human}
import play.api.libs.json.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
@Singleton
class PlayerFactory @Inject()() extends IPlayerFactory {
  override def createPlayer(name: String): IPlayer =
    Player.withDefaultActions(name, Human)

  override def createAIPlayer(name: String, strategy: IAIStrategy): IPlayer =
    Player.withDefaultActions(name, AI(strategy))

  override def createAIPlayer(name: String, strategy: IAIStrategy, customLimits: Map[PlayerActionPolicies, Int]): IPlayer =
    Player.withCustomActions(name, AI(strategy), customLimits)

}
trait IPlayerFactory {
  def createPlayer(name: String): IPlayer

  def createAIPlayer(name: String, strategy: IAIStrategy): IPlayer

  def createAIPlayer(name: String, strategy: IAIStrategy, customLimits: Map[PlayerActionPolicies, Int]): IPlayer
}
