package view.tui

import controller.IController
import util.Observable

class TuiManager(controller: IController) extends Observable{

  private var currentTui: TuiBase = new TuiMainMenu(this, controller)

  /** Starts the TUI */
  def start(): Unit = {
    println("Welcome to the Soccer Card Game (TUI Mode)!")
    currentTui.run()
  }

  /** Switch TUI States */
  def switchTui(newTui: TuiBase): Unit = {
    currentTui = newTui
    currentTui.run()
  }
}

