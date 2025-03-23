package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.controller.NoSwapsEvent
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.ISwapStrategy
import scala.collection.mutable
class ReverseSwapStrategy(
                           playerActionService: IPlayerActionManager
                         ) extends ISwapStrategy {

  override def swap(playingField: IPlayingField): Boolean = {
    val data = playingField.getDataManager
    val roles = playingField.getRoles
    val attackerBeforeAction = roles.attacker

    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.Swap)) {
      attackerBeforeAction.actionStates.get(PlayerActionPolicies.Swap) match {
        case Some(OutOfActions) =>
          playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
        case _ => // no notify for CanPerform(0)
      }
      return false
    }

    val attackerHand = data.getPlayerHand(attackerBeforeAction)
    if (attackerHand.getHandSize < 2) return false

    val reversedCards = attackerHand.getCards.reverse
    while (attackerHand.getHandSize > 0) attackerHand.removeLastCard()
    reversedCards.foreach(attackerHand.addCard)
    reversedCards.indices.foreach(i => attackerHand.update(i, reversedCards(i)))

    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.Swap)
    roles.setRoles(attackerAfterAction, roles.defender)
    playingField.notifyObservers()
    true
  }
}
