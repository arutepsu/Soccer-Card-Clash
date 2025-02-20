package model.playingFiledComponent
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.manager.{ActionManager, DataManager}
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import util.Observable

trait IPlayingField extends Observable {
  def setPlayingField(): Unit
  def getAttacker: IPlayer
  def getDefender: IPlayer
  def getDataManager: DataManager
  def getActionManager: ActionManager
  def getRoles: RolesManager
  def getScores: PlayerScores
}