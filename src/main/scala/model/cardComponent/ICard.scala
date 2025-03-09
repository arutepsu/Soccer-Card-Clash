package model.cardComponent
import model.cardComponent.base.components.Suit.Suit
import model.cardComponent.base.components.Value.Value
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import util.Serializable
import play.api.libs.json.*

import scala.xml.*


trait ICard extends Serializable {
  def value: Value
  def suit: Suit
  def boost(): ICard
  def revertBoost(): ICard
  def valueToInt: Int
  def compare(that: ICard): Int
  def fileName: String
  def copy(): ICard
  def hashCode(): Int
  def equals(obj: Any): Boolean

//  def toJson: JsObject
//
//  def toXml: Elem

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
