package model.playingFiledComponent.state.roleState

import model.playerComponent.Player
import model.playingFiledComponent.PlayingField
import model.playingFiledComponent.state.gameState.GameState
import model.playingFiledComponent.state.roleState.RoleState

class PlayerRoles(player1: Player, player2: Player, playingField: PlayingField) {
  var attacker: Player = player1
  var defender: Player = player2
  private var state: RoleState = new AttackerState()
  var gameState: GameState = GameState.SwitchRoles
  def switchRoles(): Unit = {
    val temp = attacker
    attacker = defender
    defender = temp

    // ✅ Delegate role switch logic to the state
    state.switchRoles(this)
    playingField.notifyObservers()
  }

  def setRoles(newAttacker: Player, newDefender: Player): Unit = {
    attacker = newAttacker
    defender = newDefender
    println(s"Roles set manually. Attacker: ${attacker.name}, Defender: ${defender.name}")
  }

  // ✅ Updates the state dynamically
  def setState(newState: RoleState): Unit = {
    state = newState
  }

  def getCurrentRole: String = state.getRoleName
}
