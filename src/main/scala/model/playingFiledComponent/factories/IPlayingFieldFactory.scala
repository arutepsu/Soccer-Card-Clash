package model.playingFiledComponent.factories
import model.playingFiledComponent.factories.IPlayingFieldFactory
import play.api.libs.json._
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer

trait IPlayingFieldFactory {
  def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField
//  def loadFromJson(json: JsObject): IPlayingField
}
