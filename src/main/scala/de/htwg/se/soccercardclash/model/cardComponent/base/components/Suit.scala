package de.htwg.se.soccercardclash.model.cardComponent.base.components

object Suit extends Enumeration {
  type Suit = Value
  val Hearts, Diamonds, Clubs, Spades = Value
  val allSuits: List[Suit] = List(Hearts, Diamonds, Clubs, Spades)

  def fromString(s: String): Option[Suit] = allSuits.find(suitToString(_) == s)

  def suitToString(suit: Suit): String = suit match {
    case Hearts => "Hearts"
    case Diamonds => "Diamonds"
    case Clubs => "Clubs"
    case Spades => "Spades"
  }
}