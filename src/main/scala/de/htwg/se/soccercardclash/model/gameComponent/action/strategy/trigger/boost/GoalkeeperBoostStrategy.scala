package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.IActionStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.Roles
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}
class GoalkeeperBoostStrategy(
                               playerActionService: IPlayerActionManager
                             ) extends IActionStrategy {

  override def execute(state: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = state.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    val gameCards = state.getGameCards
    val goalkeeperOpt = gameCards.getPlayerGoalkeeper(attacker)

    goalkeeperOpt match {
      case Some(goalkeeper) =>
        if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)) {
          return (false, state, List(StateEvent.NoBoostsEvent(attacker)))
        }

        val boostedGoalkeeper = goalkeeper.boost()
        val updatedGameCards = gameCards.newGoalkeeperForAttacker(attacker, boostedGoalkeeper)
        val attackerAfterAction = playerActionService.performAction(attacker, PlayerActionPolicies.Boost)
        val updatedRoles = Roles(attackerAfterAction, defender)

        val newState = state
          .newGameCards(updatedGameCards)
          .newRoles(updatedRoles)

        (true, newState, List(GameActionEvent.BoostGoalkeeper))

      case None =>
        (false, state, Nil)
    }
  }
}

