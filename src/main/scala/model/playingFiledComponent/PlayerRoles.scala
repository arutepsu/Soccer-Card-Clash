package model.playingFiledComponent
import model.playerComponent.Player

class PlayerRoles(player1: Player, player2: Player, playingField: PlayingField) {
  var attacker: Player = player1
  var defender: Player = player2

  def switchRoles(): Unit = {
    val temp = attacker
    attacker = defender
    defender = temp
    playingField.notifyObservers()
  }

  def setRoles(newAttacker: Player, newDefender: Player): Unit
  = {
    attacker = newAttacker
    defender = newDefender
    println(s"Roles set manually. Attacker: ${attacker.name}, Defender: ${defender.name}")
  }
}
