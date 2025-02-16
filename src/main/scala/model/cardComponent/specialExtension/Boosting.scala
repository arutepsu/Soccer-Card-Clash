package model.cardComponent.specialExtension

import model.cardComponent.cardBaseImplementation.Value.*
import model.cardComponent.cardBaseImplementation.Value

object Boosting {
  // ✅ Predefined boost values per card rank
  private val boostValues: Map[Value, Int] = Map(
    Two   -> 6,  // Low cards get higher boosts
    Three -> 5,
    Four  -> 5,
    Five  -> 4,
    Six   -> 4,
    Seven -> 3,
    Eight -> 3,
    Nine  -> 2,
    Ten   -> 2,
    Jack  -> 1,  // Face cards get a smaller boost
    Queen -> 1,
    King  -> 1,
    Ace   -> 0   // Ace cannot be boosted
  )

  // ✅ Maximum allowed value (Ace = 14)
  val maxAllowedValue: Int = Value.valueToInt(Ace)

  /** ✅ Returns the predefined boost for a given card value */
  def getBoostAmount(value: Value): Int = boostValues.getOrElse(value, 0)

  /** ✅ Calculates the effective boost without exceeding Ace's value */
  def calculateBoostedValue(cardValue: Int): Int = {
    val boost = getBoostAmount(Value.fromInt(cardValue).getOrElse(Ace))
    Math.min(cardValue + boost, maxAllowedValue) // Ensure it doesn't exceed 14
  }
}
