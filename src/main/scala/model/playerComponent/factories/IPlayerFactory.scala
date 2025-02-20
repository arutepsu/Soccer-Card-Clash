package model.playerComponent.factories
import model.cardComponent.ICard
import play.api.libs.json._
import model.playerComponent.IPlayer

trait IPlayerFactory {
  def createPlayer(name: String, cards: List[ICard]): IPlayer
//  def loadPlayerFromJson(json: JsObject): IPlayer
}
