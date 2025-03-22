package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class PlayerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayerFactory" should {

    "create a Player with the given name and cards" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val name = "TestPlayer"

      val factory = new PlayerFactory()
      val player: IPlayer = factory.createPlayer(name, List(card1, card2))

      player.name shouldBe name
      player.getCards should contain inOrderOnly (card1, card2)
    }
  }
}
