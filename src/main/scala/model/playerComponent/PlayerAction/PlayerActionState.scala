package model.playerComponent.PlayerAction

import model.playerComponent.Player
import model.playerComponent.PlayerAction.PlayerAction

trait PlayerActionState {
  def performAction(player: Player, action: PlayerAction): Player
}
