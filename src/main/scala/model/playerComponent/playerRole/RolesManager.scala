package model.playerComponent.playerRole

import model.playerComponent.IPlayer
import model.playingFiledComponent.IPlayingField
import com.google.inject.Inject
import model.playerComponent.base.Player

class RolesManager @Inject() (
                               private val playingField: IPlayingField,
                               private var a: IPlayer,
                               private var d: IPlayer
                             ) extends IRolesManager {
  override def attacker: IPlayer = a
  override def defender: IPlayer = d
  override def switchRoles(): Unit = {
    val temp = a
    a = d
    d = temp
    playingField.notifyObservers()
  }
  override def setRoles(newAttacker: IPlayer, newDefender: IPlayer): Unit = {
    a = newAttacker
    d = newDefender
  }
  override def reset(): Unit = {
    a = Player("NewAttacker", List()) // Provide a default or a factory-created player
    d = Player("NewDefender", List())
  }
}
