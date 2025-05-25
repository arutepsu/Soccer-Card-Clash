package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RolesFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RolesFactory" should {

    "create roles with attacker and defender" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val factory = new RolesFactory
      val roles = factory.create(attacker, defender)

      roles.attacker shouldBe attacker
      roles.defender shouldBe defender
    }
  }

  "A Roles" should {

    "switch roles correctly" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val roles = Roles(attacker, defender)

      val switched = roles.switchRoles()

      switched.attacker shouldBe defender
      switched.defender shouldBe attacker
    }

    "create new roles with specified players" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val newAttacker = mock[IPlayer]
      val newDefender = mock[IPlayer]

      val roles = Roles(attacker, defender)
      val updated = roles.newRoles(newAttacker, newDefender)

      updated.attacker shouldBe newAttacker
      updated.defender shouldBe newDefender
    }
  }
}
