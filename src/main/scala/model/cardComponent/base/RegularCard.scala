package model.cardComponent.base

import model.cardComponent.base.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import model.cardComponent.base.Value.{Value, *}
import model.cardComponent.base.{Card, Suit, Value}
import model.cardComponent.boosting.BoostingPolicies
import play.api.libs.json._
import scala.xml._
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
