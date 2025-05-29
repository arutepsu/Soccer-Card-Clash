package de.htwg.se.soccercardclash.view.tui

enum TuiKeys(val key: String) {
  case SinglePlayer extends TuiKeys(":singleplayer")
  case Multiplayer extends TuiKeys(":multiplayer")
  case Quit extends TuiKeys(":quit")
  case Undo extends TuiKeys(":undo")
  case Redo extends TuiKeys(":redo")
  case Save extends TuiKeys(":save")
  case Attack extends TuiKeys(":attack")
  case DoubleAttack extends TuiKeys(":doubleattack")
  case RegularSwap extends TuiKeys(":regularswap")
  case ReverseSwap extends TuiKeys(":reverseswap")
  case BoostDefender extends TuiKeys(":boostdefender")
  case BoostGoalkeeper extends TuiKeys(":boostgoalkeeper")
  case Exit extends TuiKeys(":exit")
  case ShowGames extends TuiKeys(":load")
}