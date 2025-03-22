package model.playingFieldComponent.dataStructure

import model.cardComponent.ICard
import model.cardComponent.factory.CardDeserializer
import model.playingFiledComponent.dataStructure.{HandCardsQueueDeserializer, IHandCardsQueue, IHandCardsQueueFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import play.api.libs.json._
import scala.xml.Elem
import org.mockito.ArgumentMatchers.any

class HandCardsQueueDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "HandCardsQueueDeserializer" should {
    "deserialize from XML using factory and card deserializer" in {
      val mockCard = mock[ICard]
      val mockCardDeserializer = mock[CardDeserializer]
      val mockFactory = mock[IHandCardsQueueFactory]
      val mockQueue = mock[IHandCardsQueue]

      val xml: Elem =
        <HandCardsQueue>
          <cards>
            <Card><suit>Clubs</suit><value>Two</value><type>Regular</type></Card>
          </cards>
        </HandCardsQueue>

      when(mockCardDeserializer.fromXml(any())).thenReturn(mockCard)
      when(mockFactory.create(List(mockCard))).thenReturn(mockQueue)

      val deserializer = new HandCardsQueueDeserializer(mockCardDeserializer, mockFactory)
      val result = deserializer.fromXml(xml)

      result shouldBe mockQueue
    }

    "deserialize from JSON using factory and card deserializer" in {
      val mockCard = mock[ICard]
      val mockCardDeserializer = mock[CardDeserializer]
      val mockFactory = mock[IHandCardsQueueFactory]
      val mockQueue = mock[IHandCardsQueue]

      val json = Json.obj("cards" -> Json.arr(Json.obj("suit" -> "Hearts", "value" -> "Three", "type" -> "Regular")))

      when(mockCardDeserializer.fromJson(any())).thenReturn(mockCard)
      when(mockFactory.create(List(mockCard))).thenReturn(mockQueue)

      val deserializer = new HandCardsQueueDeserializer(mockCardDeserializer, mockFactory)
      val result = deserializer.fromJson(json)

      result shouldBe mockQueue
    }
  }
}
