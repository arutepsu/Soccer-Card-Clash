package view.tui
import controller.IController
import util.Observer
trait TuiBase extends Observer {
  def run(): Unit

  /** ✅ Automatically update TUI state */
  override def update: Unit = {
    println("🔄 TUI Updating!")
    run() // ✅ Re-run the current TUI state when the game changes
  }
}
