package model.playerComponent.playerRole

import model.playerComponent.IPlayer
import model.playerComponent.playerRole.PlayerRole
import model.playerComponent.base.Player
import model.playingFiledComponent.PlayingField


class RolesManager(player1: IPlayer, player2: IPlayer, playingField: PlayingField) {
  var attacker: IPlayer = player1.setRole(Attacker)
  var defender: IPlayer = player2.setRole(Defender)

  def switchRoles(): Unit = {
    val temp = attacker
    attacker = defender.setRole(Attacker)
    defender = temp.setRole(Defender)

    playingField.notifyObservers()
  }
  def setRoles(newAttacker: IPlayer, newDefender: IPlayer): Unit = {
    attacker = newAttacker.setRole(Attacker)
    defender = newDefender.setRole(Defender)
  }
  
}
