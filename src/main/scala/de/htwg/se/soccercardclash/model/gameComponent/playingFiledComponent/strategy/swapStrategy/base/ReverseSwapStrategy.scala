package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IPlayerActionManager, IRolesManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.util.NoSwapsEvent
import scala.collection.mutable

class ReverseSwapStrategy(playerActionService: IPlayerActionManager) extends ISwapStrategy {

  override def swap(playingField: IPlayingField): Boolean = {
    val dataManager = playingField.getDataManager
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val hand = dataManager.getPlayerHand(attacker)

    def handleNoAction(): Boolean = {
      attacker.actionStates.get(PlayerActionPolicies.Swap).foreach {
        case OutOfActions => playingField.notifyObservers(NoSwapsEvent(attacker))
        case _            => ()
      }
      false
    }

    Option.when(playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      val cards = hand.toList
      if (cards.size >= 2) {
        val reversedHand = HandCardsQueue(cards.reverse)
        dataManager.setPlayerHand(attacker, reversedHand)

        val updatedAttacker = playerActionService.performAction(attacker, PlayerActionPolicies.Swap)
        roles.setRoles(updatedAttacker, roles.defender)

        playingField.notifyObservers()
        true
      } else {
        false
      }
    }.getOrElse {
      handleNoAction()
    }
  }
}
