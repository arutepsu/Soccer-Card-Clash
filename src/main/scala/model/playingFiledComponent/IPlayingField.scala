package model.playingFiledComponent

import model.playerComponent.IPlayer
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.manager.{ActionManager, DataManager}
import model.playingFiledComponent.strategy.scoringStrategy.PlayerScores
import util.Observable
import play.api.libs.json._
import scala.xml._
trait IPlayingField extends Observable with Serializable{
  def setPlayingField(): Unit

  def getAttacker: IPlayer

  def getDefender: IPlayer

  def getDataManager: DataManager

  def getActionManager: ActionManager

  def getRoles: RolesManager

  def getScores: PlayerScores

  override def toJson: JsObject = Json.obj(
    "attacker" -> getAttacker.toJson,
    "defender" -> getDefender.toJson,
    "scores" -> Json.obj(
      "attackerScore" -> getPlayerScore(getAttacker),
      "defenderScore" -> getPlayerScore(getDefender)
    )
  )

  override def toXml: Elem =
    <PlayingField>
      <Attacker>
        {getAttacker.toXml}
      </Attacker>
      <Defender>
        {getDefender.toXml}
      </Defender>
      <Scores>
        <AttackerScore>
          {getPlayerScore(getAttacker)}
        </AttackerScore>
        <DefenderScore>
          {getPlayerScore(getDefender)}
        </DefenderScore>
      </Scores>
    </PlayingField>
}