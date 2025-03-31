package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.util.{Events, NoSwapsEvent}

import scala.collection.mutable
class RegularSwapStrategy(
                           index: Int,
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
    if (attackerHand.getHandSize < 2 || index < 0 || index >= attackerHand.getHandSize) {
      return false
    }

    val lastIndex = attackerHand.getHandSize - 1
    val chosenCard = attackerHand(index)
    val lastCard = attackerHand(lastIndex)

    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)
    
    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.Swap)

    roles.setRoles(attackerAfterAction, roles.defender)

    playingField.notifyObservers()
    true
  }
}
