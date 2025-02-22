package model.playingFiledComponent
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.IActionManager
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playerComponent.playerRole.IRolesManager
import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.manager.base.{ActionManager, DataManager}
import model.playingFiledComponent.strategy.scoringStrategy.base.PlayerScores
import util.Observable
import play.api.libs.json._
import scala.xml._
trait IPlayingField extends Observable with Serializable{
  def setPlayingField(): Unit

  def getAttacker: IPlayer

  def getDefender: IPlayer

  def getDataManager: IDataManager

  def getActionManager: IActionManager

  def getRoles: IRolesManager

  def getScores: IPlayerScores

}