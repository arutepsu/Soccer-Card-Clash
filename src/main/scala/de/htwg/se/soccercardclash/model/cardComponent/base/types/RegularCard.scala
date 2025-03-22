package de.htwg.se.soccercardclash.model.cardComponent.base.types

import de.htwg.se.soccercardclash.model.cardComponent.base.Card
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.{Clubs, Diamonds, Hearts, Spades, Suit}
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.cardComponent.boosting.BoostingPolicies
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
  
}
