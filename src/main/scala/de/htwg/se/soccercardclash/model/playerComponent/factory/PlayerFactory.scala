package de.htwg.se.soccercardclash.model.playerComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human}
import de.htwg.se.soccercardclash.model.playerComponent.strategyAI.IAIStrategy
import play.api.libs.json.*

@Singleton
class PlayerFactory @Inject()() extends IPlayerFactory {
  override def createPlayer(name: String): IPlayer = {
    Player(name, playerType = Human)
  }
  override def createAIPlayer(name: String, strategy: IAIStrategy): IPlayer =
    Player(name, playerType = AI(strategy))

}
trait IPlayerFactory {
  def createPlayer(name: String): IPlayer
  def createAIPlayer(name: String, strategy: IAIStrategy): IPlayer
}
