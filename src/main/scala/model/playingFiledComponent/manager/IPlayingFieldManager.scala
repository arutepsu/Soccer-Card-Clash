package model.playingFiledComponent.manager

import model.playerComponent.IPlayer
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{IActionManager, IDataManager}
import model.playingFiledComponent.manager.base.{ActionManager, DataManager}
import model.playingFiledComponent.strategy.scoringStrategy.{IPlayerScores, PlayerScores}
trait IPlayingFieldManager {
  def createDataManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IDataManager
  def createActionManager(playingField: IPlayingField): IActionManager
  def createRolesManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IRolesManager
  def createScoresManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer): IPlayerScores
}
