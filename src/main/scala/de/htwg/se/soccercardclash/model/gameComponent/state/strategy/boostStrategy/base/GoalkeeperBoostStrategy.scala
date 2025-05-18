package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.Roles
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.IBoostStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, GameActionEvent, ObservableEvent, StateEvent}

class GoalkeeperBoostStrategy(
                               playerActionService: IPlayerActionManager
                             ) extends IBoostStrategy {

  override def boost(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    val dataManager = playingField.getDataManager
    val goalkeeperOpt = dataManager.getPlayerGoalkeeper(attacker)

    goalkeeperOpt match {
      case Some(goalkeeper) =>
        if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)) {
          return (false, playingField, List(StateEvent.NoBoostsEvent(attacker)))
        }

        val boostedGoalkeeper = goalkeeper.boost()
        val updatedDataManager = dataManager.setGoalkeeperForAttacker(attacker, boostedGoalkeeper)
        val attackerAfterAction = playerActionService.performAction(attacker, PlayerActionPolicies.Boost)
        val updatedRoles = Roles(attackerAfterAction, defender)

        val updatedField = playingField
          .withDataManager(updatedDataManager)
          .withRoles(updatedRoles)

        (true, updatedField, List(GameActionEvent.BoostGoalkeeper))

      case None =>
        (false, playingField, Nil)
    }
  }
}

