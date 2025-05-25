package de.htwg.se.soccercardclash.model.playerComponent

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import play.api.libs.json.*
import de.htwg.se.soccercardclash.model.playerComponent.base.PlayerType
import scala.collection.mutable
import scala.xml.*
import de.htwg.se.soccercardclash.model.playerComponent.base.*
trait IPlayer extends Serializable {
  def name: String

  def actionStates: Map[PlayerActionPolicies, PlayerActionState]

  def setName(newName: String): IPlayer

  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer

  def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer

  def getActionStates: Map[PlayerActionPolicies, PlayerActionState]

  def playerType: PlayerType

  def toXml: Elem =
    <Player name={name}
            type={playerType match {
              case Human   => "Human"
              case AI(_)   => "AI"
            }}
            strategy={playerType match {
              case AI(strategy) => strategy.getClass.getSimpleName.replace("$", "")
              case _            => ""
            }}>
      <ActionStates>
        {actionStates.map { case (policy, state) =>
        <ActionState policy={policy.toString}>
          {state.toString}
        </ActionState>
      }}
      </ActionStates>
    </Player>
  
  def toJson: JsObject = {
    val base = Json.obj(
      "name" -> name,
      "type" -> (playerType match {
        case Human   => "Human"
        case AI(_)   => "AI"
      }),
      "actionStates" -> actionStates.map {
        case (policy, state) => policy.toString -> JsString(state.toString)
      }
    )

    playerType match {
      case AI(strategy) =>
        base + ("strategy" -> JsString(strategy.getClass.getSimpleName.replace("$", "")))
      case _ =>
        base
    }
  }

  def isAI: Boolean
  def equals(obj: Any): Boolean

  def hashCode(): Int
}
