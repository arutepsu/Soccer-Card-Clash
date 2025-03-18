package model.playingFiledComponent.factory

import com.google.inject.{Inject, Singleton}
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.*
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores

@Singleton
class PlayingFieldManagerFactory @Inject()(
                                            handManager: IPlayerHandManager,
                                            fieldManager: IPlayerFieldManager
                                          ) extends IPlayingFieldManagerFactory {

  override def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager =
    new DataManager(playingField, handManager, fieldManager)

//  override def createActionManager(playingField: IPlayingField): IActionManager =
//    new ActionManager(playingField)
  override def createActionManager(playingField: IPlayingField): IActionManager = {
    val newManager = new ActionManager(playingField)
    println(s"🚀 DEBUG: New ActionManager created: $newManager for playingField: $playingField")
    newManager
}


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