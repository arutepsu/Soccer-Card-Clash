package model.playerComponent.base

import model.cardComponent.ICard
import model.cardComponent.base.Card
import model.playerComponent.IPlayer
import model.playerComponent.playerAction.{CanPerformAction, PlayerActionPolicies, PlayerActionState}
import model.playerComponent.playerRole.PlayerRole
case class Player(
                   name: String,
                   role: PlayerRole,
                   cards: List[ICard],
                   actionStates: Map[PlayerActionPolicies, PlayerActionState] =
                   PlayerActionPolicies.values.map(action => action -> CanPerformAction(action.maxUses)).toMap
                 )extends IPlayer{
  
  override def toString: String = s"Player: $name ($role), Cards: ${cards.mkString(", ")}"

  override def getCards: List[ICard] = cards

  override def setName(newName: String): IPlayer = this.copy(name = newName)

  override def setRole(newRole: PlayerRole): IPlayer = this.copy(role = newRole)

  override def performAction(action: PlayerActionPolicies): IPlayer = {
    actionStates.get(action) match {
      case Some(state) => state.performAction(this, action)
      case None =>
        println(s"Action ${action.toString} is not available for ${this.name}.")
        this
    }
  }

  override def updateActionState(action: PlayerActionPolicies, newState: PlayerActionState): IPlayer =
    this.copy(actionStates = actionStates.updated(action, newState))

  override def setHandCards(newCards: List[ICard]): IPlayer = this.copy(cards = newCards)

  override def copy(
                     name: String = this.name,
                     role: PlayerRole = this.role,
                     cards: List[ICard] = this.cards,
                     actionStates: Map[PlayerActionPolicies, PlayerActionState] = this.actionStates
                   ): Player = {
    Player(name, role, cards, actionStates)
  }
}
