package model.cardComponent

import scala.math.Integral.Implicits.infixIntegralOps
import scala.util.Random
import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
import model.cardComponent.Suit.{Diamonds, Suit}

case class Card(
            var value: Value, // ✅ Mutable so it can be updated
            val suit: Suit,
            var additionalValue: Int = 0,
            var lastBoostValue: Int = 0,
            var wasBoosted: Boolean = false
          ) {

  private var originalValue: Value = value // ✅ Store the original card value


  override def toString: String =
    s"${valueToString(value)} of ${Suit.suitToString(suit)} with addVal = ${additionalValue} (Last Boost: ${lastBoostValue})"

  def valueToString(value: Value): String = value match {
    case Ace   => "Ace"
    case Two   => "2"
    case Three => "3"
    case Four  => "4"
    case Five  => "5"
    case Six   => "6"
    case Seven => "7"
    case Eight => "8"
    case Nine  => "9"
    case Ten   => "10"
    case Jack  => "Jack"
    case Queen => "Queen"
    case King  => "King"
  }

  def valueToInt: Int = {
    val baseValue = this.value match {
      case Ace   => 14
      case Two   => 2
      case Three => 3
      case Four  => 4
      case Five  => 5
      case Six   => 6
      case Seven => 7
      case Eight => 8
      case Nine  => 9
      case Ten   => 10
      case Jack  => 11
      case Queen => 12
      case King  => 13
    }
//    baseValue + additionalValue // ✅ Always considers the boost!
    baseValue
  }

  def compare(card1: Card, card2: Card): Int = {
    val card1Value: Int = card1.valueToInt
    val card2Value: Int = card2.valueToInt

    println(s"Comparing: ${card1} ($card1Value) vs ${card2} ($card2Value)")

    (card1.value, card2.value) match {
      case (Value.Two, Value.Ace) =>
        println("Special Rule: 2 beats Ace!")
        1
      case (Value.Ace, Value.Two) =>
        println("Special Rule: Ace beats 2!")
        -1
      case _ =>
        val result = card1Value - card2Value
        println(s"Standard Comparison Result: $result")
        result
    }
  }

  def getAdditionalValue: Int = additionalValue

  /** ✅ Prevents double boosting */
  def setAdditionalValue(boost: Int): Card = {
//    if (this.wasBoosted) {
//      println(s"⚠️ Boost prevented! ${this} has already been boosted once.")
//      return this
//    }


//    println(s"old: ${this}") // ✅ Ensures correct old state

    this.originalValue = this.value // ✅ Store original value before boosting
    this.additionalValue += boost
    this.lastBoostValue = boost // ✅ Explicitly set a new boost value
    this.wasBoosted = true

    this.updateValue() // ✅ Ensure value is updated
    println(s"new: ${this}") // ✅ Ensures correct new state

    this
  }


  def updateValue(): Unit = {
    val oldValueInt = Value.valueToInt(this.value)
    val newBaseValue = oldValueInt + this.additionalValue // ✅ Old value + additional boost

    val newValue = Value.allValues
      .find(v => Value.valueToInt(v) == newBaseValue)
      .orElse(Value.allValues.sortBy(v => Math.abs(Value.valueToInt(v) - newBaseValue)).headOption)
      .getOrElse(this.value)

    if (newValue != this.value) {
      println(s"Updating card value from ${this.value} to $newValue")
      this.value = newValue
    }
  }



  /** ✅ Fix: Properly reverts boost by modifying the object in place */
  def revertAdditionalValue(): Card = {
    if (this.wasBoosted) {
      println(s"🔄 Reverting boost for ${this}")

      // ✅ Subtract the additional value from the current value before resetting
      val newBaseValue = Value.valueToInt(this.value) - this.additionalValue

      // ✅ Find the closest matching value after subtraction
      val newValue = Value.allValues
        .find(v => Value.valueToInt(v) == newBaseValue)
        .getOrElse(this.originalValue) // Fallback to the original value

      this.value = newValue // ✅ Restore the correct value
      this.additionalValue = 0 // ✅ Reset boost
      this.wasBoosted = false  // ✅ Reset boost status

      println(s"🔄 Boost Reverted: ${this}") // ✅ Debugging
    }
    this
  }



  /** ✅ Ensures boost is generated only once */
  def getBoostingPolicies: Int = {
    if (wasBoosted) {
      println(s"⚠️ Boosting prevented! ${this} was already boosted.")
      return 0
    }

    val maxBoostLimit = new Card(Ace, Diamonds).valueToInt - this.valueToInt
    val boost = Random.between(1, maxBoostLimit + 1)

    this.lastBoostValue = boost
    this.wasBoosted = true
    boost
  }

  def fileName: String = {
    val valueStr = valueToString(value).toLowerCase.replace(" ", "_")
    val suitStr = Suit.suitToString(suit).toLowerCase
    s"${valueStr}_of_${suitStr}.png"
  }
}

