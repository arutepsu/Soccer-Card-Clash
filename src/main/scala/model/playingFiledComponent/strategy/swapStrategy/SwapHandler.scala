package model.playingFiledComponent.strategy.swapStrategy
import model.playingFiledComponent.PlayingField
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.RolesManager
import model.playerComponent.base.Player

class SwapHandler(
                   playingField: PlayingField,
                   roles: RolesManager,
                   private var swapStrategy: SwapStrategy = new HandSwapStrategy()
                 ) {
  
  def setSwapStrategy(newStrategy: SwapStrategy): Unit = {
    swapStrategy = newStrategy
  }
  def swapAttacker(index: Int): Unit = {
    val attackerHand = playingField.fieldState.getPlayerHand(roles.attacker)
    swapStrategy.swap(attackerHand, index)
    roles.attacker.performAction(PlayerActionPolicies.Swap)
    playingField.notifyObservers()
  }
}
