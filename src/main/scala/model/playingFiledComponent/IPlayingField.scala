package model.playingFiledComponent
import model.playingFiledComponent.manager.{ActionManager, DataManager, IDataManager, IRolesManager, RolesManager}
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.scoringStrategy.IPlayerScores
import model.playerComponent.IPlayer
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

  def toXml: Elem = {
    <playingField>
      <Attacker>{getAttacker.toXml}</Attacker>
      <Defender>{getDefender.toXml}</Defender>
      <Scores>{getScores.toXml}</Scores>
    </playingField>
  }

  def toJson: JsObject = Json.obj(
    "attacker" -> getAttacker.toJson,
    "defender" -> getDefender.toJson,
    "scores" -> getScores.toJson
  )
  def reset(): Unit
}