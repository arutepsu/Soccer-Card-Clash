package model.cardComponent.base

import Suit.{Diamonds, Suit}
import Value.*
import model.cardComponent.ICard
import model.cardComponent.*
import model.cardComponent.boosting.BoostingPolicies

import scala.math.Integral.Implicits.infixIntegralOps
import scala.util.Random

case class Card private (
                 var value: Value,
                 val suit: Suit,
                 var additionalValue: Int = 0,
                 var lastBoostValue: Int = 0,
                 var wasBoosted: Boolean = false
               ) extends ICard{

  private var originalValue: Value = value


  override def toString: String =
    s"${valueToString(value)} of ${Suit.suitToString(suit)} with addVal = ${additionalValue} (Last Boost: ${lastBoostValue})"

  override def valueToString(value: Value): String = value match {
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

  override def valueToInt: Int = {
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
    baseValue
  }

  override def compare(card1: ICard, card2: ICard): Int = {
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

  override def getAdditionalValue: Int = additionalValue

  override def setAdditionalValue(boost: Int): Card = {
    this.originalValue = this.value
    this.additionalValue += boost
    this.lastBoostValue = boost
    this.wasBoosted = true
    this.updateValue()
    println(s"new: ${this}")

    this
  }


  override def updateValue(): Unit = {
    val oldValueInt = Value.valueToInt(this.value)
    val newBaseValue = oldValueInt + this.additionalValue

    val newValue = Value.allValues
      .find(v => Value.valueToInt(v) == newBaseValue)
      .orElse(Value.allValues.sortBy(v => Math.abs(Value.valueToInt(v) - newBaseValue)).headOption)
      .getOrElse(this.value)

    if (newValue != this.value) {
      println(s"Updating card value from ${this.value} to $newValue")
      this.value = newValue
    }
  }

  override def revertAdditionalValue(): Card = {
    if (this.wasBoosted) {
      println(s"🔄 Reverting boost for ${this}")
      val newBaseValue = Value.valueToInt(this.value) - this.additionalValue

      val newValue = Value.allValues
        .find(v => Value.valueToInt(v) == newBaseValue)
        .getOrElse(this.originalValue)

      this.value = newValue
      this.additionalValue = 0
      this.wasBoosted = false

      println(s"🔄 Boost Reverted: ${this}")
    }
    this
  }
  override def getBoostingPolicies: Int = {
    if (wasBoosted) {
      println(s"⚠️ Boosting prevented! ${this} was already boosted.")
      return 0
    }

    val boost = BoostingPolicies.getBoostAmount(this.value) // ✅ Get predefined boost

    val boostedValue = Math.min(Value.valueToInt(this.value) + boost, BoostingPolicies.maxAllowedValue)

    val finalBoost = boostedValue - Value.valueToInt(this.value) // ✅ Ensure correct boost applied

    this.lastBoostValue = finalBoost
    this.wasBoosted = true

    println(s"✅ Boosting ${this}: +$finalBoost (New Value: $boostedValue)")

    finalBoost
  }

//  def getBoostingPolicies: Int = {
//    if (wasBoosted) {
//      println(s"⚠️ Boosting prevented! ${this} was already boosted.")
//      return 0
//    }
//
//    val maxBoostLimit = new Card(Ace, Diamonds).valueToInt - this.valueToInt
//    val boost = Random.between(1, maxBoostLimit + 1)
//
//    this.lastBoostValue = boost
//    this.wasBoosted = true
//    boost
//  }

  override def fileName: String = {
    val valueStr = valueToString(value).toLowerCase.replace(" ", "_")
    val suitStr = Suit.suitToString(suit).toLowerCase
    s"${valueStr}_of_${suitStr}.png"
  }

  override def copy(): Card = new Card(
    value = this.value,
    suit = this.suit,
    additionalValue = this.additionalValue,
    lastBoostValue = this.lastBoostValue,
    wasBoosted = this.wasBoosted
  )
}

/** ✅ Companion Object (No `copy` Here) */
  object Card {
    private[cardComponent] def apply(value: Value, suit: Suit): Card =
      new Card(value, suit)
  }
