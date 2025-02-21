package model.cardComponent.base

import model.cardComponent.ICard
import model.cardComponent.base.components.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.components.Value.*
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.{Card, RegularCard}
import play.api.libs.json._
import scala.xml._
class BoostedCard(private val baseCard: RegularCard, var additionalValue: Int = 0)
  extends Card(baseCard.suit) {

  private val originalValue: Value = baseCard.value

  override def value: Value = Value.allValues
    .find(v => Value.valueToInt(originalValue) + additionalValue == Value.valueToInt(v))
    .getOrElse(originalValue)

  override def boost(): ICard = this

  override def revertBoost(): Card = new RegularCard(originalValue, baseCard.suit)

  override def copy(): Card = new BoostedCard(baseCard, additionalValue)

  override def toJson: JsObject = super.toJson ++ Json.obj(
    "type" -> JsString("Boosted"),
    "baseCard" -> baseCard.toJson,
    "additionalValue" -> additionalValue
  )

  override def toXml: Elem =
    <BoostedCard>
      <BaseCard>
        {baseCard.toXml}
      </BaseCard>
      <AdditionalValue>
        {additionalValue}
      </AdditionalValue>
    </BoostedCard>
}
