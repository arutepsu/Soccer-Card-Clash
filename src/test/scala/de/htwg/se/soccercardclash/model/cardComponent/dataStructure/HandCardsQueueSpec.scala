package de.htwg.se.soccercardclash.model.cardComponent.dataStructure

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import play.api.libs.json.*

import scala.util.{Failure, Success}

class HandCardsQueueSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A HandCardsQueue" should {

    "initialize with a list of cards" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]

      val queue = new HandCardsQueue(List(card1, card2))
      queue.toList should contain inOrder (card1, card2)
      queue.getHandSize shouldBe 2
    }

    "add a card to the front" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]

      val queue = new HandCardsQueue(List(card1))
      val updatedQueue = queue.addCard(card2) // get the new queue

      updatedQueue.toList.head shouldBe card2
      updatedQueue.getHandSize shouldBe 2
    }


    "remove the last card" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]

      val queue = new HandCardsQueue(List(card1, card2))
      val removed = queue.removeLastCard()

      removed match {
        case Success((removedCard, updatedQueue)) =>
          removedCard shouldBe card2
          updatedQueue.toList should contain only card1
        case Failure(_) =>
          fail("Expected successful removal of last card")
      }
    }


    "throw an exception when removing from empty queue" in {
      val queue = new HandCardsQueue(Nil)

      val result = queue.removeLastCard()

      result should matchPattern {
        case Failure(ex: RuntimeException) if ex.getMessage.contains("Hand is empty") =>
      }
    }

    "convert to XML correctly" in {
      val card = mock[ICard]
      when(card.toXml).thenReturn(<card/>)

      val queue = new HandCardsQueue(List(card))
      val xml = queue.toXml

      (xml \ "card").nonEmpty shouldBe true
    }

    "convert to JSON correctly" in {
      val card = mock[ICard]
      when(card.toJson).thenReturn(Json.obj("mock" -> "card"))

      val queue = new HandCardsQueue(List(card))
      val json = queue.toJson

      (json \ "cards")(0) shouldBe Json.obj("mock" -> "card")
    }
  }
}
