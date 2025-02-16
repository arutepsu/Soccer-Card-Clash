package model.playingFiledComponent.state.gameState

sealed trait GameState

object GameState {
  case object Attack extends GameState
  case object Refill extends GameState
  case object SwitchRoles extends GameState
  case object GameOver extends GameState
  case object Boost extends GameState
  case object Swap extends GameState
}
