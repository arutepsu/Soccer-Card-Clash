package model.playerComponent.playerRole

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField

class RolesManager(playingField: IPlayingField, player1: IPlayer, player2: IPlayer) {
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
  }
}