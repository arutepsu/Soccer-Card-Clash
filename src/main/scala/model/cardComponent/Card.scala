package model.cardComponent

import scala.math.Integral.Implicits.infixIntegralOps
import scala.util.Random
import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
import model.cardComponent.Suit.{Diamonds, Suit}

case class Card(
                 value: Value,
                 suit: Suit,
                 additionalValue: Int = 0,
                 lastBoostValue: Int = 0,
                 wasBoosted: Boolean = false // ✅ New flag to prevent double boosting
               ) {

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
    baseValue + additionalValue // ✅ Always considers the boost!
  }

  def updateValue(): Card = {
    val newBaseValue = this.valueToInt // Get the total integer value (base + boost)
    val newValue = Value.allValues.find(v => Value.valueToInt(v) == newBaseValue).getOrElse(this.value)

    this.copy(value = newValue) // ✅ Return a new Card with updated `value`
  }

  def compare(card1: Card, card2: Card): Int = {
    val card1Value: Int = card1.valueToInt // Includes base + additionalValue
    val card2Value: Int = card2.valueToInt // Includes base + additionalValue

    println(s"Comparing: ${card1} ($card1Value) vs ${card2} ($card2Value)")

    (card1.value, card2.value) match {
      case (Value.Two, Value.Ace) =>
        println("Special Rule: 2 beats Ace!")
        1 // ✅ 2 beats Ace
      case (Value.Ace, Value.Two) =>
        println("Special Rule: Ace beats 2!")
        -1 // ✅ Ace beats 2
      case _ =>
        val result = card1Value - card2Value
        println(s"Standard Comparison Result: $result")
        result // Standard comparison
    }
  }

  def getAdditionalValue: Int = additionalValue

  /** ✅ Prevents double boosting */
  def setAdditionalValue(boost: Int): Card = {
    if (this.wasBoosted) { // ✅ Prevents applying a second boost
      println(s"⚠️ Boost prevented! ${this} has already been boosted once.")
      return this // ✅ Returns the same card without boosting again
    }

    println(s"old: ${this}")
    val newCard = this.copy(
      additionalValue = this.additionalValue + boost,
      lastBoostValue = boost, // ✅ Store last applied boost value
      wasBoosted = true // ✅ Marks the card as boosted
    )
    println(s"new: ${newCard}")
    newCard.updateValue()
  }

  def revertAdditionalValue(): Card = {
    if (this.wasBoosted) { // ✅ Reset only if the card was boosted
      println(s"🔄 Reverting boost for ${this}")
      this.copy(additionalValue = 0, lastBoostValue = 0, wasBoosted = false) // ✅ Resets the boost
    } else {
      this // ✅ Returns the same card if no boost was applied
    }
  }

  /** ✅ Ensures boost is generated only once */
  def getBoostingPolicies: Int = {
    if (wasBoosted) { // ✅ Prevents generating a new boost if already boosted
      println(s"⚠️ Boosting prevented! ${this} was already boosted.")
      return 0 // ✅ No additional boost allowed
    }

    val maxBoostLimit = Card(Ace, Diamonds).valueToInt - this.valueToInt
    val boost = Random.between(1, maxBoostLimit + 1)

    this.copy(lastBoostValue = boost, wasBoosted = true).lastBoostValue // ✅ Stores boost and marks as boosted
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
