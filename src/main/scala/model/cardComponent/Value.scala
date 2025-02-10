package model.cardComponent

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

}
