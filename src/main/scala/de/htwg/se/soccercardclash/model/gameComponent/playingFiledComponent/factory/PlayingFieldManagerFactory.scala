package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory

import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores


@Singleton
class PlayingFieldManagerFactory @Inject()(
                                            handManager: IPlayerHandManager,
                                            fieldManager: IPlayerFieldManager,
                                            playerActionService: IPlayerActionManager
                                          ) extends IPlayingFieldManagerFactory {

  override def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager =
    new DataManager(playingField, handManager, fieldManager)

  override def createActionManager(playingField: IPlayingField): IActionManager =
    new ActionManager(playingField, playerActionService)

  override def createRolesManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IRolesManager =
    new RolesManager(playingField, player1, player2)

  override def createScoresManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IPlayerScores =
    new PlayerScores(playingField, player1, player2)
}

trait IPlayingFieldManagerFactory {
  def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager

  def createActionManager(playingField: IPlayingField): IActionManager

  def createRolesManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IRolesManager

  def createScoresManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IPlayerScores
}