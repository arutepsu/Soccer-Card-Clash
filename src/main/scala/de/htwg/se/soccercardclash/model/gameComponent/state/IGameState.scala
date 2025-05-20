package de.htwg.se.soccercardclash.model.gameComponent.state

import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.xml.*

trait IGameState extends Observable with Serializable {
  def getDataManager: IDataManager

  def getRoles: IRoles

  def getScores: IScores

  def updateDataManager(newDataManager: IDataManager): IGameState

  def updateRoles(newRoles: IRoles): IGameState

  def updateScores(newScores: IScores): IGameState

  def toXml: Elem = {
    <gameState>
      <attacker>
        {getRoles.attacker.toXml}
      </attacker>
      <defender>
        {getRoles.defender.toXml}
      </defender>
      <attackerHand>
        {getDataManager.getPlayerHand(getRoles.attacker).toList.map(_.toXml)}
      </attackerHand>
      <defenderHand>
        {getDataManager.getPlayerHand(getRoles.defender).toList.map(_.toXml)}
      </defenderHand>
      <attackerField>
        {
        getDataManager.getPlayerDefenders(getRoles.attacker).map {
          case Some(card) => card.toXml
          case None => <Card xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
        }
        }
      </attackerField>

      <defenderField>
        {
        getDataManager.getPlayerDefenders(getRoles.defender).map {
          case Some(card) => card.toXml
          case None => <Card xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
        }
        }
      </defenderField>

      <attackerGoalkeeper>
        {getDataManager.getPlayerGoalkeeper(getRoles.attacker).map(_.toXml).getOrElse(<empty/>)}
      </attackerGoalkeeper>
      <defenderGoalkeeper>
        {getDataManager.getPlayerGoalkeeper(getRoles.defender).map(_.toXml).getOrElse(<empty/>)}
      </defenderGoalkeeper>
      <attackerScore>
        {getScores.getScore(getRoles.attacker)}
      </attackerScore>
      <defenderScore>
        {getScores.getScore(getRoles.defender)}
      </defenderScore>
    </gameState>
  }


  def toJson: JsObject = Json.obj(
    "attacker" -> getRoles.attacker.toJson,
    "defender" -> getRoles.defender.toJson,
    "attackerHand" -> getDataManager.getPlayerHand(getRoles.attacker).toList.map(_.toJson),
    "defenderHand" -> getDataManager.getPlayerHand(getRoles.defender).toList.map(_.toJson),
    "attackerField" -> getDataManager.getPlayerDefenders(getRoles.attacker).map {
      case Some(card) => card.toJson
      case None => JsNull
    },
    "defenderField" -> getDataManager.getPlayerDefenders(getRoles.defender).map {
      case Some(card) => card.toJson
      case None => JsNull
    },
    "attackerGoalkeeper" -> getDataManager.getPlayerGoalkeeper(getRoles.attacker).map(_.toJson).getOrElse(Json.obj()),
    "defenderGoalkeeper" -> getDataManager.getPlayerGoalkeeper(getRoles.defender).map(_.toJson).getOrElse(Json.obj()),
    "attackerScore" -> getScores.getScore(getRoles.attacker),
    "defenderScore" -> getScores.getScore(getRoles.defender)
  )
}
