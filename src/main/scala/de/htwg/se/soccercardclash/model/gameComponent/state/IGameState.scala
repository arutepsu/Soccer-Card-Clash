package de.htwg.se.soccercardclash.model.gameComponent.state

import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, IScores}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.Observable
import play.api.libs.json.*

import scala.xml.*

trait IGameState extends Observable with Serializable {
  def getPlayer1: IPlayer

  def getPlayer2: IPlayer

  def getDataManager: IDataManager

  def getRoles: IRoles

  def getScores: IScores

  def withDataManager(newDataManager: IDataManager): IGameState

  def withRolesManager(newRoles: IRoles): IGameState

  def withScores(newScores: IScores): IGameState

  def toXml: Elem = {
    <gameState>
      <attacker>
        {getRoles.attacker.toXml}
      </attacker>
      <defender>
        {getRoles.defender.toXml}
      </defender>
      <player1Hand>
        {getDataManager.getPlayerHand(getPlayer1).toList.map(_.toXml)}
      </player1Hand>
      <player2Hand>
        {getDataManager.getPlayerHand(getPlayer2).toList.map(_.toXml)}
      </player2Hand>
      <player1Field>
        {getDataManager.getPlayerDefenders(getPlayer1).map(_.toXml)}
      </player1Field>
      <player2Field>
        {getDataManager.getPlayerDefenders(getPlayer2).map(_.toXml)}
      </player2Field>
      <player1Goalkeeper>
        {getDataManager.getPlayerGoalkeeper(getPlayer1).map(_.toXml).getOrElse(<empty/>)}
      </player1Goalkeeper>
      <player2Goalkeeper>
        {getDataManager.getPlayerGoalkeeper(getPlayer2).map(_.toXml).getOrElse(<empty/>)}
      </player2Goalkeeper>
      <player1Score>
        {getScores.getScorePlayer1}
      </player1Score>
      <player2Score>
        {getScores.getScorePlayer2}
      </player2Score>
    </gameState>
  }


  def toJson: JsObject = Json.obj(
    "attacker" -> getRoles.attacker.toJson,
    "defender" -> getRoles.defender.toJson,
    "player1Hand" -> getDataManager.getPlayerHand(getPlayer1).toList.map(_.toJson),
    "player2Hand" -> getDataManager.getPlayerHand(getPlayer2).toList.map(_.toJson),
    "player1Field" -> getDataManager.getPlayerDefenders(getPlayer1).map(_.toJson),
    "player2Field" -> getDataManager.getPlayerDefenders(getPlayer2).map(_.toJson),
    "player1Goalkeeper" -> getDataManager.getPlayerGoalkeeper(getPlayer1).map(_.toJson).getOrElse(Json.obj()),
    "player2Goalkeeper" -> getDataManager.getPlayerGoalkeeper(getPlayer2).map(_.toJson).getOrElse(Json.obj()),
    "player1Score" -> getScores.getScorePlayer1,
    "player2Score" -> getScores.getScorePlayer2
  )
}
