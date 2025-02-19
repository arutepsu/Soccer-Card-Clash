package controller
import util.ObservableEvent

enum ControllerEvents extends ObservableEvent {
  // 🔹 Menu Events
  case MainMenu, CreatePlayer, LoadGame, Exit

  // 🔹 Create Player Events
  case EnterPlayer1Name, EnterPlayer2Name, StartGame, CreatePlayers
  case RegularAttack, DoubleAttack, RegularSwap, CircularSwap
  case BoostDefender, BoostGoalkeeper

  //Player Evenents
  case AttckerHandCards, AttackerDefenderCards, PlayingField
  // 🔹 Exit Game Events
  case Quit

  // 🔹 Other Actions
  case Undo, Redo, SaveGame
}
