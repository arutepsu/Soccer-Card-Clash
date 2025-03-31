package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayerFactorySpec extends AnyWordSpec with Matchers {

  "PlayerFactory" should {

    "create a Player with the given name" in {
      val name = "TestPlayer"

      val factory = new PlayerFactory()
      val player: IPlayer = factory.createPlayer(name)

      player.name shouldBe name
    }
  }
}
