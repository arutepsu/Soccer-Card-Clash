package de.htwg.se.soccercardclash.model.cardComponent.dataStructure

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class HandCardsQueueFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "HandCardsQueueFactory" should {
    "create a HandCardsQueue with given cards" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val factory = new HandCardsQueueFactory

      val queue = factory.create(List(card1, card2))

      queue shouldBe a[HandCardsQueue]
      queue.toList should contain inOrder (card1, card2)
    }
  }
}

