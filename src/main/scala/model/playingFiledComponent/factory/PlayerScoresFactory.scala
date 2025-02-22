package model.playingFiledComponent.factory

import com.google.inject.Singleton
import model.playingFiledComponent.IPlayingField
import model.playerComponent.IPlayer
import model.playerComponent.base.factories.IPlayerFactory
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores

@Singleton
class PlayerScoresFactory extends IPlayerScoresFactory {
  override def createPlayerScores(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IPlayerScores = {
    new PlayerScores(playingField, player1, player2)
  }
}
trait IPlayingFieldFactory {
  def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField
}
