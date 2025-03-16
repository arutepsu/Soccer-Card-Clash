package model.playerComponent
import model.cardComponent.ICard
import model.playerComponent.playerAction.{PlayerActionPolicies, PlayerActionState}
import play.api.libs.json._
import scala.xml._

import scala.collection.mutable
trait IPlayer extends Serializable {
  def name: String
  def cards: List[ICard]
  def actionStates: Map[PlayerActionPolicies, PlayerActionState]
  def getCards: List[ICard]
  def setName(newName: String): IPlayer
  def performAction(action: PlayerActionPolicies): IPlayer
  def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer
  def setHandCards(newCards: List[ICard]): IPlayer
  def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer
  def getActionStates: Map[PlayerActionPolicies, PlayerActionState]

  def toXml: Elem =
    <Player name={name}>
      <Cards>
        {cards.map(_.toXml)}
      </Cards>
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
    "cards" -> cards.map(_.toJson),
    "actionStates" -> actionStates.map { case (policy, state) => policy.toString -> state.toString }
  )
  def equals(obj: Any): Boolean
  def hashCode(): Int
}
