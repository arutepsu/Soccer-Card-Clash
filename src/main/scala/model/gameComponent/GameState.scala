package model.gameComponent

object GameState extends Enumeration {
  type GameState = Value
  val NotStarted, Initialized, InAction, Ended = Value
}

