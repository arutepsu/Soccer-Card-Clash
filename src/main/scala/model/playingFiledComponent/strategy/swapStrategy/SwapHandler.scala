package model.playingFiledComponent.strategy.swapStrategy
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.RolesManager
import model.playingFiledComponent.IPlayingField

class SwapHandler(
                   playingField: IPlayingField,
                   roles: RolesManager,
                   private var swapStrategy: SwapStrategy = new HandSwapStrategy()
                 ) {
  
  def setSwapStrategy(newStrategy: SwapStrategy): Unit = {
    swapStrategy = newStrategy
  }
  def swapAttacker(index: Int): Unit = {
    val attackerHand = playingField.getDataManager.getPlayerHand(roles.attacker)
    swapStrategy.swap(attackerHand, index)
    roles.attacker.performAction(PlayerActionPolicies.Swap)
    playingField.notifyObservers()
  }
}
