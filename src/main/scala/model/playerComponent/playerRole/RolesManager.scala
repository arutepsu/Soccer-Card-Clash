package model.playerComponent.playerRole

import model.playerComponent.IPlayer
import model.playerComponent.playerRole.PlayerRole
import model.playerComponent.base.Player
import model.playingFiledComponent.base.PlayingField

class RolesManager(player1: IPlayer, player2: IPlayer, playingField: PlayingField) {
  var attacker: IPlayer = player1
  var defender: IPlayer = player2
  def switchRoles(): Unit = {
    val temp = attacker
    attacker = defender
    defender = temp
    playingField.notifyObservers()
  }

  def setRoles(newAttacker: IPlayer, newDefender: IPlayer): Unit = {
    attacker = newAttacker
    defender = newDefender
    println(s"Roles set manually. Attacker: ${attacker.name}, Defender: ${defender.name}")
  }

}