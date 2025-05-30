package de.htwg.se.soccercardclash.model.gameComponent

import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.xml.*

trait IGameState extends Serializable {
  def getGameCards: IGameCards

  def getRoles: IRoles

  def getScores: IScores

  def newGameCards(newGameCards: IGameCards): IGameState

  def newRoles(newRoles: IRoles): IGameState

  def newScores(newScores: IScores): IGameState

  def toXml: Elem = {
    <gameState>
      <attacker>
        {getRoles.attacker.toXml}
      </attacker>
      <defender>
        {getRoles.defender.toXml}
      </defender>
      <attackerHand>
        {getGameCards.getPlayerHand(getRoles.attacker).toList.map(_.toXml)}
      </attackerHand>
      <defenderHand>
        {getGameCards.getPlayerHand(getRoles.defender).toList.map(_.toXml)}
      </defenderHand>
      <attackerField>
        {
        getGameCards.getPlayerDefenders(getRoles.attacker).map {
          case Some(card) => card.toXml
          case None => <Card xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
        }
        }
      </attackerField>

      <defenderField>
        {
        getGameCards.getPlayerDefenders(getRoles.defender).map {
          case Some(card) => card.toXml
          case None => <Card xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
        }
        }
      </defenderField>

      <attackerGoalkeeper>
        {getGameCards.getPlayerGoalkeeper(getRoles.attacker).map(_.toXml).getOrElse(<empty/>)}
      </attackerGoalkeeper>
      <defenderGoalkeeper>
        {getGameCards.getPlayerGoalkeeper(getRoles.defender).map(_.toXml).getOrElse(<empty/>)}
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
    "attackerHand" -> getGameCards.getPlayerHand(getRoles.attacker).toList.map(_.toJson),
    "defenderHand" -> getGameCards.getPlayerHand(getRoles.defender).toList.map(_.toJson),
    "attackerField" -> getGameCards.getPlayerDefenders(getRoles.attacker).map {
      case Some(card) => card.toJson
      case None => JsNull
    },
    "defenderField" -> getGameCards.getPlayerDefenders(getRoles.defender).map {
      case Some(card) => card.toJson
      case None => JsNull
    },
    "attackerGoalkeeper" -> getGameCards.getPlayerGoalkeeper(getRoles.attacker).map(_.toJson).getOrElse(Json.obj()),
    "defenderGoalkeeper" -> getGameCards.getPlayerGoalkeeper(getRoles.defender).map(_.toJson).getOrElse(Json.obj()),
    "attackerScore" -> getScores.getScore(getRoles.attacker),
    "defenderScore" -> getScores.getScore(getRoles.defender)
  )
}
