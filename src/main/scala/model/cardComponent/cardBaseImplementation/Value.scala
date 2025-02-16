package model.cardComponent.cardBaseImplementation

object Value {
  sealed trait Value extends Ordered[Value] {
    override def compare(that: Value): Int = {
      Value.allValues.indexOf(this) - Value.allValues.indexOf(that)
    }
  }

  case object Ace extends Value
  case object Two extends Value
  case object Three extends Value
  case object Four extends Value
  case object Five extends Value
  case object Six extends Value
  case object Seven extends Value
  case object Eight extends Value
  case object Nine extends Value
  case object Ten extends Value
  case object Jack extends Value
  case object Queen extends Value
  case object King extends Value

  // List of all card values.
  val allValues: List[Value] = List(Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King)

  // ✅ New Method: Convert `Value` to Int
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

  def fromInt(n: Int): Option[Value] = allValues.find(valueToInt(_) == n)

  // ✅ Convert a string to a `Value`.
  def fromString(s: String): Option[Value] = allValues.find(_.toString.toLowerCase == s.toLowerCase)

  // ✅ Check if the card is a face card.
  def isFaceCard(value: Value): Boolean = value == Jack || value == Queen || value == King

  // ✅ Check if the card is a number card.
  def isNumberCard(value: Value): Boolean = valueToInt(value) >= 2 && valueToInt(value) <= 10
}