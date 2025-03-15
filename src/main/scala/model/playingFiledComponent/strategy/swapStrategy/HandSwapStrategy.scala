package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
import model.playingFiledComponent.manager.IDataManager
import model.playerComponent.playerRole.IRolesManager
import model.playingFiledComponent.IPlayingField
import model.playerComponent.playerAction.*
class HandSwapStrategy(index: Int) extends ISwapStrategy {
  override def swap(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackerBeforeAction = roles.attacker

    // Check if the attacker has any Swap actions left
    attackerBeforeAction.actionStates.get(PlayerActionPolicies.Swap) match {
      case Some(OutOfActions) =>
        println(s"[DEBUG] ${attackerBeforeAction.name} has no Swap actions left. Execution is prevented.")
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
        println(s"[DEBUG] ${attackerBeforeAction.name} has no Swap actions left. Execution is prevented.")
        return false
      case _ => // Continue execution
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

    // Perform the action and update the attacker's state
    val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.Swap)

    // Debug print for remaining Swap uses
    attackerAfterAction.actionStates.get(PlayerActionPolicies.Swap) match {
      case Some(CanPerformAction(remainingUses)) =>
        println(s"[DEBUG] Remaining Swap uses for ${attackerAfterAction.name}: $remainingUses")
      case Some(OutOfActions) =>
        println(s"[DEBUG] ${attackerAfterAction.name} has no Swap actions left.")
      case _ =>
        println(s"[DEBUG] Unexpected state for Swap action.")
    }

    // Update attacker in roles
    roles.setRoles(attackerAfterAction, roles.defender)

    playingField.notifyObservers()
    println(s"[DEBUG] Observers notified of the change.")
    true
  }
}
