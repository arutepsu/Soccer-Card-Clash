package model.playerComponent.factories
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

//  override def loadPlayerFromJson(json: JsObject): IPlayer = {
//  }
}
