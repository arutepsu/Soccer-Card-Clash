package view.tui

enum TuiKeys(val key: String) {
  case StartGame extends TuiKeys(":start")
  case Quit extends TuiKeys(":quit")
  case Undo extends TuiKeys(":undo")
  case Redo extends TuiKeys(":redo")
  case Save extends TuiKeys(":save")
  case Load extends TuiKeys(":load")
  case Attack extends TuiKeys(":attack")
  case DoubleAttack extends TuiKeys(":double attack")
  case CreatePlayers extends TuiKeys(":create players")
  case RegularSwap extends TuiKeys(":regular swap")
  case BoostDefender extends TuiKeys(":boost defender")
  case Exit extends TuiKeys(":exit")
}