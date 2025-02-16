//package model.playerComponent
//
//import model.cardComponent.base.Card
//import scala.collection.mutable
//
//case class Player(
//                   name: String,
//                   cards: List[Card],
//                   usedBoostCard: Option[Boolean] = Some(false),
//                   usedExecuteAttack: Option[Boolean] = Some(false)
//                 ) {
//  override def toString: String = s"Player: $name, Cards: ${cards.mkString(", ")}"
//
//  def getCards: List[Card] = cards
//
//  def setName(newName: String): Player = {
//    this.copy(name = newName)
//  }
//
//  def useBoostCard(): Player = {
//    if (usedBoostCard.contains(true)) {
//      println(s"$name has already used BoostCard!")
//      this
//    } else {
//      println(s"$name is using BoostCard!")
//      this.copy(usedBoostCard = Some(true))
//    }
//  }
//
//  def useExecuteAttack(): Player = {
//    if (usedExecuteAttack.contains(true)) {
//      println(s"$name has already used ExecuteAttack!")
//      this
//    } else {
//      println(s"$name is using ExecuteAttack!")
//      this.copy(usedExecuteAttack = Some(true))
//    }
//  }
//  def setHandCards(newCards: List[Card]): Player = {
//    this.copy(cards = newCards)
//  }
//}
package model.playerComponent

import model.cardComponent.base.Card
import model.playerComponent.PlayerAction.{PlayerAction, PlayerActionState}
import model.playerComponent.PlayerAction.PlayerAction.CanPerformAction

case class Player(
                   name: String,
                   cards: List[Card],
                   actionStates: Map[PlayerAction, PlayerActionState] =
                   PlayerAction.values.map(action => action -> CanPerformAction(action.maxUses)).toMap
                 ) {

  override def toString: String = s"Player: $name, Cards: ${cards.mkString(", ")}"

  def getCards: List[Card] = cards

  def setName(newName: String): Player = this.copy(name = newName)

  /** ✅ Perform an action using the enum */
  def performAction(action: PlayerAction): Player = {
    actionStates.get(action) match {
      case Some(state) => state.performAction(this, action)
      case None =>
        println(s"Action ${action.toString} is not available for ${this.name}.")
        this
    }
  }

  /** ✅ Update action state dynamically */
  def updateActionState(action: PlayerAction, newState: PlayerActionState): Player = {
    this.copy(actionStates = actionStates.updated(action, newState))
  }

  def setHandCards(newCards: List[Card]): Player = this.copy(cards = newCards)
}

