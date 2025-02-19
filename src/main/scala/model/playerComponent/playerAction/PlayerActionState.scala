package model.playerComponent.playerAction

import model.playerComponent.IPlayer
import model.playerComponent.playerAction.PlayerActionPolicies

trait PlayerActionState {
  def performAction(player: IPlayer, action: PlayerActionPolicies): IPlayer
}
