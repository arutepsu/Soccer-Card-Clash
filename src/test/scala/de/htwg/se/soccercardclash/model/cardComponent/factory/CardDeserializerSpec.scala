package de.htwg.se.soccercardclash.model.cardComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.types._
import de.htwg.se.soccercardclash.model.cardComponent.factory.ICardFactory
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json._

import scala.xml.Elem

class CardDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Mock CardFactory
  private val mockCardFactory: ICardFactory = mock[ICardFactory]

  // ✅ Create instance of `CardDeserializer`
  private val deserializer = new CardDeserializer(mockCardFactory)

  "CardDeserializer" should {

    "deserialize a RegularCard from XML correctly" in {
      val xml: Elem =
        <card>
          <type>Regular</type>
          <suit>Hearts</suit>
          <value>Ace</value>
        </card>

      val mockCard: ICard = mock[ICard]
      when(mockCardFactory.createCard(Value.Ace, Suit.Hearts)).thenReturn(mockCard)

      val result = deserializer.fromXml(xml)
      result shouldBe mockCard

      verify(mockCardFactory).createCard(Value.Ace, Suit.Hearts)
    }

    "deserialize a BoostedCard from XML correctly" in {
      val xml: Elem =
        <card>
          <type>Boosted</type>
          <suit>Clubs</suit>
          <value>10</value>
          <additionalValue>2</additionalValue>
        </card>

      val mockBaseCard = mock[RegularCard]
      when(mockCardFactory.createCard(Value.Ten, Suit.Clubs)).thenReturn(mockBaseCard)

      val result = deserializer.fromXml(xml)

      result shouldBe a[BoostedCard]
      result.asInstanceOf[BoostedCard].additionalValue shouldBe 2

      verify(mockCardFactory).createCard(Value.Ten, Suit.Clubs)
    }

    "throw an exception when deserializing from XML with an invalid suit" in {
      val xml: Elem =
        <card>
          <type>Regular</type>
          <suit>UnknownSuit</suit>
          <value>Ace</value>
        </card>

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("ERROR: Invalid suit value: 'UnknownSuit'")
    }

    "throw an exception when deserializing from XML with an invalid value" in {
      val xml: Elem =
        <card>
          <type>Regular</type>
          <suit>Diamonds</suit>
          <value>UnknownValue</value>
        </card>

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("Invalid card value: 'UnknownValue'")
    }

    "deserialize a RegularCard from JSON correctly" in {
      val json: JsObject = Json.obj(
        "type" -> "Regular",
        "suit" -> "Spades",
        "value" -> "King"
      )

      val mockCard: ICard = mock[ICard]
      when(mockCardFactory.createCard(Value.King, Suit.Spades)).thenReturn(mockCard)

      val result = deserializer.fromJson(json)
      result shouldBe mockCard

      verify(mockCardFactory).createCard(Value.King, Suit.Spades)
    }

    "deserialize a BoostedCard from JSON correctly" in {
      val json: JsObject = Json.obj(
        "type" -> "Boosted",
        "suit" -> "Diamonds",
        "value" -> "Jack",
        "additionalValue" -> 3
      )

      val mockBaseCard = mock[RegularCard]
      when(mockCardFactory.createCard(Value.Jack, Suit.Diamonds)).thenReturn(mockBaseCard)

      val result = deserializer.fromJson(json)

      result shouldBe a[BoostedCard]
      result.asInstanceOf[BoostedCard].additionalValue shouldBe 3

      verify(mockCardFactory).createCard(Value.Jack, Suit.Diamonds)
    }

    "throw an exception when deserializing from JSON with an invalid suit" in {
      val json: JsObject = Json.obj(
        "type" -> "Regular",
        "suit" -> "UnknownSuit",
        "value" -> "Queen"
      )

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromJson(json)
      }

      exception.getMessage should include("UnknownSuit")
    }

    "throw an exception when deserializing from JSON with an invalid value" in {
      val json: JsObject = Json.obj(
        "type" -> "Regular",
        "suit" -> "Hearts",
        "value" -> "InvalidValue"
      )

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromJson(json)
      }

      exception.getMessage should include("Invalid card value: InvalidValue")

    }
    "throw an exception when Boosted card is missing <additionalValue>" in {
      val xml: Elem =
        <card>
          <type>Boosted</type>
          <suit>Hearts</suit>
          <value>5</value>
        </card>

      val mockRegularCard = mock[RegularCard]
      when(mockCardFactory.createCard(Value.Five, Suit.Hearts)).thenReturn(mockRegularCard)

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("Missing or invalid <additionalValue>")
    }
    "throw an exception when <additionalValue> is not an integer" in {
      val xml: Elem =
        <card>
          <type>Boosted</type>
          <suit>Hearts</suit>
          <value>5</value>
          <additionalValue>abc</additionalValue>
        </card>

      val mockRegularCard = mock[RegularCard]
      when(mockCardFactory.createCard(Value.Five, Suit.Hearts)).thenReturn(mockRegularCard)

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("Missing or invalid <additionalValue>")
    }


    "throw an exception when deserializing from XML without type tag" in {
      val xml: Elem =
        <card>
          <suit>Hearts</suit>
          <value>Queen</value>
        </card>

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("Unknown card type")
    }

    "throw an exception when deserializing from XML with unknown type" in {
      val xml: Elem =
        <card>
          <type>UnknownType</type>
          <suit>Hearts</suit>
          <value>Queen</value>
        </card>

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      exception.getMessage should include("Unknown card type: UnknownType")
    }

    "throw an exception when deserializing from JSON without type field" in {
      val json: JsObject = Json.obj(
        "suit" -> "Clubs",
        "value" -> "9"
      )

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromJson(json)
      }

      exception.getMessage should include("'type' is undefined")
    }

    "throw an exception when deserializing from JSON with unknown card type" in {
      val json: JsObject = Json.obj(
        "type" -> "UltraCard",
        "suit" -> "Clubs",
        "value" -> "9"
      )

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromJson(json)
      }

      exception.getMessage should include("Unknown card type: UltraCard")
    }
  }
}