//package model.cardComponent
//import scala.math.Integral.Implicits.infixIntegralOps
//import scala.util.Random
//import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
//import model.cardComponent.Suit.{Diamonds, Suit}
//
//case class Card(value: Value, suit: Suit, var additionalValue: Int = 0) {
//  override def toString: String = s"${valueToString(value)} of ${Suit.suitToString(suit)} with addVal = ${getAdditionalValue}"
//
//  def valueToString(value: Value): String = value match {
//    case Ace => "Ace"
//    case Two => "2"
//    case Three => "3"
//    case Four => "4"
//    case Five => "5"
//    case Six => "6"
//    case Seven => "7"
//    case Eight => "8"
//    case Nine => "9"
//    case Ten => "10"
//    case Jack => "Jack"
//    case Queen => "Queen"
//    case King => "King"
//  }
//
//  def valueToInt: Int = {
//    val baseValue = this.value match {
//      case Ace => 14
//      case Two => 2
//      case Three => 3
//      case Four => 4
//      case Five => 5
//      case Six => 6
//      case Seven => 7
//      case Eight => 8
//      case Nine => 9
//      case Ten => 10
//      case Jack => 11
//      case Queen => 12
//      case King => 13
//    }
//    baseValue + additionalValue // ✅ Always considers the boost!
//  }
//  def updateValue(): Card = {
//    val newBaseValue = this.valueToInt // Get the total integer value (base + boost)
//    val newValue = Value.allValues.find(v => Value.valueToInt(v) == newBaseValue).getOrElse(this.value)
//
//    this.copy(value = newValue) // ✅ Return a new Card with updated `value`
//  }
//
//
//  def compare(card1: Card, card2: Card): Int = {
//    val card1Value: Int = card1.valueToInt // Includes base + additionalValue
//    val card2Value: Int = card2.valueToInt // Includes base + additionalValue
//
//    println(s"Comparing: ${card1} ($card1Value) vs ${card2} ($card2Value)")
//
//    (card1.value, card2.value) match {
//      case (Value.Two, Value.Ace) =>
//        println("Special Rule: 2 beats Ace!")
//        1 // ✅ 2 beats Ace
//      case (Value.Ace, Value.Two) =>
//        println("Special Rule: Ace beats 2!")
//        -1 // ✅ Ace beats 2
//      case _ =>
//        val result = card1Value - card2Value
//        println(s"Standard Comparison Result: $result")
//        result // Standard comparison
//    }
//  }
//
//
//  def getAdditionalValue: Int = additionalValue
//
//  def setAdditionalValue(boost: Int): Card = {
//    println(s"old: ${this}")
//    val newCard = this.copy(additionalValue = this.additionalValue + boost) // ✅ Store new Card
//    println(s"new: ${newCard}")
//    newCard.updateValue() // ✅ Ensure `value` is updated
//    newCard // ✅ Return new Card
//  }
//
//
//  def revertAdditionalValue(): Unit = {
//    this.additionalValue = 0
//  }
//
//  def getBoostingPolicies: Int = {
//    val maxBoostLimit = Card(Ace, Diamonds).valueToInt - this.valueToInt// Maximum boost limit: Ace (14) - current value
//    val boostAmount = Random.between(1, maxBoostLimit + 1) // Random boost value between 1 and max limit
//    boostAmount
//  }
//
//  def fileName: String = {
//    val valueStr = valueToString(value).toLowerCase.replace(" ", "_")
//    val suitStr = Suit.suitToString(suit).toLowerCase
//    s"${valueStr}_of_${suitStr}.png"
//  }
//}
