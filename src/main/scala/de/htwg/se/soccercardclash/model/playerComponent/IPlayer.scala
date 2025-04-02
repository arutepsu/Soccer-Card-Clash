package de.htwg.se.soccercardclash.model.playerComponent

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import play.api.libs.json.*

import scala.collection.mutable
import scala.xml.*

trait IPlayer extends Serializable {
  def name: String

  def actionStates: Map[PlayerActionPolicies, PlayerActionState]

  def setName(newName: String): IPlayer

  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer

  def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer

  def getActionStates: Map[PlayerActionPolicies, PlayerActionState]

  def toXml: Elem =
    <Player name={name}>
      <ActionStates>
        {actionStates.map { case (policy, state) =>
        <ActionState policy={policy.toString}>
          {state.toString}
        </ActionState>
      }}
      </ActionStates>
    </Player>

  def toJson: JsObject = Json.obj(
    "name" -> name,
    "actionStates" -> actionStates.map { case (policy, state) => policy.toString -> state.toString }
  )

  def equals(obj: Any): Boolean

  def hashCode(): Int
}
