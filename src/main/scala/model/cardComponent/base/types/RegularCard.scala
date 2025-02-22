package model.cardComponent.base.types

import model.cardComponent.base.Card
import model.cardComponent.base.components.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.components.Value.*
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.types.BoostedCard
import model.cardComponent.boosting.BoostingPolicies
import play.api.libs.json.*

import scala.xml.*
class RegularCard(initialValue: Value, override val suit: Suit) extends Card(suit) {

  private var _value: Value = initialValue

  def setValue(newValue: Value): Unit = _value = newValue

  override def boost(): Card = {
    val boostAmount = BoostingPolicies.getBoostAmount(this.value)
    new BoostedCard(this, boostAmount)
  }

  override def revertBoost(): Card = this

  override def copy(): Card = new RegularCard(value, suit)

  override def value: Value = _value

  override def toJson: JsObject = super.toJson + ("type" -> JsString("Regular"))

  override def toXml: Elem =
    <RegularCard>
      <Value>
        {value.toString}
      </Value>
      <Suit>
        {suit.toString}
      </Suit>
    </RegularCard>
}
