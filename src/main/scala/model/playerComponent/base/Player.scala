package model.playerComponent.base

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.*
import play.api.libs.json._
import scala.xml._
case class Player(
                   name: String,
                   cards: List[ICard],
                   actionStates: Map[PlayerActionPolicies, PlayerActionState] =
                   PlayerActionPolicies.values.map(action => action -> CanPerformAction(action.maxUses)).toMap
                 ) extends IPlayer {

  override def toString: String = s"Player: $name, Cards: ${cards.mkString(", ")}"
  override def getCards: List[ICard] = cards

  override def setName(newName: String): IPlayer = this.copy(name = newName)

  override def performAction(action: PlayerActionPolicies): IPlayer = {
    actionStates.get(action) match {
      case Some(state) => state.performAction(this, action)
      case None =>
        this
    }
  }

  override def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer = {
    this.copy(actionStates = actionStates.updated(action, newState))
  }

  override def setHandCards(newCards: List[ICard]): IPlayer = this.copy(cards = newCards)

  override def setActionStates(newActionStates: Map[PlayerActionPolicies, PlayerActionState]): IPlayer = {
    this.copy(actionStates = newActionStates)
  }
  override def toXml: Elem =
    <Player>
      <name>{name}</name>
      <cards>
        {cards.map(_.toXml)}
      </cards>
      <actionStates>
        {actionStates.map { case (policy, state) =>
        <action>
          <policy>{policy.toString}</policy>
          <state>{state.toString}</state>
        </action>
      }}
      </actionStates>
    </Player>

  // ✅ Convert Player to JSON
  override def toJson: JsObject = Json.obj(
    "name" -> name,
    "cards" -> cards.map(_.toJson),
    "actionStates" -> actionStates.map { case (policy, state) => policy.toString -> state.toString }
  )
}