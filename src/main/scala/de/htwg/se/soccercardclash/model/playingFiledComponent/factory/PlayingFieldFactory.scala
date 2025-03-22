package de.htwg.se.soccercardclash.model.playingFiledComponent.factory

import com.google.inject.{Inject, Provider, Singleton}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.base.PlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.factory.{IPlayingFieldFactory, IPlayingFieldManagerFactory}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import play.api.libs.json.*


class PlayingFieldFactory @Inject() (manager: IPlayingFieldManagerFactory) extends IPlayingFieldFactory {
  override def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField =
    new PlayingField(player1, player2)(using manager)
}
trait IPlayingFieldFactory {
  def createPlayingField(player1: IPlayer, player2: IPlayer): IPlayingField
}

