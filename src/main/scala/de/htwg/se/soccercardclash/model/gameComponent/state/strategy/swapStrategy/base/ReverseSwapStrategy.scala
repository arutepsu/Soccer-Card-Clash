package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, Roles}
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, StateEvent}

import scala.collection.mutable

class ReverseSwapStrategy(playerActionService: IPlayerActionManager) extends ISwapStrategy {

  override def swap(playingField: IGameState): (Boolean, IGameState, List[ObservableEvent]) = {
    var dataManager = playingField.getDataManager
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val defender = roles.defender
    val hand = dataManager.getPlayerHand(attacker)

    if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      attacker.actionStates.get(PlayerActionPolicies.Swap) match {
        case Some(OutOfActions) => return (false, playingField, List(StateEvent.NoSwapsEvent(attacker)))
        case _ => return (false, playingField, Nil)
      }
    }

    val cards = hand.toList
    if (cards.size >= 2) {
      val reversedHand = HandCardsQueue(cards.reverse)

      dataManager = dataManager.updatePlayerHand(attacker, reversedHand)
      val updatedField1 = playingField.updateDataManager(dataManager)

      val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
      val updatedRoles = Roles(updatedAttacker, defender)

      val updatedField2 = updatedField1.updateRoles(updatedRoles)

      (true, updatedField2, List(GameActionEvent.ReverseSwap))
    } else {
      (false, playingField, Nil)
    }
  }
}
