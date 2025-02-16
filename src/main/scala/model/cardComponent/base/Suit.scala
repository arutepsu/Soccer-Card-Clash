package model.cardComponent.base

object Suit extends Enumeration {
  type Suit = Value
  val Hearts, Diamonds, Clubs, Spades = Value

  def suitToString(suit: Suit): String = suit match {
    case Hearts => "Hearts"
    case Diamonds => "Diamonds"
    case Clubs => "Clubs"
    case Spades => "Spades"
  }

  val allSuits: List[Suit] = List(Hearts, Diamonds, Clubs, Spades)

  // ✅ Convert a string to a `Suit` (useful for parsing).
  def fromString(s: String): Option[Suit] = allSuits.find(suitToString(_) == s)

  // ✅ Check if the suit is red or black.
  def isRed(suit: Suit): Boolean = suit == Hearts || suit == Diamonds

  def isBlack(suit: Suit): Boolean = suit == Clubs || suit == Spades
}