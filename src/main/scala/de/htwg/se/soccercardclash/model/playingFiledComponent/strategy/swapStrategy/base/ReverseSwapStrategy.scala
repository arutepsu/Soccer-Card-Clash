package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.controller.NoSwapsEvent
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IDataManager, IRolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.ISwapStrategy
import scala.collection.mutable
class ReverseSwapStrategy extends ISwapStrategy {
  override def swap(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackerBeforeAction = roles.attacker

    attackerBeforeAction.actionStates.get(PlayerActionPolicies.Swap) match {
      case Some(OutOfActions) =>
        playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
        return false
      case _ =>
    }

    val attackerHand = data.getPlayerHand(attackerBeforeAction)

    if (attackerHand.getHandSize < 2) {
      return false
    }

    val reversedCards = attackerHand.getCards.reverse

    while (attackerHand.getHandSize > 0) {
      attackerHand.removeLastCard()
    }

    reversedCards.foreach(attackerHand.addCard)

    for (i <- reversedCards.indices) {
      attackerHand.update(i, reversedCards(i))
    }

    val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.Swap)

    attackerAfterAction.actionStates.get(PlayerActionPolicies.Swap) match {
      case Some(CanPerformAction(remainingUses)) =>
      case Some(OutOfActions) => playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
      case _ =>
    }

    roles.setRoles(attackerAfterAction, roles.defender)
    playingField.notifyObservers()
    true
  }
}
