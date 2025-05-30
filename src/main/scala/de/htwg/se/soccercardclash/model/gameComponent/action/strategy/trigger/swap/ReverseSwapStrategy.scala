package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swap

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, StateEvent}

import scala.collection.mutable
class ReverseSwapStrategy(playerActionService: IPlayerActionManager) extends IActionStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    var gameCards = state.getGameCards
    val roles = state.getRoles
    val attacker = roles.attacker
    val defender = roles.defender
    val hand = gameCards.getPlayerHand(attacker)

    if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      attacker.actionStates.get(PlayerActionPolicies.Swap) match {
        case Some(OutOfActions) => return (false, state, List(StateEvent.NoSwapsEvent(attacker)))
        case _ => return (false, state, Nil)
      }
    }

    val cards = hand.toList
    if (cards.size >= 2) {
      val reversedHand = HandCardsQueue(cards.reverse)

      gameCards = gameCards.newPlayerHand(attacker, reversedHand)
      val updatedGameCards = state.newGameCards(gameCards)

      val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
      val updatedRoles = Roles(updatedAttacker, defender)

      val newState = updatedGameCards.newRoles(updatedRoles)

      (true, newState, List(GameActionEvent.ReverseSwap))
    } else {
      (false, state, Nil)
    }
  }
}