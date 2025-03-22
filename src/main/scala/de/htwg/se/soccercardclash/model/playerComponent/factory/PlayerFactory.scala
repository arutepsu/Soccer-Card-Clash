package de.htwg.se.soccercardclash.model.playerComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import play.api.libs.json._


@Singleton
class PlayerFactory @Inject()() extends IPlayerFactory {
  override def createPlayer(name: String, cards: List[ICard]): IPlayer = {
    Player(name, cards)
  }
}
trait IPlayerFactory {
  def createPlayer(name: String, cards: List[ICard]): IPlayer

}
