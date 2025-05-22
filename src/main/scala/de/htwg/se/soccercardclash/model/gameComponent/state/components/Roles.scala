package de.htwg.se.soccercardclash.model.gameComponent.state.components

import com.google.inject.Inject
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import play.api.libs.json.*

import scala.xml.*

trait IRolesFactory {
  def create(attacker: IPlayer, defender: IPlayer): IRoles
}

class RolesFactory extends IRolesFactory {
  override def create(attacker: IPlayer, defender: IPlayer): IRoles =
    Roles(attacker, defender)
}

case class Roles(
                  attacker: IPlayer,
                  defender: IPlayer
                ) extends IRoles {

  override def switchRoles(): Roles =
    Roles(defender, attacker)

  override def newRoles(newAttacker: IPlayer, newDefender: IPlayer): Roles =
    Roles(newAttacker, newDefender)

}

trait IRoles {
  def attacker: IPlayer

  def defender: IPlayer

  def switchRoles(): IRoles

  def newRoles(newAttacker: IPlayer, newDefender: IPlayer): IRoles
}
