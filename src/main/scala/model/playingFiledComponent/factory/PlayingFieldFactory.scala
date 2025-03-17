package model.playingFiledComponent.factory
import com.google.inject.{Inject, Provider, Singleton}
import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.base.PlayingField
import model.playingFiledComponent.factory.{IPlayingFieldFactory, IPlayingFieldManagerFactory}
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import play.api.libs.json.*


class PlayingFieldFactory @Inject() (manager: IPlayingFieldManagerFactory) extends IPlayingFieldFactory {
  override def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField =
    new PlayingField(player1, player2)(using manager)
}
trait IPlayingFieldFactory {
  def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField
}

