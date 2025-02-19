package model.playerComponent

import model.cardComponent.ICard
import model.cardComponent.base.Card
import model.playerComponent.PlayerAction.{PlayerAction, PlayerActionState}
import model.playerComponent.PlayerAction.CanPerformAction

case class Player(
                   name: String,
                   cards: List[ICard],
                   actionStates: Map[PlayerAction, PlayerActionState] =
                   PlayerAction.values.map(action => action -> CanPerformAction(action.maxUses)).toMap
                 ) {

  override def toString: String = s"Player: $name, Cards: ${cards.mkString(", ")}"
  def getCards: List[ICard] = cards

  def setName(newName: String): Player = this.copy(name = newName)

  def performAction(action: PlayerAction): Player = {
    actionStates.get(action) match {
      case Some(state) => state.performAction(this, action)
      case None =>
        println(s"Action ${action.toString} is not available for ${this.name}.")
        this
    }
  }

  def updateActionState(action: PlayerAction, newState: PlayerActionState): Player = {
    this.copy(actionStates = actionStates.updated(action, newState))
  }

  def setHandCards(newCards: List[Card]): Player = this.copy(cards = newCards)
}

