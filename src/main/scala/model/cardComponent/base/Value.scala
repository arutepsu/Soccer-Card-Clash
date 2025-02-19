package model.cardComponent.base

object Value {
  sealed trait Value extends Ordered[Value] {
    override def compare(that: Value): Int = Value.allValues.indexOf(this) - Value.allValues.indexOf(that)

    override def toString: String = Value.valueNames.getOrElse(this, "Unknown")
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
  
  val allValues: List[Value] = List(Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King)
  
  private val valueToIntMap: Map[Value, Int] = Map(
    Ace -> 14, Two -> 2, Three -> 3, Four -> 4, Five -> 5, Six -> 6,
    Seven -> 7, Eight -> 8, Nine -> 9, Ten -> 10, Jack -> 11, Queen -> 12, King -> 13
  )

  private val valueNames: Map[Value, String] = Map(
    Ace -> "Ace", Two -> "2", Three -> "3", Four -> "4", Five -> "5", Six -> "6",
    Seven -> "7", Eight -> "8", Nine -> "9", Ten -> "10", Jack -> "Jack", Queen -> "Queen", King -> "King"
  )
  
  def valueToInt(value: Value): Int = valueToIntMap.getOrElse(value, 0)

  def fromInt(n: Int): Option[Value] = allValues.find(valueToInt(_) == n)

  def fromString(s: String): Option[Value] = allValues.find(_.toString.equalsIgnoreCase(s))
}
