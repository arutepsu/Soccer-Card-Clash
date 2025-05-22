package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.Roles
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.IBoostStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}

class DefenderBoostStrategy(index: Int, playerActionService: IPlayerActionManager) extends IBoostStrategy {

  override def boost(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    val gameCards = state.getGameCards
    val defenders = gameCards.getPlayerDefenders(attacker)

    if (index < 0 || index >= defenders.size) {
      return (false, state, Nil)
    }

    val cardOpt = defenders(index)

    if (cardOpt.isEmpty || !playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)) {
      return (false, state, List(StateEvent.NoBoostsEvent(attacker)))
    }

    val originalCard = cardOpt.get
    val boostedCard = originalCard.boost()
    val updatedDefenders = defenders.updated(index, Some(boostedCard))

    val updatedGameCards = gameCards.newPlayerDefenders(attacker, updatedDefenders)
    val attackerAfterAction = playerActionService.performAction(attacker, PlayerActionPolicies.Boost)
    val updatedRoles = Roles(attackerAfterAction, defender)

    val newState = state
      .newGameCards(updatedGameCards)
      .newRoles(updatedRoles)
    
    (true, newState, List(GameActionEvent.BoostDefender))
  }
}
