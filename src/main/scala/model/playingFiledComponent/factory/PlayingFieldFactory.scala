package model.playingFiledComponent.factory
import model.playingFiledComponent.factory.IPlayingFieldFactory
import play.api.libs.json.*
import com.google.inject.Singleton
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import com.google.inject.{Inject, Singleton}
import com.google.inject.{Inject, Singleton}
import com.google.inject.{Inject, Provider, Singleton}
import model.playingFiledComponent.manager.IPlayingFieldManager
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import play.api.libs.json.JsObject


class PlayingFieldFactory @Inject() (manager: IPlayingFieldManager) extends IPlayingFieldFactory {
  override def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField =
    new PlayingField(player1, player2)(using manager) // Pass manager explicitly
}
trait IPlayerScoresFactory {
  def createPlayerScores(playingField: IPlayingField, player1:IPlayer, player2: IPlayer): IPlayerScores
}