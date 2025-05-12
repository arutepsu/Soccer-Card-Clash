package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, Roles}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{EventDispatcher, Events, ObservableEvent}

import scala.collection.mutable
import scala.util.{Failure, Success}

class RegularSwapStrategy(index: Int, playerActionService: IPlayerActionManager) extends ISwapStrategy {

  override def swap(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    val dataManager = playingField.getDataManager
    val hand = dataManager.getPlayerHand(attacker)

    val canSwap =
      playerActionService.canPerform(attacker, PlayerActionPolicies.Swap) &&
        hand.getHandSize >= 2 &&
        index >= 0 &&
        index < hand.getHandSize

    if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      return (false, playingField, List(Events.NoSwapsEvent(attacker)))
    }

    if (!canSwap) {
      return (false, playingField, Nil)
    }

    hand.swap(index, hand.getHandSize - 1) match {
      case Success(updatedHand) =>
        val updatedDataManager = dataManager.setPlayerHand(attacker, updatedHand)
        val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
        val updatedRoles = Roles(updatedAttacker, defender)

        val updatedField = playingField
          .withDataManager(updatedDataManager)
          .withRolesManager(updatedRoles)

        (true, updatedField, List(Events.RegularSwap))

      case Failure(_) =>
        (false, playingField, Nil)
    }
  }
}
