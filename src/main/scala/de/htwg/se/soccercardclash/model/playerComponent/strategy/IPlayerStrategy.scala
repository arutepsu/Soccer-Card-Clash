package de.htwg.se.soccercardclash.model.playerComponent.strategy

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.PlayerType
import de.htwg.se.soccercardclash.util.*


trait IPlayerStrategy {
  def decideAction(gameState: GameContext, player: IPlayer): PlayerAction
}

class SimpleAIStrategy extends IPlayerStrategy {
  override def decideAction(ctx: GameContext, player: IPlayer): PlayerAction = {
    val state = ctx.state
    val dataManager = state.getDataManager
    val roles = state.getRoles
    val defender = if (roles.attacker == player) roles.defender else roles.attacker

    val attackerHand = dataManager.getPlayerHand(player)
    val defenderField = dataManager.getPlayerDefenders(defender)
    
    if (defenderField.nonEmpty) {
      SingleAttackAction(defenderIndex = 0)
    } else {
      NoOpAction
    }
  }
}
