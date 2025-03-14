package model.playingFiledComponent.manager.base

import com.google.inject.Inject
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.base.{ActionManager, DataManager}
import model.playingFiledComponent.manager.{IActionManager, IDataManager, IPlayingFieldManager}
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import com.google.inject.{Inject, Singleton}
@Singleton
class PlayingFieldManager @Inject() (
                                      handManager: IPlayerHandManager,
                                      fieldManager: IPlayerFieldManager
                                    ) extends IPlayingFieldManager {

  override def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager =
    new DataManager(playingField, handManager, fieldManager)

  override def createActionManager(playingField: IPlayingField): IActionManager =
    new ActionManager(playingField)

  override def createRolesManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IRolesManager =
    new RolesManager(playingField, player1, player2)

  override def createScoresManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IPlayerScores =
    new PlayerScores(playingField, player1, player2)
}