package model.playingFiledComponent.strategy.swapStrategy
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import javax.inject.Inject
class SwapHandler @Inject()(
                   playingField: IPlayingField,
                   private var swapStrategy: SwapStrategy = new HandSwapStrategy()
                 ) {
  private lazy val roles: IRolesManager = playingField.getRoles
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
