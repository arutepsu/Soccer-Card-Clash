package controller
import util.ObservableEvent

enum ControllerEvents extends ObservableEvent {
  // 🔹 Menu Events
  case MainMenu, CreatePlayer, LoadGame, Exit

  // 🔹 Create Player Events
  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, HandSwap, CircularSwap
  case BoostDefender, BoostGoalkeeper

  // 🔹 Exit Game Events
  case Quit

  // 🔹 Other Actions
  case Undo, Redo, SaveGame
}
