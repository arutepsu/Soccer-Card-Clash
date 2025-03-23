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
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackerBeforeAction = roles.attacker
    
    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.Swap)) {
      playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
      return false
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
    
    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.Swap)

    roles.setRoles(attackerAfterAction, roles.defender)
    playingField.notifyObservers()
    true
  }
}
