package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.swap

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}

import scala.collection.mutable
import scala.util.{Failure, Success}
class RegularSwapStrategy(index: Int, playerActionService: IPlayerActionManager) extends IActionStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    val gameCards = state.getGameCards
    val hand = gameCards.getPlayerHand(attacker)

    val canSwap =
      playerActionService.canPerform(attacker, PlayerActionPolicies.Swap) &&
        hand.getHandSize >= 2 &&
        index >= 0 &&
        index < hand.getHandSize

    if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      return (false, state, List(StateEvent.NoSwapsEvent(attacker)))
    }

    if (!canSwap) {
      return (false, state, Nil)
    }

    hand.swap(index, hand.getHandSize - 1) match {
      case Success(updatedHand) =>
        val updatedGameCards = gameCards.newPlayerHand(attacker, updatedHand)
        val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
        val updatedRoles = Roles(updatedAttacker, defender)

        val newState = state
          .newGameCards(updatedGameCards)
          .newRoles(updatedRoles)

        (true, newState, List(GameActionEvent.RegularSwap))

      case Failure(_) =>
        (false, state, Nil)
    }
  }
}