package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.RolesManager
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RolesManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RolesManager" should {

    "initialize with attacker and defender" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)

      manager.attacker shouldBe attacker
      manager.defender shouldBe defender
    }

//    "switch attacker and defender and notify observers" in {
//      val mockField = mock[IPlayingField]
//      val attacker = mock[IPlayer]
//      val defender = mock[IPlayer]
//
//      val manager = new RolesManager(mockField, attacker, defender)
//
//      manager.switchRoles()
//
//      manager.attacker shouldBe defender
//      manager.defender shouldBe attacker
//      verify(mockField).notifyObservers()
//    }

    "set new roles" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val newAttacker = mock[IPlayer]
      val newDefender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)

      manager.setRoles(newAttacker, newDefender)

      manager.attacker shouldBe newAttacker
      manager.defender shouldBe newDefender
    }

    "reset roles to default players" in {
      val mockField = mock[IPlayingField]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val manager = new RolesManager(mockField, attacker, defender)
      manager.reset()

      manager.attacker shouldBe Player("NewAttacker", List())
      manager.defender shouldBe Player("NewDefender", List())
    }
  }
}
