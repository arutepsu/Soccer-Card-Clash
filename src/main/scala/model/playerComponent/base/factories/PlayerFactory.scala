package model.playerComponent.base.factories

import model.cardComponent.ICard
import play.api.libs.json._
import com.google.inject.Singleton
import model.playerComponent.IPlayer
import com.google.inject.{Inject, Singleton}
import model.playerComponent.base.Player

@Singleton
class PlayerFactory @Inject()() extends IPlayerFactory {
  override def createPlayer(name: String, cards: List[ICard]): IPlayer = {
    Player(name, cards)
  }
}
trait IPlayerFactory {
  def createPlayer(name: String, cards: List[ICard]): IPlayer

}
