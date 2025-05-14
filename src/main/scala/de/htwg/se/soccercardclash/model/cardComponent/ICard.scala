package de.htwg.se.soccercardclash.model.cardComponent

import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.Suit
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.*
import de.htwg.se.soccercardclash.util.Serializable
import play.api.libs.json.*

import scala.xml.*



trait ICard extends Serializable {
  def value: Value

  def suit: Suit

  def boost(): ICard

  def revertBoost(): ICard

  def compare(that: ICard): Int = (this.value, that.value) match {
    case (Value.Two, Value.Ace) => 1
    case (Value.Ace, Value.Two) => -1
    case _ => this.valueToInt.compare(that.valueToInt)
  }

  def valueToInt: Int = Value.valueToInt(value)

  def fileName: String =
    s"${value.toString.toLowerCase.replace(" ", "_")}_of_${Suit.suitToString(suit).toLowerCase}.png"

  override def toString: String =
    s"${value.toString} of ${Suit.suitToString(suit)}"

  def toXml: Elem = this match {
    case regularCard: RegularCard =>
      <Card>
        <suit>
          {suit}
        </suit>
        <value>
          {regularCard.value}
        </value>
        <type>Regular</type>
      </Card>

    case boostedCard: BoostedCard =>
      <Card>
        <suit>
          {suit}
        </suit>
        <value>
          {boostedCard.value}
        </value>
        <type>Boosted</type>
        <additionalValue>
          {boostedCard.additionalValue}
        </additionalValue>
      </Card>
  }

  def toJson: JsObject = this match {
    case regularCard: RegularCard =>
      Json.obj(
        "suit" -> suit.toString,
        "value" -> regularCard.value.toString,
        "type" -> "Regular"
      )

    case boostedCard: BoostedCard =>
      Json.obj(
        "suit" -> suit.toString,
        "value" -> boostedCard.value.toString,
        "type" -> "Boosted",
        "additionalValue" -> boostedCard.additionalValue
      )
  }
}
