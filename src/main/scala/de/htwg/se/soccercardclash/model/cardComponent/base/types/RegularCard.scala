package de.htwg.se.soccercardclash.model.cardComponent.base.types

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.Suit
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.Value
import de.htwg.se.soccercardclash.model.cardComponent.boosting.BoostingPolicies

case class RegularCard(value: Value, suit: Suit) extends ICard {
  override def boost(): ICard = BoostedCard(this, BoostingPolicies.getBoostAmount(value))

  override def revertBoost(): ICard = this

}
