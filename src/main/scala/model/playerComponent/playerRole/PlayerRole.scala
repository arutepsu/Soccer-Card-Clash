package model.playerComponent.playerRole

sealed trait PlayerRole {
  override def toString: String = this match {
    case Attacker => "Attacker"
    case Defender => "Defender"
  }
}

case object Attacker extends PlayerRole
case object Defender extends PlayerRole
