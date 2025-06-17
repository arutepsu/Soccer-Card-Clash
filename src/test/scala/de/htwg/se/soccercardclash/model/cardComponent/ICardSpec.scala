package de.htwg.se.soccercardclash.model.cardComponent

import de.htwg.se.soccercardclash.model.cardComponent.base.components.*
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.xml.Utility.trim

class ICardSpec extends AnyFlatSpec with Matchers {

  "RegularCard" should "compare normally by value" in {
    val ten = RegularCard(Value.Ten, Suit.Hearts)
    val jack = RegularCard(Value.Jack, Suit.Hearts)

    ten.compare(jack) should be < 0
    jack.compare(ten) should be > 0
    jack.compare(jack) shouldBe 0
  }

  it should "treat Two > Ace specially" in {
    val two = RegularCard(Value.Two, Suit.Spades)
    val ace = RegularCard(Value.Ace, Suit.Spades)

    two.compare(ace) shouldBe 1
    ace.compare(two) shouldBe -1
  }

  it should "return the correct file name" in {
    val card = RegularCard(Value.Three, Suit.Clubs)
    card.fileName shouldBe "3_of_clubs.png"
  }

  it should "return the correct string representation" in {
    val card = RegularCard(Value.Queen, Suit.Diamonds)
    card.toString shouldBe "Queen of Diamonds"
  }

  it should "convert to correct JSON" in {
    val card = RegularCard(Value.Nine, Suit.Hearts)
    val expected = Json.obj(
      "suit" -> "Hearts",
      "value" -> "9",
      "type" -> "Regular"
    )

    card.toJson shouldBe expected
  }

  "BoostedCard" should "have increased valueToInt" in {
    val base = RegularCard(Value.Seven, Suit.Diamonds)
    val boosted = BoostedCard(base, 3)

    boosted.valueToInt shouldBe base.valueToInt + 3
  }

  it should "convert to correct JSON" in {
    val card = BoostedCard(RegularCard(Value.King, Suit.Spades), 1)
    val expected = Json.obj(
      "suit" -> "Spades",
      "value" -> "Ace",
      "type" -> "Boosted",
      "additionalValue" -> 1
    )

    card.toJson shouldBe expected
  }

  it should "revert to base card" in {
    val base = RegularCard(Value.Eight, Suit.Clubs)
    val boosted = BoostedCard(base, 4)

    boosted.revertBoost() shouldBe base
  }
  it should "convert to correct XML" in {
    val card = RegularCard(Value.Four, Suit.Spades)
    val xml = card.toXml

    (xml \ "suit").text.trim shouldBe "Spades"
    (xml \ "value").text.trim shouldBe "4"
    (xml \ "type").text.trim shouldBe "Regular"
    (xml \ "additionalValue").isEmpty shouldBe true
  }

  "BoostedCard" should "serialize correctly to XML" in {
    val base = RegularCard(Value.Jack, Suit.Clubs)
    val boosted = BoostedCard(base, 2)
    val xml = boosted.toXml

    (xml \ "suit").text.trim shouldBe "Clubs"
    (xml \ "value").text.trim shouldBe "King"
    (xml \ "type").text.trim shouldBe "Boosted"
    (xml \ "additionalValue").text.trim shouldBe "2"
  }

}
