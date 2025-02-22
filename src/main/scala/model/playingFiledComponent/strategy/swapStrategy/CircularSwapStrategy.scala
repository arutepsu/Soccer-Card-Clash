package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
import model.playingFiledComponent.manager.IDataManager
import model.playerComponent.playerRole.IRolesManager
import model.playingFiledComponent.IPlayingField
class CircularSwapStrategy(index: Int) extends ISwapStrategy {
  override def swap(playingField: IPlayingField): Unit = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackerHand = data.getPlayerHand(roles.attacker)
    
    if (attackerHand.getHandSize < 2) {
      return
    }

    if (index < 0 || index >= attackerHand.getHandSize) {
      return
    }

    val cardToMove = attackerHand.remove(index)
    attackerHand.enqueue(cardToMove)
  }
}
