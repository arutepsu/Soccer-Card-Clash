package model.cardComponent.boosting

import model.cardComponent.base.components.Value.*
import model.cardComponent.base.components.Value

object BoostingPolicies {

  val maxAllowedValue: Int = Value.valueToInt(Value.Ace)
  private val boostValues: Map[Value, Int] = Map(
    Two -> 6, Three -> 5, Four -> 5, Five -> 4, Six -> 4,
    Seven -> 3, Eight -> 3, Nine -> 2, Ten -> 2,
    Jack -> 1, Queen -> 1, King -> 1, Ace -> 0
  )

  def getBoostAmount(value: Value): Int = boostValues.getOrElse(value, 0)
}
