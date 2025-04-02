package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.util.{Events, NoSwapsEvent}

import scala.collection.mutable

class RegularSwapStrategy(index: Int, playerActionService: IPlayerActionManager) extends ISwapStrategy {

  override def swap(playingField: IPlayingField): Boolean = {
    val dataManager = playingField.getDataManager
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val hand = dataManager.getPlayerHand(attacker)

    def canSwap: Boolean =
      playerActionService.canPerform(attacker, PlayerActionPolicies.Swap) &&
        hand.getHandSize >= 2 &&
        index >= 0 &&
        index < hand.getHandSize

    def swapCards(): Unit = {
      val lastIndex = hand.getHandSize - 1
      val temp1 = hand(index)
      val temp2 = hand(lastIndex)
      hand.update(index, temp2)
      hand.update(lastIndex, temp1)
    }

    if (!playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      playingField.notifyObservers(NoSwapsEvent(attacker))
      false
    } else if (!canSwap) {
      false
    } else {
      swapCards()
      val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
      roles.setRoles(updatedAttacker, roles.defender)
      playingField.notifyObservers()
      true
    }
  }
}