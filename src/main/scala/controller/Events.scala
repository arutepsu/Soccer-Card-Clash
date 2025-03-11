package controller
import util.ObservableEvent

enum Events extends ObservableEvent {

  case MainMenu, CreatePlayer, LoadGame, Exit

  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, RegularSwap, CircularSwap
  case BoostDefender, BoostGoalkeeper, Reverted

  case AttackerHandCards, AttackerDefenderCards, PlayingField

  case Quit
  
  case PauseGame, ResetGame
  
  case Undo, Redo, SaveGame
}
