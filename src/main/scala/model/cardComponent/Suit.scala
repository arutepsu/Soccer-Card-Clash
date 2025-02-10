package model.cardComponent

object Suit extends Enumeration {
  type Suit = Value
  val Hearts, Diamonds, Clubs, Spades = Value

  def suitToString(suit: Suit): String = suit match {
    case Hearts => "Hearts"
    case Diamonds => "Diamonds"
    case Clubs => "Clubs"
    case Spades => "Spades"
  }
}

