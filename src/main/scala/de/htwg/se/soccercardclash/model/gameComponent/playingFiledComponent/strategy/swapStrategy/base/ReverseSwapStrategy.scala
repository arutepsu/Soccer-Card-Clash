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

    def handleNoAction(): Boolean = {
      attacker.actionStates.get(PlayerActionPolicies.Swap).foreach {
        case OutOfActions => playingField.notifyObservers(NoSwapsEvent(attacker))
        case _            => ()
      }
      false
    }

    def reverseCards(cards: mutable.Queue[ICard]): List[ICard] = cards.toList.reverse


    def applyReversedHand(hand: IHandCardsQueue, cards: List[ICard]): Unit = {

      hand.getHandSize until 0 by -1 foreach (_ => hand.removeLastCard())
      cards.foreach(hand.addCard)
      cards.zipWithIndex.foreach((card, i) => hand.update(i, card))
    }

    Option.when(playerActionService.canPerform(attacker, PlayerActionPolicies.Swap)) {
      val hand = dataManager.getPlayerHand(attacker)
      val cards = hand.getCards
      if (cards.size >= 2) {
        val reversed = cards.toList.reverse
        applyReversedHand(hand, reversed)
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
