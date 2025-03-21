package model.playingFiledComponent.strategy.swapStrategy.base

import controller.{Events, NoSwapsEvent}
import model.playerComponent.playerAction.*
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.dataStructure.HandCardsQueue
import model.playingFiledComponent.manager.{IDataManager, IRolesManager}
import model.playingFiledComponent.strategy.swapStrategy.ISwapStrategy

import scala.collection.mutable
class HandSwapStrategy(index: Int) extends ISwapStrategy {
  override def swap(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackerBeforeAction = roles.attacker
    
    attackerBeforeAction.actionStates.get(PlayerActionPolicies.Swap) match {
      case Some(OutOfActions) => playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 => playingField.notifyObservers(NoSwapsEvent(attackerBeforeAction))
        return false
      case _ =>
    }

    val attackerHand = data.getPlayerHand(attackerBeforeAction)
    if (attackerHand.getHandSize < 2 || index < 0 || index >= attackerHand.getHandSize) {
      return false
    }

    val lastIndex = attackerHand.getHandSize - 1
    val chosenCard = attackerHand(index)
    val lastCard = attackerHand(lastIndex)

    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)
    
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
