package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import play.api.libs.json.*
import scala.xml.*

class RolesManager @Inject()(
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
    a = Player("NewAttacker")
    d = Player("NewDefender")
  }
}

trait IRolesManager {
  def attacker: IPlayer

  def defender: IPlayer

  def switchRoles(): Unit

  def setRoles(newAttacker: IPlayer, newDefender: IPlayer): Unit

  def reset(): Unit
  
}
