package model.playingFiledComponent.factories
import model.playingFiledComponent.factories.IPlayingFieldFactory
import play.api.libs.json._
import com.google.inject.Singleton
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import com.google.inject.{Inject, Singleton}

@Singleton
class PlayingFieldFactory @Inject()() extends IPlayingFieldFactory {
  override def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField = {
    new PlayingField(player1, player2)
  }

//  override def loadFromJson(json: JsObject): IPlayingField = {
//
//  }
}
