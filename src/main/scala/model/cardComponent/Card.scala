package model.cardComponent
import scala.util.Random

import model.cardComponent.Value.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Seven, Six, Ten, Three, Two, Value}
import model.cardComponent.Suit.Suit

case class Card(value: Value, suit: Suit, formerValue: Option[Value] = None) {
  override def toString: String = s"${valueToString(value)} of ${Suit.suitToString(suit)}"

  def valueToString(value: Value): String = value match {
    case Ace => "Ace"
    case Two => "2"
    case Three => "3"
    case Four => "4"
    case Five => "5"
    case Six => "6"
    case Seven => "7"
    case Eight => "8"
    case Nine => "9"
    case Ten => "10"
    case Jack => "Jack"
    case Queen => "Queen"
    case King => "King"
  }

  def valueToInt(value: Value): Int = value match {
    case Ace => 14
    case Two => 2
    case Three => 3
    case Four => 4
    case Five => 5
    case Six => 6
    case Seven => 7
    case Eight => 8
    case Nine => 9
    case Ten => 10
    case Jack => 11
    case Queen => 12
    case King => 13
  }

  def compare(card1: Value, card2: Value): Int = {
    println(s"Comparing: ${valueToString(card1)} (${valueToInt(card1)}) vs ${valueToString(card2)} (${valueToInt(card2)})")

    (card1, card2) match {
      case (Two, Ace)  =>
        println("Special Rule: 2 beats Ace!")
        1  // ✅ 2 beats Ace
      case (Ace, Two)  =>
        println("Special Rule: Ace beats 2!")
        -1 // ✅ Ace beats 2
      case _ =>
        val result = valueToInt(card1) - valueToInt(card2)
        println(s"Standard Comparison Result: $result")
        result // Standard comparison
    }
  }

  def getFormerValue: Option[Value] = formerValue

  def setFormerValue(v: Value): Card = {
    this.copy(formerValue = Some(v))
  }

  def revertToFormerValue: Card = {
    this.formerValue match {
      case Some(originalValue) => this.copy(value = originalValue, formerValue = None)
      case None => this
    }
  }

  def getBoostingPolicies: Int = {
    val maxBoostLimit = valueToInt(Value.Ace) - valueToInt(value) // Maximum boost limit: Ace (14) - current value
    val boostAmount = Random.between(1, maxBoostLimit + 1) // Random boost value between 1 and max limit
    boostAmount
  }

  def fileName: String = {
    val valueStr = valueToString(value).toLowerCase.replace(" ", "_")
    val suitStr = Suit.suitToString(suit).toLowerCase
    s"${valueStr}_of_${suitStr}.png"
  }

}

//refill field cards, after defender is changed!